package in.solomk.inmemoryset.api;

import in.solomk.inmemoryset.api.handler.AddItemHandler;
import in.solomk.inmemoryset.api.handler.HasItemHandler;
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
            HasItemHandler hasItemHandler,
            AddItemHandler addItemHandler) {

        return RouterFunctions.route()
                .GET("/items/{itemValue}", hasItemHandler)
                .POST("/items/{itemValue}", addItemHandler)
                .build();

    }

}
