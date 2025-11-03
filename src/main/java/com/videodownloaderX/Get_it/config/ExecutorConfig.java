package com.videodownloaderX.Get_it.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean(name = "downloadExecutor")
    public ExecutorService downloadExecutor() {
        return new ThreadPoolExecutor(
                0,
                2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Bean(name = "scheduledExecutor")
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2);
    }

    @Bean
    public Semaphore rateLimitSemaphore() {
        return new Semaphore(2);
    }
}
