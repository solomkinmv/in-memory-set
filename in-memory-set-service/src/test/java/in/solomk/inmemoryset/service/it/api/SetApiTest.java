package in.solomk.inmemoryset.service.it.api;

import in.solomk.inmemoryset.service.it.BaseIntegrationTest;
import in.solomk.inmemoryset.service.it.client.ServiceTestClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class SetApiTest extends BaseIntegrationTest {

    @Autowired
    private ServiceTestClient serviceTestClient;

    @Nested
    class HasItemTests {
        @Test
        void givenEmptySet_whenCheckHasItem_thenReturnsNotFound() {
            serviceTestClient.hasItem("123")
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .isEmpty();
        }

        @Test
        @Disabled
        void givenSetWithItem_whenCheckHasItem_thenReturnsOk() {
            // todo: implement
//            serviceTestClient.addItem("123")
//                    .expectStatus()
//                    .isOk();

            serviceTestClient.hasItem("123")
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .isEmpty();
        }

        @Test
        void givenNonIntegerItemValue_whenCheckHasItem_thenReturnsBadRequest() {
            serviceTestClient.hasItem("notAnInteger")
                    .expectStatus()
                    .isBadRequest()
                    .expectBody()
                    .json("""
                            {
                                "message": "Item value should be an integer: 'notAnInteger'"
                            }
                            """)
                    .jsonPath("$.incidentId").isNotEmpty()
                    .jsonPath("$.error").isNotEmpty()
                    .jsonPath("$.path").isNotEmpty()
                    .jsonPath("$.requestId").isNotEmpty()
                    .jsonPath("$.timestamp").isNotEmpty()
                    .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
