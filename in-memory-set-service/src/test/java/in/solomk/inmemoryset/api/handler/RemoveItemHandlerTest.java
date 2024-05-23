package in.solomk.inmemoryset.api.handler;

import in.solomk.inmemoryset.exception.BadRequestServiceException;
import in.solomk.inmemoryset.service.InMemorySetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class RemoveItemHandlerTest {
    private InMemorySetService inMemorySetService;
    private RemoveItemHandler removeItemHandler;

    @BeforeEach
    void setUp() {
        inMemorySetService = Mockito.mock(InMemorySetService.class);
        removeItemHandler = new RemoveItemHandler(inMemorySetService);
    }

    @Test
    void givenItemRemoved_whenHandle_thenReturnsOk() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "1")
                .build();
        when(inMemorySetService.removeItem(1)).thenReturn(true);

        Mono<ServerResponse> response = removeItemHandler.handle(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void givenItemNotRemoved_whenHandle_thenReturnsOk() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "1")
                .build();
        when(inMemorySetService.removeItem(1)).thenReturn(true);

        Mono<ServerResponse> response = removeItemHandler.handle(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void givenNonIntegerItemValue_whenHandle_thenReturnsBadRequest() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "notAnInteger")
                .build();

        Mono<ServerResponse> response = removeItemHandler.handle(request);

        StepVerifier.create(response)
                .verifyError(BadRequestServiceException.class);
    }
}
