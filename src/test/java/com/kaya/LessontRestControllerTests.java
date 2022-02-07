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
import com.kaya.web.LessontRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LessontRestController.class)
public class LessontRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private StudentRepo studentRepo;

	@MockBean
	private LessonRepo lessonRepo;

	Lesson lesson1 = new Lesson(1L, "Maths", 3, new Date(), null);
	Lesson lesson2 = new Lesson(2L, "Geography", 3, new Date(), null);
	Lesson lesson3 = new Lesson(2L, "Physic", 3, new Date(), null);

//    Get Lessons --> Success
	@Test
	void whenCallGetLessons_thenReturns200() throws Exception {

		List<Lesson> lessons = new ArrayList<>();
		lessons.add(lesson1);
		lessons.add(lesson2);

		Mockito.when(lessonRepo.findAll()).thenReturn(lessons);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/lessons").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).findAll();
		assertThat(objectMapper.writeValueAsString(lessons)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(lesson1.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].lessonName"));
		assertThat(lesson1.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$[0].quota"));
		assertThat(lesson2.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].lessonName"));
		assertThat(lesson2.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$[1].quota"));
	}

//  GetLessons --> LessonNotFoundException
	@Test
	void whenCallgetLessons_thenReturnsNoData() throws Exception {

		Mockito.when(lessonRepo.findAll()).thenReturn(Collections.emptyList());

		MvcResult mvcResult = mockMvc.perform(get("/lessons").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).findAll();

		assertThat("Any Lessons not found").isEqualTo(JsonPath.parse(responseBody).read("$.message"));
		assertThat("uri=/lessons").isEqualTo(JsonPath.parse(responseBody).read("$.details"));
	}

//  GetLesson by ID --> Success
	@Test
	void whenCallGetLessonById_thenReturns200() throws Exception {

		Mockito.when(lessonRepo.findById(lesson1.getId())).thenReturn(java.util.Optional.of(lesson1));

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/lesson/" + lesson1.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).findById(lesson1.getId());
		assertThat(objectMapper.writeValueAsString(lesson1)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(lesson1.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$.lessonName"));
		assertThat(lesson1.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$.quota"));
	}

//  GetLessons --> LessonNotFoundException
	@Test
	void whenCallgetLessonById_thenReturnsNoData() throws Exception {

		Mockito.when(lessonRepo.findById(lesson1.getId())).thenReturn(java.util.Optional.empty());

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/lesson/" + lesson1.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).findById(lesson1.getId());

		assertThat("Lesson not found by " + lesson1.getId()).isEqualTo(JsonPath.parse(responseBody).read("$.message"));
		assertThat("uri=/lesson/" + lesson1.getId()).isEqualTo(JsonPath.parse(responseBody).read("$.details"));
	}

//  Create New Lesson --> Success
	@Test
	void whenCallCreateLesson_thenReturns200() throws Exception {

		Lesson lesson = Lesson.builder().id(5L).lessonName("Music").quota(3).enrolledStudents(Collections.emptyList())
				.build();

		Mockito.when(lessonRepo.save(lesson)).thenReturn(lesson);

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/lesson").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(lesson)))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).save(lesson);
		assertThat(objectMapper.writeValueAsString(lesson)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(lesson.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$.id"));
		assertThat(lesson.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$.lessonName"));
		assertThat(lesson.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$.quota"));
		assertThat("Lesson is added succesfully").isEqualTo(mvcResult.getResponse().getHeader("Message"));
	}

//  Update New Lesson --> Success  
	@Test
	void whenCallUpdateLesson_thenReturns200() throws Exception {

		Lesson updateLesson = Lesson.builder().id(1L).lessonName("Music").quota(3).updateDate(new Date())
				.enrolledStudents(Collections.emptyList()).build();

		lesson1.setEnrolledStudents(Collections.emptyList());

		Mockito.when(lessonRepo.findById(lesson1.getId())).thenReturn(java.util.Optional.of(lesson1));
		Mockito.when(lessonRepo.save(updateLesson)).thenReturn(updateLesson);

		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.put("/lesson/" + lesson1.getId()).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateLesson)))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).save(updateLesson);
		assertThat(objectMapper.writeValueAsString(updateLesson)).isEqualToIgnoringWhitespace(responseBody);

		assertThat(updateLesson.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$.id"));
		assertThat(updateLesson.getLessonName()).isEqualTo(JsonPath.parse(responseBody).read("$.lessonName"));
		assertThat(updateLesson.getQuota()).isEqualTo(JsonPath.parse(responseBody).read("$.quota"));
		assertThat("Lesson is updated succesfully with id : " + updateLesson.getId().intValue())
				.isEqualTo(mvcResult.getResponse().getHeader("Message"));
	}
	
//  Get Students From Lesson --> Success  
	@Test
	void whenCallGetLessonsFromStudent_thenReturns200() throws Exception {

		List<Long> studentIdsOfLesson = new ArrayList<>();
		studentIdsOfLesson.add(1L);
		studentIdsOfLesson.add(2L);

		Mockito.when(lessonRepo.findAllStudentIdsOfLesson(lesson1.getId())).thenReturn(studentIdsOfLesson);

		Student student1 = Student.builder().id(1L).firstName("Mesut").lastName("Kaya")
				.enrolledLessons(Collections.emptyList()).updateDate(new Date()).build();

		Student student2 = Student.builder().id(2L).firstName("Omer").lastName("Kaya")
				.enrolledLessons(Collections.emptyList()).updateDate(new Date()).build();

		Mockito.when(studentRepo.findById(student1.getId())).thenReturn(java.util.Optional.of(student1));
		Mockito.when(studentRepo.findById(student2.getId())).thenReturn(java.util.Optional.of(student2));

		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/lesson/" + lesson1.getId() + "/students")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		verify(lessonRepo, times(1)).findAllStudentIdsOfLesson(lesson1.getId());
		verify(studentRepo, times(1)).findById(student1.getId());
		verify(studentRepo, times(1)).findById(student2.getId());

		assertThat(student1.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$[0].id"));
		assertThat(student1.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].firstName"));
		assertThat(student1.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$[0].lastName"));
		assertThat(student2.getId().intValue()).isEqualTo(JsonPath.parse(responseBody).read("$[1].id"));
		assertThat(student2.getFirstName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].firstName"));
		assertThat(student2.getLastName()).isEqualTo(JsonPath.parse(responseBody).read("$[1].lastName"));
	}

}
