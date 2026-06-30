package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.dto.ChapterDto;
import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Lesson;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IChapterService {

    List<Chapter> getChapters();

    void deleteChapter(Long id);

    Lesson addLesson(Lesson l, Long idChapter, MultipartFile thumbNail, MultipartFile video);

    List<Chapter> getChaptersByCourse(Long idCourse);

    List<Lesson> getLessonsByChapter(Long idChapter);
}
