package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.dto.ChapterDto;
import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;
import com.esprit.springjwt.entity.e_learning.Lesson;
import com.esprit.springjwt.mapper.ChapterMapper;
import com.esprit.springjwt.service.e_learning.IChapterService;
import com.esprit.springjwt.service.e_learning.ICourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/e-learning/chapter")
public class ChapterController {

    @Autowired
    IChapterService chapterService;

    @Autowired
    ICourseService courseService;

    @GetMapping()
    public ResponseEntity<?> getChapters() {
        List<Chapter> chapterList = chapterService.getChapters();
        List<ChapterDto> chapters = new ArrayList<>();
        if(!chapterList.isEmpty()) {
            for (Chapter c: chapterList) {
                chapters.add( ChapterMapper.mapToDto(c, c.getCourse()));
            }
        }
            return new ResponseEntity<>(chapters, HttpStatus.OK);
    }

    @GetMapping("/getChaptersByCourse/{idCourse}")
    public ResponseEntity<?> getChaptersByCourse(@PathVariable("idCourse") Long idCourse) {

        List<Chapter> chapters = chapterService.getChaptersByCourse(idCourse);

        if(!chapters.isEmpty()) {
            return new ResponseEntity<>(chapters, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getLessonsByChapter/{idChapter}")
    public ResponseEntity<?> getLessonsByChapter(@PathVariable("idChapter") Long idChapter) {
        List<Lesson> lessons = chapterService.getLessonsByChapter(idChapter);

        if(!lessons.isEmpty()) {
            return new ResponseEntity<>(lessons, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable("id") Long id) {

        try {
            chapterService.deleteChapter(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/addLesson/{idChapter}")
    public ResponseEntity<?> addLessonToCHapter(@RequestParam("thumbNail") MultipartFile thumbNail, @RequestParam("video") MultipartFile video, @RequestParam("lesson") String lesson, @PathVariable("idChapter") Long idChapter) throws JsonProcessingException {

        if(lesson.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Lesson l = objectMapper.readValue(lesson, Lesson.class);
            return new ResponseEntity<>(chapterService.addLesson(l, idChapter, thumbNail, video), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
