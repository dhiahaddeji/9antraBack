package com.esprit.springjwt.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.entity.Response;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.ForumRepository;
import com.esprit.springjwt.repository.ResponseRepository;
import com.esprit.springjwt.repository.UserRepository;

@Service
public class ResponseService {
	@Autowired
	ForumRepository forumRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ResponseRepository responseRepository;
	 @Autowired
	 private WebSocketService webSocketService;
	 
	public void createResponse(Forum content , Long id , Long forum_id) {
		User user = userRepository.getById(id);
		Forum forum = forumRepository.getById(forum_id);
		Set<Response> responses = null;
		Response response = new Response();
		if(user!=null && forum!=null) {
			response.setUserEnvoi(user);
			response.setContent(content.getPost());
			responseRepository.save(response);
			responses = forum.getResponses();
			responses.add(response);
			forum.setResponses(responses);
			forumRepository.save(forum);
		}
	}

}
