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

class AddItemHandlerTest {
    private InMemorySetService inMemorySetService;
    private AddItemHandler addItemHandler;

    @BeforeEach
    void setUp() {
        inMemorySetService = Mockito.mock(InMemorySetService.class);
        addItemHandler = new AddItemHandler(inMemorySetService);
    }

    @Test
    void givenItemAdded_whenHandle_thenReturnsOk() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "1")
                .build();
        when(inMemorySetService.addItem(1)).thenReturn(true);

        Mono<ServerResponse> response = addItemHandler.handle(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void givenItemNotAdded_whenHandle_thenReturnsOk() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "1")
                .build();
        when(inMemorySetService.addItem(1)).thenReturn(true);

        Mono<ServerResponse> response = addItemHandler.handle(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void givenNonIntegerItemValue_whenHandle_thenReturnsBadRequest() {
        var request = MockServerRequest.builder()
                .pathVariable("itemValue", "notAnInteger")
                .build();

        Mono<ServerResponse> response = addItemHandler.handle(request);

        StepVerifier.create(response)
                .verifyError(BadRequestServiceException.class);
    }
}
