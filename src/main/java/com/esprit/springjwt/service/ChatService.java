package com.esprit.springjwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Chat;
import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.ChatRepository;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.UserRepository;

@Service
public class ChatService {

	@Autowired
	ChatRepository chatRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	GroupsRepository groupsRepository; 
	
	
	 @Autowired
	 private WebSocketService webSocketService;
	 
	 
	public List<Chat> getMessagesByGroupId(Long id){
		return chatRepository.getMessagesByGroupId(id);
	}
	
	public List<Chat> getMessagesForAll(){
		return chatRepository.getMessagesForAll();
	}
	
	public Chat createChat(Forum chat , Long group_id , Long user_id) {
		Groups group = groupsRepository.getById(group_id);
		User user = userRepository.getById(user_id);
		Chat newChat = new Chat();
		newChat.setMessage(chat.getPost());
		if(group !=null && user !=null) {
			newChat.setGroupe(group);
			newChat.setUserEnvoi(user);
		}
		chatRepository.save(newChat);
		webSocketService.notifyFront("chat");
		return newChat;
	}
	
	public Chat createChatAll(Forum chat , Long user_id) {
		User user = userRepository.getById(user_id);
		Chat newChat = new Chat();
		newChat.setMessage(chat.getPost());
		if(user !=null) {
			newChat.setGroupe(null);
			newChat.setUserEnvoi(user);
		}
		chatRepository.save(newChat);
		webSocketService.notifyFront("chat");
		return newChat;
	}
	
	public void deleteMessage(Long id) throws Exception {
		Chat chat = chatRepository.getById(id);
		if(chat == null) {
			throw new Exception("Message not found");
		}else {
			chat.setStatus(1);
			chatRepository.save(chat);
			webSocketService.notifyFront("chat");
		}
	}
}
