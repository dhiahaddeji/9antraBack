package com.esprit.springjwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Notification;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.NotificationRepository;
import com.esprit.springjwt.repository.UserRepository;

@Service
public class NotificationService {

	@Autowired
	NotificationRepository notificationRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	WebSocketService webSocketService;
	
	public void deleteAllNotifications() {
		notificationRepository.deleteAll();
		webSocketService.notifyFront("notification");
	}
	
	public void deleteNotificationById(Long id) throws Exception {
		Notification notification = notificationRepository.getById(id);
		if(notification ==null) {
			throw new Exception("Notification not found");
		}else {
			notificationRepository.delete(notification);
			webSocketService.notifyFront("notification");
		}
	}
	
	public void sendNotifToAllUsers(String message , String link , String title) {
		List<User> users = userRepository.findAll();
		for(User u:users) {
			Notification notification = new Notification();
			notification.setMessage(message);
			notification.setLink(link);
			notification.setTitle(title);
			notification.setUserRecu(u);
			notificationRepository.save(notification);	
			webSocketService.notifyFront("notification");
		}	
	}
	
	
	
	public void sendNotifToUser(String message , Long id) {
		User user = userRepository.getById(id);
		Notification notification = new Notification();
		notification.setMessage(message);
		notification.setUserRecu(user);
		notificationRepository.save(notification);
		webSocketService.notifyFront("notification");
	}
	
	public List<Notification> getAllNotifications(Long id){
		List<Notification> notifications = notificationRepository.getAllNotifications(id);
		return notifications;
	}
	
	public void changeStatus(Long id) throws Exception{
		Notification notification = notificationRepository.getById(id);
		if(notification==null) {
			throw new Exception("Notification not found");
		}else {
			if(notification.getStatus()==0) {
				notification.setStatus(1);
				notificationRepository.save(notification);
				webSocketService.notifyFront("notification");
			}	
		}
	}
	
	public void changeStatusToSeenDetails(Long id) throws Exception{
		Notification notification = notificationRepository.getById(id);
		if(notification==null) {
			throw new Exception("Notification not found");
		}else {
			if(notification.getStatus()!=-1) {
				notification.setStatus(-1);
				notificationRepository.save(notification);
				webSocketService.notifyFront("notification");
			}	
		}
	}
	
	public void changeStatusToDeleted(Long id ) throws Exception {
		Notification notification = notificationRepository.getById(id);
		if(notification==null) {
			throw new Exception("Notification not found");
		}else {
			if(notification.getStatus()!=2) {
				notification.setStatus(2);
				notificationRepository.save(notification);
				webSocketService.notifyFront("notification");
			}	
		}
	}
}
