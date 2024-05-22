package in.solomk;

import in.solomk.infra.InMemorySetInfraStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class InMemorySetInfraStackTest {

    @Test
    public void testStack() throws IOException {
        App app = new App();
        InMemorySetInfraStack stack = new InMemorySetInfraStack(app, "test");

        Template template = Template.fromStack(stack);

//        template.hasResourceProperties("AWS::SQS::Queue", new HashMap<String, Number>() {{
//          put("VisibilityTimeout", 300);
//        }});
//
//        template.resourceCountIs("AWS::SNS::Topic", 1);
    }
}
