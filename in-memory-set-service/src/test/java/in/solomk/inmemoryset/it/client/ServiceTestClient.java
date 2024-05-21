package in.solomk.inmemoryset.it.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Component
@AllArgsConstructor
public class ServiceTestClient {

    private final WebTestClient webTestClient;

    public WebTestClient.ResponseSpec hasItem(String itemValue) {
        return webTestClient.get()
                            .uri("/items/" + itemValue)
                            .exchange();
    }
}
