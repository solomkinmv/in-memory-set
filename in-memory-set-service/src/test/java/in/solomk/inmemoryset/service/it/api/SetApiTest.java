package in.solomk.inmemoryset.service.it.api;

import in.solomk.inmemoryset.service.it.BaseIntegrationTest;
import in.solomk.inmemoryset.service.it.client.ServiceTestClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SetApiTest extends BaseIntegrationTest {

    @Autowired
    private ServiceTestClient serviceTestClient;

    @Nested
    class HasItemTests {
        @Test
        void returnsFalseWhenItemIsNotPresent() {
            serviceTestClient.hasItem("item1")
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .isEmpty();
        }

        @Test
        void returns200ResponseWhenItemIsPresent() {
            serviceTestClient.hasItem("item")
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .isEmpty();
        }
    }
}
