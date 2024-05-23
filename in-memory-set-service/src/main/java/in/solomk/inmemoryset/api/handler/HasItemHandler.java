package in.solomk.inmemoryset.api.handler;

import in.solomk.inmemoryset.exception.BadRequestServiceException;
import in.solomk.inmemoryset.service.InMemorySetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class HasItemHandler implements HandlerFunction<ServerResponse> {

    private final InMemorySetService inMemorySetService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var itemValue = request.pathVariable("itemValue");
        try {
            int intItem = Integer.parseInt(itemValue);
            boolean result = inMemorySetService.hasItem(intItem);
            if (result) {
                return ServerResponse.ok().build();
            }
            return ServerResponse.notFound().build();
        } catch (NumberFormatException e) {
            return Mono.error(new BadRequestServiceException(e, "Item value should be an integer: '%s'", itemValue));
        }
    }
}
