package in.solomk.infra;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.AmazonLinuxCpuType;
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.CfnEIP;
import software.amazon.awscdk.services.ec2.Instance;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.KeyPair;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.UserData;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class InMemorySetInfraStack extends Stack {

    private static final String IMAGE_NAME = "solomkinmv/in-memory-set-service";

    public InMemorySetInfraStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public InMemorySetInfraStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        Vpc vpc = Vpc.Builder.create(this, id + "-Vpc")
                .maxAzs(1)
                .build();

        SecurityGroup sg = SecurityGroup.Builder.create(this, id + "-SecurityGroup")
                .vpc(vpc)
                .build();
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH access");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP access");

        AmazonLinuxImage ami = AmazonLinuxImage.Builder.create()
                .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
                .cpuType(AmazonLinuxCpuType.ARM_64)
                .build();

        Instance instance = Instance.Builder.create(this, id + "-Instance")
                .instanceType(InstanceType.of(InstanceClass.T4G, InstanceSize.NANO))
                .machineImage(ami)
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder()
                        .subnetType(SubnetType.PUBLIC)
                        .build())
                .keyPair(KeyPair.fromKeyPairName(this, id + "-KeyPair", "MacBook"))
                .securityGroup(sg)
                .build();

        UserData userData = UserData.forLinux();
        String userDataScript = """
                #!/bin/bash
                yum update -y
                yum install docker -y
                service docker start
                usermod -a -G docker ec2-user
                docker run -d -p 80:8080 %s --rm
                """.formatted(IMAGE_NAME);
        userData.addCommands(userDataScript);
        instance.addUserData(userData.render());

        CfnEIP elasticIp = CfnEIP.Builder.create(this, id + "-ElasticIp")
                .instanceId(instance.getInstanceId())
                .build();
    }
}
