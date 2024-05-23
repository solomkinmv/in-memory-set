package in.solomk.inmemoryset.config;

import in.solomk.inmemoryset.service.HashTable;
import in.solomk.inmemoryset.service.InMemorySetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public InMemorySetService inMemorySetService() {
        // todo: make this configurable with properties
        return new InMemorySetService(new HashTable(16, 0.75, 2, 0.25));
    }
}
