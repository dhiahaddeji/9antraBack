package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

	@Query(value="select * from quiz where formation_id=:id",nativeQuery=true)
	List<Quiz> filterQuizByFormation(Long id);
}
