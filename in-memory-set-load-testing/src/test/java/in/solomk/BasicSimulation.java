package in.solomk;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BasicSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080") // Here is the root for all relative URLs
            .acceptHeader("application/json"); // Here are the common headers

    ChainBuilder generateSession = exec(session -> {
        int randomValue = new java.util.Random().nextInt(1000);
        return session.set("value", randomValue);
    });

    ChainBuilder addItemRequest = exec(http("Add Request")
            .post(session -> "/items/" + session.getInt("value"))
            .asJson());

    ChainBuilder removeItemRequest = exec(http("Remove Request")
            .delete(session -> "/items/" + session.getInt("value"))
            .asJson());

    ChainBuilder checkItemRequest = exec(http("Check Request")
            .get(session -> "/items/" + session.getInt("value"))
            .asJson()
            .check(status().in(200, 404)));

    ScenarioBuilder checkOnlyScenario = scenario("Check Only")
            .repeat(100)
            .on(generateSession,
                    checkItemRequest.pause(Duration.ofMillis(10)));

    ScenarioBuilder addCheckRemoveScenario = scenario("Add Check Remove")
            .repeat(100)
            .on(generateSession,
                    addItemRequest.pause(Duration.ofMillis(5)),
                    checkItemRequest.pause(Duration.ofMillis(10)),
                    removeItemRequest.pause(Duration.ofMillis(5)));


    public BasicSimulation() {
        setUp(
                checkOnlyScenario.injectOpen(constantUsersPerSec(1000).during(60)),
                addCheckRemoveScenario.injectOpen(constantUsersPerSec(1000).during(60))
        ).protocols(httpProtocol);
    }
}
