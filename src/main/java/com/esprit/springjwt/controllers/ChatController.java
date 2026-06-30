package com.esprit.springjwt.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.esprit.springjwt.entity.Chat;
import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.service.ChatService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	
	@Autowired
	ChatService chatService;
	

	@PostMapping("/createChat")
	public ResponseEntity<?> sendMessage (@RequestBody Forum chat , @RequestParam("group_id") Long group_id , @RequestParam("user_id") Long user_id) {
		try {
			return ResponseEntity.ok(chatService.createChat(chat ,group_id ,user_id));
			
		}catch(InternalServerError e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
		
	}
	
	@PostMapping("/createChatAll")
	public ResponseEntity<?> sendMessageAll (@RequestBody Forum chat , @RequestParam("user_id") Long user_id) {
		try {
			return ResponseEntity.ok(chatService.createChatAll(chat ,user_id));
			
		}catch(InternalServerError e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
		
	}
	
	@GetMapping("/getMessagesByGroupId")
	public ResponseEntity<?> getMessagesByGroupId(@RequestParam("id") Long id){
	List<Chat> chats= new ArrayList<>();
	try {
		chats = chatService.getMessagesByGroupId(id);
		return ResponseEntity.ok(chats);
	}catch(InternalServerError e) {
		System.err.println(e.getMessage());
		return ResponseEntity.ok("error while fetching data ");
	}
		
	}
	
	@GetMapping("/getMessagesForAll")
	public ResponseEntity<?> getMessagesForAll(){
	List<Chat> chats= new ArrayList<>();
	try {
		chats = chatService.getMessagesForAll();
		return ResponseEntity.ok(chats);
	}catch(InternalServerError e) {
		System.err.println(e.getMessage());
		return ResponseEntity.ok("error while fetching data ");
	}
		
	}
	
	@DeleteMapping("/deleteMessage")
	public ResponseEntity<?> deleteMessage(@RequestParam("id") Long id){
		try {
			chatService.deleteMessage(id);
			return ResponseEntity.ok(" Message deleted successfully");
		}catch(Exception e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
	}
}
