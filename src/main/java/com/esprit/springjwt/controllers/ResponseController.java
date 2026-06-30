package com.esprit.springjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.service.ResponseService;

@RestController
@RequestMapping("/api/forum/response")
public class ResponseController {
	
	@Autowired
	ResponseService responseService;
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createResponse(@RequestParam("id") Long id ,@RequestParam("forum_id") Long forum_id , @RequestBody Forum forum){
		try {
			responseService.createResponse(forum, id,forum_id);
			return ResponseEntity.ok("success");
		}catch(InternalServerError e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
	}
	
	
	
	

}
