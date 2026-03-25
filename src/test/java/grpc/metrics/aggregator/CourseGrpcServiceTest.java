package grpc.metrics.aggregator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.proto.tutorial.CourseServiceGrpc;
import com.proto.tutorial.ProtoTutorial.Course;
import com.proto.tutorial.ProtoTutorial.CourseRequest;

import io.grpc.ManagedChannelBuilder;

public class CourseGrpcServiceTest extends ProtoTest{
    
    private CourseServiceGrpc.CourseServiceBlockingStub stub;

    @BeforeEach
    void setup() {
        // setup communication channel
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                    .usePlaintext()
                    .build();
        
        // create stub
        stub = CourseServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdown();
    }

    @Test
    void testGetCourse() {
        // prepare request
        CourseRequest request = CourseRequest.newBuilder().setId(1).build();

        // call the server
        Course response = stub.getCourse(request);

        // assertions
        assertEquals(1, response.getId());
        assertEquals("Rest with spring", response.getCourseName());
    }
}


