package com.esprit.springjwt.mapper;

import com.esprit.springjwt.dto.LessonDto;
import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Lesson;

public class LessonMapper {
    public static LessonDto maptToDto(Lesson lesson, Chapter chapter) {
        return LessonDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .videoLesson(lesson.getVideoLesson())
                .thumbNail(lesson.getThumbNail())
                .chapterTitle(chapter.getTitle())
                .build();
    }
}
