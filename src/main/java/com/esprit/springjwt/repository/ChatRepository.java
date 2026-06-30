package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.esprit.springjwt.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	
	@Query(value="select * from chat where id_group=:id",nativeQuery=true)
	List<Chat> getMessagesByGroupId(Long id);
	
	@Query(value="select * from chat where id_group is Null",nativeQuery=true)
	List<Chat> getMessagesForAll();
}
