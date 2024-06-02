package in.solomk;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BasicSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http.baseUrl(System.getProperty("host", "http://localhost:8080"))
            .acceptHeader("application/json"); // Here are the common headers

    private final ChainBuilder generateSession = exec(session -> {
        int randomValue = new java.util.Random().nextInt(1000);
        return session.set("value", randomValue);
    });

    private final ChainBuilder addItemRequest = exec(http("Add Request")
            .put(session -> "/items/" + session.getInt("value"))
            .asJson());

    private final ChainBuilder removeItemRequest = exec(http("Remove Request")
            .delete(session -> "/items/" + session.getInt("value"))
            .asJson());

    private final ChainBuilder checkItemRequest = exec(http("Check Request")
            .get(session -> "/items/" + session.getInt("value"))
            .asJson()
            .check(status().in(200, 404)));

    private final ScenarioBuilder checkOnlyScenario = scenario("Check Only")
            .repeat(100)
            .on(generateSession,
                    checkItemRequest.pause(Duration.ofMillis(10)));

    private final ScenarioBuilder addCheckRemoveScenario = scenario("Add Check Remove")
            .repeat(100)
            .on(generateSession,
                    addItemRequest.pause(Duration.ofMillis(5)),
                    checkItemRequest.pause(Duration.ofMillis(10)),
                    removeItemRequest.pause(Duration.ofMillis(5)));


    public BasicSimulation() {
        setUp(
                checkOnlyScenario.injectOpen(constantUsersPerSec(10).during(60)),
                addCheckRemoveScenario.injectOpen(rampUsers(2000).during(60))
        ).protocols(httpProtocol);
    }
}
