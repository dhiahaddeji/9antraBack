package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Lesson;

import java.io.IOException;
import java.util.List;

public interface ILessonService {

    List<Lesson> getLessons();

    void deleteLesson(Long id) throws IOException;

    Lesson getOne(Long id);
}
