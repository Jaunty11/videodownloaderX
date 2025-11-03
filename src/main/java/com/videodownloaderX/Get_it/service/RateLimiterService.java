package com.videodownloaderX.Get_it.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;

    @Value("${videodownloader.rate-limit.delay-ms:500}")
    private long delayMs;

    @Autowired
    public RateLimiterService(
            Semaphore rateLimitSemaphore,
            @Qualifier("scheduledExecutor") ScheduledExecutorService scheduler) {
        this.semaphore = rateLimitSemaphore;
        this.scheduler = scheduler;
    }

    public void executeWithRateLimit(Runnable task) throws InterruptedException {
        // Acquire permit (blocks if none available)
        semaphore.acquire();

        try {
            // Execute the download task
            task.run();
        } finally {
            // Release permit after delay (creates human-like pattern)
            scheduler.schedule(
                    () -> semaphore.release(),
                    delayMs,
                    TimeUnit.MILLISECONDS
            );
        }
    }
}
