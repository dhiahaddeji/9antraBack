package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.entity.Notification;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {


	@Query(value="select * from forum order by created_at",nativeQuery=true)
	List<Forum> getMessages();
	
	@Query(value="select * from forum order by created_at desc",nativeQuery=true)
	Page<Forum> getPostsPaginate(PageRequest pageRequest);
	
	@Query(value="select * from forum where subject Like %:search% order by created_at desc",nativeQuery=true)
	Page<Forum> getPostsPaginateSearch(PageRequest pageRequest,String search);
}
