package in.solomk.inmemoryset.service.api;

import in.solomk.inmemoryset.service.api.handler.HasItemHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
public class RouteConfiguration {

    @Bean
    RouterFunction<ServerResponse> routerFunction(
            HasItemHandler hasItemHandler) {

        return RouterFunctions.route()
                .GET("/items/{itemValue}", hasItemHandler)
                .build();

    }

}
