package com.esprit.springjwt.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Chat;
import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Response;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.ForumRepository;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.UserRepository;

@Service
public class ForumService {

	@Autowired
	ForumRepository forumRepository;
	
	@Autowired
	UserRepository userRepository;
	
	 @Autowired
	 private WebSocketService webSocketService;
	 
	 
	public List<Forum> getMessages(){
		return forumRepository.getMessages();
	}
	
	public Forum createPost(Forum forum, Long user_id) {
		User user = userRepository.getById(user_id);
		Forum newForum = new Forum();
		Set<Response> responses = new HashSet<>();
		newForum.setPost(forum.getPost());
		newForum.setSubject(forum.getSubject());
		newForum.setResponses(responses);
		if(user !=null) {
			newForum.setUserEnvoi(user);
		}
		forumRepository.save(newForum);
		webSocketService.notifyFront("forum");
		return newForum;
	}
	
	public void deletePost(Long id) throws Exception {
		Forum forum = forumRepository.getById(id);
		if(forum == null) {
			throw new Exception("Message not found");
		}else {
			forum.setStatus(1);
			forumRepository.save(forum);
			webSocketService.notifyFront("forum");
		}
	}
	
	public Forum getForumById(Long id){
		Forum forum = forumRepository.getById(id);
		return forum;
	}
}
