package in.solomk;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BasicSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080") // Here is the root for all relative URLs
            .acceptHeader("application/json"); // Here are the common headers

    ScenarioBuilder addScenario = scenario("Add Scenario") // A scenario is a chain of requests and pauses
            .exec(session -> {
                int randomValue = new java.util.Random().nextInt(1000);
                return session.set("value", randomValue);
            })
            .exec(http("Add Request")
                    .post(session -> "/items/" + session.getInt("value"))
                    .asJson())
            .pause(5); // Note that Gatling has recorded real time pauses

    ScenarioBuilder removeScenario = scenario("Remove Scenario") // A scenario is a chain of requests and pauses
            .exec(session -> {
                int randomValue = new java.util.Random().nextInt(1000);
                return session.set("value", randomValue);
            })
            .exec(http("Remove Request")
                    .delete(session -> "/items/" + session.getInt("value"))
                    .asJson())
            .pause(5); // Note that Gatling has recorded real time pauses

    ScenarioBuilder checkScenario = scenario("Check Scenario") // A scenario is a chain of requests and pauses
            .exec(session -> {
                int randomValue = new java.util.Random().nextInt(1000);
                return session.set("value", randomValue);
            })
            .exec(http("Check Request")
                    .get(session -> "/items/" + session.getInt("value"))
                    .asJson())
            .pause(5); // Note that Gatling has recorded real time pauses


    public BasicSimulation() {
        setUp(
                addScenario.injectOpen(atOnceUsers(1000)),
                removeScenario.injectOpen(atOnceUsers(1000)),
                checkScenario.injectOpen(atOnceUsers(1000))
        ).protocols(httpProtocol);
    }
}
