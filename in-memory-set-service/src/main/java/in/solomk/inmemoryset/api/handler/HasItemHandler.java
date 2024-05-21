package in.solomk.inmemoryset.api.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class HasItemHandler implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var itemValue = request.pathVariable("itemValue");
        if (itemValue.equals("item")) {
            return ServerResponse.ok()
                    .build();
        }
        return ServerResponse.notFound()
                .build();
    }
}
