package in.solomk.inmemoryset.it.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@Component
@AllArgsConstructor
public class ServiceTestClient {

    private final WebTestClient webTestClient;

    public ResponseSpec hasItem(String itemValue) {
        return webTestClient.get()
                .uri("/items/" + itemValue)
                .exchange();
    }

    public ResponseSpec addItem(String itemValue) {
        return webTestClient.put()
                .uri("/items/" + itemValue)
                .exchange();
    }

    public ResponseSpec removeItem(String itemValue) {
        return webTestClient.delete()
                .uri("/items/" + itemValue)
                .exchange();
    }
}
