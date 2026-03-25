package grpc.metrics.aggregator;

import java.util.concurrent.atomic.AtomicInteger;

import com.proto.tutorial.MetricsServiceGrpc;
import com.proto.tutorial.Metrics.MetricResponse;
import com.proto.tutorial.Metrics.PingMetric;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MyGrpcMetricsService extends MetricsServiceGrpc.MetricsServiceImplBase{
    
    private final AtomicInteger globalCounter = new AtomicInteger(0);
    
    @Override
    public StreamObserver<PingMetric> streamMetrics(StreamObserver<MetricResponse> responseObserver) {


        return new StreamObserver<PingMetric>() {
            private int streamCount = 0;

            @Override
            public void onNext(PingMetric metric) {
                if (metric.getDeviceId().isEmpty()) return;
                streamCount++;
                globalCounter.incrementAndGet();
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("client error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                    MetricResponse.newBuilder()
                        .setAccepted(true)
                        .setMessage("Processed " + streamCount + " metrics")
                        .build()
                );

                responseObserver.onCompleted();
            }
        };
    }
}
