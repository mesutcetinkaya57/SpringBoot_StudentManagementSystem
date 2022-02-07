package com.kaya.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kaya.model.Student;

@Repository	
public interface StudentRepo extends JpaRepository<Student, Long> {
	
	@Query(value = "SELECT lesson_id FROM students_lessons WHERE student_id = ?1",nativeQuery = true)
	List<Long> findAllLessonIdsOfStudent(Long student_id);
}
