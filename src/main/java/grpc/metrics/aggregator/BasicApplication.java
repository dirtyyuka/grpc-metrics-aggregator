package grpc.metrics.aggregator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.proto.tutorial.ProtoTutorial.Course;
import com.proto.tutorial.ProtoTutorial.Student;

@SpringBootApplication
public class BasicApplication {
	@Bean
	ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}

	@Bean
	RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
		return new RestTemplate(Arrays.asList(hmc));
	}

	@Bean 
	public CourseRepository createTestCourses() {
		Map<Integer, Course> courses = new HashMap<>();
		Course course1 = Course.newBuilder()
			.setId(1)
			.setCourseName("Rest with spring")
			.addAllStudent(new ArrayList<Student>())
			.build();
		Course course2 = Course.newBuilder()
			.setId(2)
			.setCourseName("Spring security")
			.addAllStudent(new ArrayList<Student>())
			.build();
		courses.put(course1.getId(), course1);
		courses.put(course2.getId(), course2);
		return new CourseRepository(courses);
	}

	public static void main(String[] args) {
		SpringApplication.run(BasicApplication.class, args);
	}

}
