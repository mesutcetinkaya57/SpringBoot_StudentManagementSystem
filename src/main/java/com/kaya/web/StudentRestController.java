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
import com.kaya.exceptions.StudentLimitExceedException;
import com.kaya.exceptions.StudentNotFoundException;
import com.kaya.model.Lesson;
import com.kaya.model.Student;

@RestController
public class StudentRestController {
	
	@Autowired
	private LessonRepo lessonRepo;
	
	@Autowired
	private StudentRepo studentRepo;

	@RequestMapping(method = RequestMethod.GET, value = "/students")
	public ResponseEntity<List<Student>> getStudents() throws Exception {

		List<Student> students = studentRepo.findAll();
		if (students.isEmpty()) throw new StudentNotFoundException("Any Students not found");

		return ResponseEntity.ok(students);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/student/{id}")
	public ResponseEntity<Student> getStudent (@PathVariable("id") Long id) throws Exception {
			Student student = studentRepo.findById(id).orElseThrow(() -> 
							new StudentNotFoundException("Student not found by " + id));
		return ResponseEntity.ok(student);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/student")
	public ResponseEntity<Student> createStudent(@RequestBody Student student) throws Exception{
		Student newAddedStudent = studentRepo.save(student);
		return ResponseEntity.ok().header("Message", "Student is added succesfully").body(newAddedStudent);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/student/{id}")
	public ResponseEntity<?> updateStudent(@PathVariable("id") Long id, @RequestBody Student studentRequest) throws Exception{
			Student student = studentRepo.findById(id).orElseThrow(() 
					-> new StudentNotFoundException("Student not found by " + id));
		
			student.setFirstName(studentRequest.getFirstName());
			student.setLastName(studentRequest.getLastName());
			student.setUpdateDate(studentRequest.getUpdateDate());
			student = studentRepo.save(student);
			return ResponseEntity.ok().header("Message", "Student is updated succesfully with id : " + id ).body(student);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "student/{studentId}/lesson/{lessonId}")
	public ResponseEntity<?> addLessonsToStudent(@PathVariable Long studentId, @PathVariable Long lessonId) throws Exception {
		Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() 
				-> new LessonNotFoundException("Lesson not found by " + lessonId));

		int quotaOfLesson = lesson.getQuota();
		int studentCountOfLesson = lessonRepo.countAllStudentIdsOfLesson(lessonId);
		
		if (studentCountOfLesson < quotaOfLesson) {
			Student student = studentRepo.findById(studentId).orElseThrow(() 
					-> new StudentNotFoundException("Student not found by " + studentId));

			student.addLessonToList(lesson);
			student = studentRepo.save(student);
			return ResponseEntity.ok().header("Message", "Lesson "+ lesson.getLessonName() + " is added to " 
														+ student.getFirstName()).build();
		} else {
			throw new StudentLimitExceedException("Student Limit exceeded. The quota of " + lesson.getLessonName() +" is : " + quotaOfLesson);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "student/{studentId}/lesson/{lessonId}")
	public ResponseEntity<?> deleteLessonFromStudent(@PathVariable Long studentId, @PathVariable Long lessonId) throws Exception {

			Student student = studentRepo.findById(studentId).orElseThrow(() 
				-> new StudentNotFoundException("Student not found by " + studentId));
			
			Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() 
					-> new LessonNotFoundException("Lesson not found by " + lessonId));
			
			student.getEnrolledLessons().remove(lesson);
			student = studentRepo.save(student);
			
			return ResponseEntity.ok().header("Message", "Lesson "+ lesson.getLessonName() + " is removed from " 
																				+ student.getFirstName()).build();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "student/{studentId}/lessons")
	public ResponseEntity<List<Lesson>> getLessonsFromStudent(@PathVariable Long studentId) throws Exception {

			List<Long> lessonIdsOfStudent = studentRepo.findAllLessonIdsOfStudent(studentId);
			List<Lesson> lessonsOfStudent = new ArrayList<>();
			Lesson lesson;
			for (Long lessonId : lessonIdsOfStudent) {
				lesson = lessonRepo.findById(lessonId).orElseThrow(() 
						-> new LessonNotFoundException("Lesson not found by " + lessonId));
				lessonsOfStudent.add(lesson);
			}
			return ResponseEntity.ok(lessonsOfStudent);
	}
}
