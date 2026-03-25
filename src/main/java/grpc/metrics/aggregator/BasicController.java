package grpc.metrics.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proto.tutorial.ProtoTutorial.Course;

@RestController
public class BasicController {
    @Autowired
    CourseRepository courseRepository;

    @RequestMapping("/courses/{id}")
    Course customer(@PathVariable Integer id) {
        return courseRepository.getCourse(id);
    }
}
