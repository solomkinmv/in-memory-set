package in.solomk.inmemoryset.infra;

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

        template.resourceCountIs("AWS::EC2::Instance", 1);
        template.resourceCountIs("AWS::EC2::SecurityGroup", 1);
        template.resourceCountIs("AWS::EC2::VPC", 1);
        template.resourceCountIs("AWS::EC2::Subnet", 2);
    }
}
