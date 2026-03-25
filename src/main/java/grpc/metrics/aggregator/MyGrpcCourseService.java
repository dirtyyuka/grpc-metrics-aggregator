package grpc.metrics.aggregator;

import org.springframework.beans.factory.annotation.Autowired;

import com.proto.tutorial.CourseServiceGrpc;
import com.proto.tutorial.ProtoTutorial.Course;
import com.proto.tutorial.ProtoTutorial.CourseRequest;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@GrpcService
public class MyGrpcCourseService extends CourseServiceGrpc.CourseServiceImplBase {

    @Autowired
    private CourseRepository courseRepository;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyGrpcCourseService.class);

    @Override
    public void getCourse(CourseRequest request, StreamObserver<Course> responseObserver) {
        // handle task to background worker
        Mono.fromCallable(() -> {
            log.info("fetching course: " + request.getId());
            return courseRepository.getCourse(request.getId());
        })
        // delegate to background worker
        .subscribeOn(Schedulers.boundedElastic())
    
        // data is ready
        .subscribe(
            course -> {
                responseObserver.onNext(course);
                responseObserver.onCompleted();
            },
            error -> {
                // log.error("error fetching course", error);
                responseObserver.onError(Status.INTERNAL
                    .withDescription("Database error")
                    .asException());
            }
        );
    }

}
