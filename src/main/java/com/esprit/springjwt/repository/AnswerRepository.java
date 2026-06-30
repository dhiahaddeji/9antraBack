package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.Answer;
import com.esprit.springjwt.entity.Forum;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	
	@Query(value="select * from forum order by created_at desc",nativeQuery=true)
	Page<Forum> getPostsPaginate(PageRequest pageRequest);
	
	@Query(value="select count(*) from answer where quiz_id=:id",nativeQuery=true)
	int getCountQuestionsByQuizId(Long id);
	
	@Query(value="select * from answer where quiz_id=:id",nativeQuery=true)
	List<Answer> getQuestionsByQuizId(Long id);
	
	@Query(value="delete from answer where id=:id",nativeQuery=true)
	void deleteAnswer(Long id);
}
