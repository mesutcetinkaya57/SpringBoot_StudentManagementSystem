package com.kaya;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kaya.data.LessonRepo;
import com.kaya.data.StudentRepo;
import com.kaya.model.Lesson;
import com.kaya.model.Student;
import com.kaya.web.StudentRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = StudentRestController.class)
public class StudentRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private StudentRepo studentRepo;

	@MockBean
	private LessonRepo lessonRepo;

	Student student1 = new Student(1L, "Mesut", "Cetinkaya", new Date(), new ArrayList<>());
	Student student2 = new Student(2L, "Nazim", "Cetinkaya", new Date(), new ArrayList<>());
	Student student3 = new Student(3L, "Omer", "Cetinkaya", new Date(), new ArrayList<>());

//  Get Students --> Success
	@Test
	void whenCallGetStudents_thenReturns200() throws Exception {

		List<Student> students = new ArrayList<>();
		students.add(student1);
		students.add(student2);

		Mockito.when(studentRepo.findAll()).thenReturn(students);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/students").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).findAll();
		assertThat(objectMapper.writeValueAsString(students)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(student1.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].firstName"));
		assertThat(student1.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].lastName"));
		assertThat(student2.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].firstName"));
		assertThat(student2.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].lastName"));
	}

//Get Students --> StudentNotFoundException
	@Test
	void whenCallgetStudents_thenReturnsNoData() throws Exception {

		Mockito.when(studentRepo.findAll()).thenReturn(Collections.emptyList());

		MvcResult mvcResult = mockMvc.perform(get("/students").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).findAll();

		assertThat("Any Students not found").isEqualTo(JsonPath.parse(responseBody).read("$.message"));
		assertThat("uri=/students").isEqualTo(JsonPath.parse(responseBody).read("$.details"));
	}

//GetStudent by ID --> Success
	@Test
	void whenCallGetStudentById_thenReturns200() throws Exception {

		Mockito.when(studentRepo.findById(student1.getId())).thenReturn(java.util.Optional.of(student1));

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/student/" + student1.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).findById(student1.getId());
		assertThat(objectMapper.writeValueAsString(student1)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(student1.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$.firstName"));
		assertThat(student1.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$.lastName"));
	}

//Get Students --> StudentNotFoundException
	@Test
	void whenCallgetStudentById_thenReturnsNoData() throws Exception {

		Mockito.when(studentRepo.findById(student1.getId())).thenReturn(java.util.Optional.empty());

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/student/" + student1.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).findById(student1.getId());

		assertThat("Student not found by " + student1.getId())
				.isEqualTo(JsonPath.parse(responseBody).read("$.message"));
		assertThat("uri=/student/" + student1.getId()).isEqualTo(JsonPath.parse(responseBody).read("$.details"));
	}

//Create New Student --> Success
	@Test
	void whenCallCreateStudent_thenReturns200() throws Exception {

		Student student = Student.builder().id(5L).firstName("Mesut").lastName("Kaya")
				.enrolledLessons(Collections.emptyList()).updateDate(new Date()).build();

		Mockito.when(studentRepo.save(student)).thenReturn(student);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/student").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(student)))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).save(student);
		assertThat(objectMapper.writeValueAsString(student)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(student.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$.id"));
		assertThat(student.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$.firstName"));
		assertThat(student.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$.lastName"));
		assertThat("Student is added succesfully").isEqualTo(mvcResult.getResponse().getHeader("Message"));
	}

//Update New Student --> Success  
	@Test
	void whenCallUpdateStudent_thenReturns200() throws Exception {

		Student updateStudent = Student.builder().id(1L).firstName("Mesut").lastName("Kaya")
				.enrolledLessons(Collections.emptyList()).updateDate(new Date()).build();

		student1.setEnrolledLessons(Collections.emptyList());

		Mockito.when(studentRepo.findById(student1.getId())).thenReturn(java.util.Optional.of(student1));
		Mockito.when(studentRepo.save(updateStudent)).thenReturn(updateStudent);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.put("/student/" + student1.getId())
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateStudent)))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).save(updateStudent);
		assertThat(objectMapper.writeValueAsString(updateStudent)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(updateStudent.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$.id"));
		assertThat(updateStudent.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$.firstName"));
		assertThat(updateStudent.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$.lastName"));
		assertThat("Student is updated succesfully with id : " + updateStudent.getId().intValue())
				.isEqualTo(mvcResult.getResponse().getHeader("Message"));
	}

//Get Lessons From Student--> Success  
	@Test
	void whenCallGetStudentsFromStudent_thenReturns200() throws Exception {

		List<Long> lessonIdsOfStudent = new ArrayList<>();
		lessonIdsOfStudent.add(1L);
		lessonIdsOfStudent.add(2L);

		Mockito.when(studentRepo.findAllLessonIdsOfStudent(student1.getId())).thenReturn(lessonIdsOfStudent);

		Lesson lesson1 = Lesson.builder().id(1L).lessonName("Maths").quota(3).enrolledStudents(Collections.emptyList())
						.updateDate(new Date()).build();
		Lesson lesson2 = Lesson.builder().id(2L).lessonName("Geography").quota(3).enrolledStudents(Collections.emptyList())
				.updateDate(new Date()).build();
		
		Mockito.when(lessonRepo.findById(lesson1.getId())).thenReturn(java.util.Optional.of(lesson1));
		Mockito.when(lessonRepo.findById(lesson2.getId())).thenReturn(java.util.Optional.of(lesson2));

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/student/" + student1.getId() + "/lessons")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(studentRepo, times(1)).findAllLessonIdsOfStudent(student1.getId());
		verify(lessonRepo, times(1)).findById(lesson1.getId());
		verify(lessonRepo, times(1)).findById(lesson2.getId());

		assertThat(lesson1.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$[0].id"));
		assertThat(lesson1.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].lessonName"));
		assertThat(lesson1.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$[0].quota"));
		assertThat(lesson2.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$[1].id"));
		assertThat(lesson2.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].lessonName"));
		assertThat(lesson2.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$[1].quota"));
	}

}
