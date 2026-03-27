# gRPC Metrics Aggregator
<p align="left">
  <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/gRPC-4285F4?style=for-the-badge&logo=grpc&logoColor=white" />
  <img src="https://img.shields.io/badge/Protocol%20Buffers-002D57?style=for-the-badge&logo=protocol-buffers&logoColor=white" />
</p>


## Overview
This project provides a high performance pipeline to ingest raw data from gRPC-enabled microservices and aggregate them into metrics.

## Core features
1. <b>Client-side Streaming</b>: Devices can setup a single persistent connection and fire metrics until done.

2. <b>Asynchronous Processing</b>: Uses gRPC stream observer to handle data non blockingly, ensuring the stream is never blocked.

3. <b>Thread-safe Aggregation</b>: Uses an AtomicInteger to track global throughput on the stream without race conditions

## Performance Benchmarks
The current architecture has been validated through testing to have:

- <b>Throughput</b>: 1,000 pings per second per stream

- <b>Latency</b>: Sub-10ms processing per batch

- <b>Efficiency</b>: Minimal memory usage due to having no massive object buffered.

## Getting Started

1. Define the contract
```proto
syntax = "proto3";

package metrics.v1;
option java_package = "com.proto.tutorial";
option java_outer_classname = "Metrics";

import "google/protobuf/timestamp.proto";

message PingMetric {
    string device_id = 1;
    int64 latency_ms = 2;
    google.protobuf.Timestamp timestamp = 3;

    enum Status {
        SUCCESS = 0;
        TIMEOUT = 1;
        ERROR = 2;
    }
    Status status = 4;
}

message MetricsBatch {
    repeated PingMetric metrics = 1;
}

message MetricResponse {
    bool accepted = 1;
    string message = 2;
}

service MetricsService {
    rpc StreamMetrics(stream PingMetric) returns (MetricResponse);

    rpc SendBatch(MetricsBatch) returns (MetricResponse);
}
```

2. Run the test. This repository includes a specialized JUnit stress test that simulates a high-load environment using ScheduledExecutorService.

```java
@Test
void validate1000Pings_stressTest() throws InterruptedException {
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
```

## Internal Logic

1. Client stub creates a proxy StreamObserver for communication

2. HTTP/2 Frames transport serialized Protobuf data

3. Server handlers creates a listener for incoming data and passing it to server side stream observer.

## Roadmap

- Redis Integration: Remove in-memory counters and use redis streams for persistence

- Flow Control: Implement ManualFlowControl to handle backpressure

### Contact
Mayank Joshi - @dirtyyuka - mayankjoshi455@gmail.com
<br>
Project link: https://github.com/dirtyyuka/grpc-metrics-aggregator
