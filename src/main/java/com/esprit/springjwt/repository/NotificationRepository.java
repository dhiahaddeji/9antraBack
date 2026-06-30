package com.esprit.springjwt.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.esprit.springjwt.entity.Chat;
import com.esprit.springjwt.entity.Notification;

@EnableJpaRepositories
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query(value="SELECT * FROM notification WHERE (user_recu = :id OR user_recu IS NULL) and status!=2 ORDER BY created_at LIMIT 15",nativeQuery=true)
	List<Notification> getAllNotifications(Long id);
	
	@Query(value="select * from notification where user_recu=:id",nativeQuery=true)
	Page<Notification> getNotifsPaginate(PageRequest pageRequest,Long id );
}
