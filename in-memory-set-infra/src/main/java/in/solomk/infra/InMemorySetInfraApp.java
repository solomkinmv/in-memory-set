package in.solomk.infra;

import software.amazon.awscdk.App;

public final class InMemorySetInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        new InMemorySetInfraStack(app, "InMemorySetInfraStack");

        app.synth();
    }
}
