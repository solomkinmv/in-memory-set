package in.solomk.inmemoryset.it.api;

import in.solomk.inmemoryset.it.BaseIntegrationTest;
import in.solomk.inmemoryset.it.client.ServiceTestClient;
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
            serviceTestClient.hasItem(nextItemValueAsString())
                    .expectStatus()
                    .isNotFound()
                    .expectBody()
                    .isEmpty();
        }

        @Test
        @Disabled
        void givenSetWithItem_whenCheckHasItem_thenReturnsOk() {
            var itemValue = nextItemValueAsString();
            serviceTestClient.addItem(itemValue)
                    .expectStatus()
                    .isOk();

            serviceTestClient.hasItem(itemValue)
                    .expectStatus()
                    .isOk();
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

    @Nested
    class AddItemTests {
        @Test
        void givenEmptySet_whenAddItem_thenReturnsOk() {
            serviceTestClient.addItem(nextItemValueAsString())
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                                "status": "SUCCESS"
                            }
                            """);
        }

        @Test
        void givenSetWithItem_whenAddItem_thenReturnsOk() {
            var itemValue = nextItemValueAsString();
            serviceTestClient.addItem(itemValue)
                    .expectStatus()
                    .isOk();

            serviceTestClient.addItem(itemValue)
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                                "status": "FAILURE"
                            }
                            """);
        }

        @Test
        void givenNonIntegerItemValue_whenAddItem_thenReturnsBadRequest() {
            serviceTestClient.addItem("notAnInteger")
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
