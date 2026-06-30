package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.dto.LessonDto;
import com.esprit.springjwt.entity.e_learning.Lesson;
import com.esprit.springjwt.mapper.LessonMapper;
import com.esprit.springjwt.service.e_learning.ILessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/e-learning/lesson")
public class LessonController {

    @Autowired
    ILessonService lessonService;


    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<Lesson> lessonList = lessonService.getLessons();
        List<LessonDto> lessons = new ArrayList<>();
        if(!lessonList.isEmpty()) { 
            for (Lesson l: lessonList) {
                lessons.add(LessonMapper.maptToDto(l, l.getChapter()));
            }
        }

        return new ResponseEntity<>(lessons, HttpStatus.OK);
    }

    @GetMapping("/{idLesson}")
    public ResponseEntity<?> getLesson(@PathVariable("idLesson") Long id) {
        Lesson lesson = lessonService.getOne(id);

        if(lesson != null)
            return new ResponseEntity<>(lesson, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Long id) {

        try {
            lessonService.deleteLesson(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
