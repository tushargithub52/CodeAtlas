package codeatlas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

/**
 * General application configuration — registers the RestClient builder and async indexing thread pool.
 */
@Configuration
@EnableAsync
public class AppConfig {

    /**
     * Provides a shared RestClient.Builder bean for making HTTP calls to external APIs.
     */
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Thread pool executor used for async repository indexing tasks.
     */
    @Bean(name = "Indexing Executor")
    Executor indexingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("index-");
        executor.initialize();
        return executor;
    }
}
