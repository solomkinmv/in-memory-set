package in.solomk.inmemoryset.api;

import in.solomk.inmemoryset.api.dto.OperationResponse;
import in.solomk.inmemoryset.api.handler.AddItemHandler;
import in.solomk.inmemoryset.api.handler.HasItemHandler;
import in.solomk.inmemoryset.api.handler.RemoveItemHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
@RegisterReflectionForBinding({OperationResponse.class})
public class RouteConfiguration {

    @Bean
    RouterFunction<ServerResponse> routerFunction(
            HasItemHandler hasItemHandler,
            AddItemHandler addItemHandler,
            RemoveItemHandler removeItemHandler) {

        return RouterFunctions.route()
                .GET("/items/{itemValue}", hasItemHandler)
                .POST("/items/{itemValue}", addItemHandler)
                .DELETE("/items/{itemValue}", removeItemHandler)
                .build();

    }

}
