package com.kaya.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaya.model.Lesson;

@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long >{

	@Query(value = "SELECT student_id FROM students_lessons WHERE lesson_id = ?1",nativeQuery = true)
	List<Long> findAllStudentIdsOfLesson(Long lesson_id);
	
	@Query(value = "SELECT Count(*) FROM students_lessons WHERE lesson_id = ?1",nativeQuery = true)
	Integer countAllStudentIdsOfLesson(Long lesson_id);
}
