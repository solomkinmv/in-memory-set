package in.solomk.infra;

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
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class InMemorySetInfraStack extends Stack {
    public InMemorySetInfraStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public InMemorySetInfraStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Create a new VPC or use an existing one
        Vpc vpc = Vpc.Builder.create(this, "InMemorySetVpc")
                .maxAzs(1)
                .build();

        // Define a Security Group to allow SSH and HTTP traffic
        SecurityGroup sg = SecurityGroup.Builder.create(this, "InMemorySetSecurityGroup")
                .vpc(vpc)
                .build();
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH access");
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP access");

        // Define the Amazon Linux 2 AMI
        AmazonLinuxImage ami = AmazonLinuxImage.Builder.create()
                .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
                .cpuType(AmazonLinuxCpuType.ARM_64)
                .build();

        // Create an EC2 instance
        Instance instance = Instance.Builder.create(this, "InMemorySetInstance")
                .instanceType(InstanceType.of(InstanceClass.T4G, InstanceSize.NANO))
                .machineImage(ami)
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder()
                        .subnetType(SubnetType.PUBLIC)
                        .build())
                .keyPair(KeyPair.fromKeyPairName(this, "InMemorySetKeyPair", "MacBook"))
                .securityGroup(sg)
                .build();

        // Add user data to install Docker and run a Docker container
        UserData userData = UserData.forLinux();
        String userDataScript = """
                #!/bin/bash
                yum update -y
                yum install docker -y
                service docker start
                usermod -a -G docker ec2-user
                docker run -d -p 80:8080 solomkinmv/in-memory-set-service --rm
                """;
        userData.addCommands(userDataScript);
        instance.addUserData(userData.render());

        // Assign an Elastic IP to the instance
        CfnEIP elasticIp = CfnEIP.Builder.create(this, "InMemorySetElasticIp")
                .instanceId(instance.getInstanceId())
                .build();
    }
}
