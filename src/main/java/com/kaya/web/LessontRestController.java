package com.kaya.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kaya.data.LessonRepo;
import com.kaya.data.StudentRepo;
import com.kaya.exceptions.LessonNotFoundException;
import com.kaya.exceptions.StudentNotFoundException;
import com.kaya.model.Lesson;
import com.kaya.model.Student;

@RestController
public class LessontRestController {

	@Autowired
	private LessonRepo lessonRepo;

	@Autowired
	private StudentRepo studentRepo;

	@RequestMapping(method = RequestMethod.GET, value = "/lessons")
	public ResponseEntity<List<Lesson>> getLessons() throws Exception {
		List<Lesson> lessons = lessonRepo.findAll();
		if (lessons.isEmpty())
			throw new LessonNotFoundException("Any Lessons not found");

		return ResponseEntity.ok(lessons);
	}


	@RequestMapping(method = RequestMethod.GET, value = "/lesson/{id}")
	public ResponseEntity<Lesson> getLesson(@PathVariable("id") Long id) throws Exception {
		Lesson lesson = lessonRepo.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("Lesson not found by " + id));

		return ResponseEntity.ok(lesson);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/lesson")
	public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) throws Exception {
		Lesson newLesson = lessonRepo.save(lesson);
		return ResponseEntity.ok().header("Message", "Lesson is added succesfully").body(newLesson);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/lesson/{id}")
	public ResponseEntity<?> updateLesson(@PathVariable("id") Long id, @RequestBody Lesson lessonRequest)
			throws Exception {
		Lesson lesson = lessonRepo.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("Lesson not found by " + id));
		lesson.setLessonName(lessonRequest.getLessonName());
		lesson.setQuota(lessonRequest.getQuota());
		lesson.setUpdateDate(lessonRequest.getUpdateDate());
		lesson = lessonRepo.save(lesson);
		return ResponseEntity.ok().header("Message", "Lesson is updated succesfully with id : " + id).body(lesson);
	}

	@RequestMapping(method = RequestMethod.GET, value = "lesson/{lessonId}/students")
	public ResponseEntity<List<Student>> getLessonsFromStudent(@PathVariable Long lessonId) throws Exception {
		List<Long> studentIdsOfLesson = lessonRepo.findAllStudentIdsOfLesson(lessonId);
		List<Student> studentsOfLesson = new ArrayList<>();
		Student student;
		for (Long studentId : studentIdsOfLesson) {
			student = studentRepo.findById(studentId)
					.orElseThrow(() -> new StudentNotFoundException("Student not found by " + studentId));

			studentsOfLesson.add(student);
		}

		return ResponseEntity.ok(studentsOfLesson);
	}
}
