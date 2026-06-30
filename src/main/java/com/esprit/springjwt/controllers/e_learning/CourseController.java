package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;
import com.esprit.springjwt.service.e_learning.ICourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/e-learning/course")
public class CourseController {

    @Autowired
    ICourseService courseService;


    @GetMapping
    public ResponseEntity<?> getCourses() {
        List<Course> courses = courseService.getAll();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOneCourse(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);

        if(course != null)
            return new ResponseEntity<>(course, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<?> addCourse(@RequestParam("image") MultipartFile image, @RequestParam("video") MultipartFile video, @RequestParam String course) throws JsonProcessingException {

        if(course.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Course c = objectMapper.readValue(course, Course.class);
            return new ResponseEntity<>(courseService.addCourse(image, video, c), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<?> updateCourse(@RequestParam("trailer") MultipartFile trailer, @RequestParam("image") MultipartFile image, @RequestParam String course) throws Exception {

        if(course.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Course c = objectMapper.readValue(course, Course.class);
            return new ResponseEntity<>(courseService.updateCourse(trailer, image, c), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/addChapter/{idCourse}")
    public ResponseEntity<?> addChapterToCourse(@RequestBody Chapter c, @PathVariable("idCourse") Long idCourse) {

        if(c != null) {
            return new ResponseEntity<>(courseService.addChapter(c, idCourse), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{idCourse}")
    public ResponseEntity<?> deleteCourse(@PathVariable("idCourse") Long id) {
        try {
            courseService.deleteCourse(id);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/addCourseToWishList")
    public ResponseEntity<?> addCoursetoWishList(@RequestParam("user_id") Long user_id , @RequestParam("course_id") Long course_id){
    	try {
			courseService.addCourseToWishList(user_id, course_id);
			return ResponseEntity.ok("Course added");
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    	
    }
    
    
    @DeleteMapping("/deleteCourseToWishList")
    public ResponseEntity<?> deleteCourseToWishList(@RequestParam("user_id") Long user_id , @RequestParam("course_id") Long course_id){
    	try {
			courseService.deleteCourseToWishList(user_id, course_id);
			return ResponseEntity.ok("Course deleted");
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    	
    }
}
