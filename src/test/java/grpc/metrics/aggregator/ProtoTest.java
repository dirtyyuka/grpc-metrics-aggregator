package grpc.metrics.aggregator;

import org.springframework.boot.test.context.SpringBootTest;

import io.grpc.ManagedChannel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class ProtoTest {
    
    ManagedChannel channel;
}