package com.esprit.springjwt.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.esprit.springjwt.entity.Chat;
import com.esprit.springjwt.entity.Forum;
import com.esprit.springjwt.payload.request.PaginateForum;
import com.esprit.springjwt.repository.ForumRepository;
import com.esprit.springjwt.service.ForumService;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
	
	@Autowired
	ForumService forumService;
	
	@Autowired
	ForumRepository forumRepository;
	
	@PostMapping("/createPost")
	public ResponseEntity<?> createPost (@RequestBody Forum forum  , @RequestParam("user_id") Long user_id) {
		try {
			return ResponseEntity.ok(forumService.createPost(forum ,user_id));
			
		}catch(InternalServerError e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
		
	}
	
	@GetMapping("/getPosts")
	public ResponseEntity<?> getPosts(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "per_page", defaultValue = "5") int size , @RequestParam(name="search",defaultValue ="") String search){
		
	if (page < 0 || size <= 0 ) {
	    return ResponseEntity.badRequest().body("Invalid page or per_page values.");
	}
	
	try {
		Page<Forum> forums;
		if(search.isEmpty()) {
			forums = forumRepository.getPostsPaginate(PageRequest.of(page, size));
		}else {
			forums = forumRepository.getPostsPaginateSearch(PageRequest.of(page, size),search);
		}
		int total = forums.getTotalPages();
        int[] count_page = new int[total];
        for (int i = 0; i < total; i++) {
            count_page[i] = i;
        }
        PaginateForum data = new PaginateForum(count_page, forums, page);
		return ResponseEntity.ok(data);
		
	}catch(InternalServerError e) {
		System.err.println(e.getMessage());
		return ResponseEntity.ok("error while fetching data ");
	}
		
	}
	
	@PutMapping("/deletePost")
	public ResponseEntity<?> deletePost(@RequestParam("id") Long id){
		try {
			forumService.deletePost(id);
			return ResponseEntity.ok(" Message deleted successfully");
		}catch(Exception e) {
			System.err.println(e.getMessage());
			return ResponseEntity.ok("error while fetching data ");
		}
	}
	
	@GetMapping("/getForumById")
	public ResponseEntity<?> getForumById(@RequestParam("id") Long id){
		return ResponseEntity.ok(forumService.getForumById(id));
	}

}
