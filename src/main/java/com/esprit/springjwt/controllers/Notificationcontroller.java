package com.esprit.springjwt.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esprit.springjwt.entity.Notification;
import com.esprit.springjwt.payload.request.PaginateInfo;
import com.esprit.springjwt.payload.request.PaginateInfoNotif;
import com.esprit.springjwt.repository.NotificationRepository;
import com.esprit.springjwt.service.NotificationService;

@RestController
@RequestMapping("/api/notification")
public class Notificationcontroller {

	@Autowired
	NotificationService notificationService;
	
	@Autowired
	NotificationRepository notificationRepository;
	
	@DeleteMapping("/deleteAll")
	public ResponseEntity<?> deleteAllNotifications(){
		notificationService.deleteAllNotifications();
		return ResponseEntity.ok("All your notifications are deleted");
	}
	
	@DeleteMapping("/deleteById")
	public ResponseEntity<?> deleteById(@RequestParam("id") Long id){
		try {
			notificationService.deleteNotificationById(id);
			return ResponseEntity.ok("Notification deleted successfully");
		}catch(Exception e) {
			return ResponseEntity.ok("Error while deleting notification");
		}
	}
	
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllNotifications(@RequestParam("id") Long id){
		List<Notification> notifications = notificationService.getAllNotifications(id);
		return ResponseEntity.ok(notifications);
	}
	
	@PutMapping("/changeStatus")
	public ResponseEntity<?> changeStatus(@RequestParam("id") Long id){
		try {
			notificationService.changeStatus(id);
			return ResponseEntity.ok("Status changes successfully"); 
		}catch(Exception e) {
			return ResponseEntity.ok("Error while changing status"); 
		}
		
	}
	
	@PutMapping("/changeStatusToSeenDetails")
	public ResponseEntity<?> changeStatusToSeenDetails(@RequestParam("id") Long id){
		try {
			notificationService.changeStatusToSeenDetails(id);
			return ResponseEntity.ok("Status changes successfully"); 
		}catch(Exception e) {
			return ResponseEntity.ok("Error while changing status"); 
		}
	}
	
	/*@PostMapping("/create")
	public ResponseEntity<?> create(){
		notificationService.sendNotifToAllUsers("test");
		return ResponseEntity.ok("Status changes successfully"); 
	}*/
	
	@GetMapping("/allNotifPaginate")
	public ResponseEntity<?> getAllNotifPaginate(
    		@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "per_page", defaultValue = "5") int size , @RequestParam("id") Long id){
    	
	 	
	 	if (page < 0 || size <= 0 ) {
	        return ResponseEntity.badRequest().body("Invalid page or per_page values.");
	 		}
	    
	 		try {
	        Page<Notification> notifs;
	        notifs=notificationRepository.getNotifsPaginate(PageRequest.of(page, size),id);
	        
	        int total = notifs.getTotalPages();
	        int[] count_page = new int[total];
	        for (int i = 0; i < total; i++) {
	            count_page[i] = i;
	        }

	        PaginateInfoNotif data = new PaginateInfoNotif(count_page, notifs, page);
	        return ResponseEntity.ok(data);
	        
	    } catch (Exception e) {
	    	
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching paginated products.");
	    }
    }
	
	@PutMapping("/changeStatusToDeleted")
	public ResponseEntity<?> changeStatusToDeleted(@RequestParam("id") Long id){
		try {
			notificationService.changeStatusToDeleted(id);
			return ResponseEntity.ok("Status changes successfully"); 
		}catch(Exception e) {
			return ResponseEntity.ok("Error while changing status"); 
		}
	}
}
