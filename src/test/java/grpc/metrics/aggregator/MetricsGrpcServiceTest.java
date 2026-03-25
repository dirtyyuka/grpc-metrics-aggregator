package grpc.metrics.aggregator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.proto.tutorial.MetricsServiceGrpc;
import com.proto.tutorial.Metrics.MetricResponse;
import com.proto.tutorial.Metrics.PingMetric;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class MetricsGrpcServiceTest extends ProtoTest {

    private MetricsServiceGrpc.MetricsServiceStub stub;

    @BeforeEach
    void setup() {
        // setup communication channel
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // create stub
        stub = MetricsServiceGrpc.newStub(channel);
    }

    @Test
    void validate1000Pings_stressTest() throws InterruptedException {
        StreamObserver<MetricResponse> responseObserver = new StreamObserver<>() {
            public void onNext(MetricResponse value) {
                System.out.println("response: " + value);
            }

            public void onError(Throwable t) {
                t.printStackTrace();
            }

            public void onCompleted() {
                System.out.println("Done");
            }
        };

        StreamObserver<PingMetric> requestObserver = stub.streamMetrics(responseObserver);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 100; i++) {
                requestObserver.onNext(PingMetric.newBuilder()
                        .setDeviceId("dev-" + i)
                        .setLatencyMs(10)
                        .build());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        requestObserver.onCompleted();
    }

}
