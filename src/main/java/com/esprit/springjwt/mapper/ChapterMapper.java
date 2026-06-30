package com.esprit.springjwt.mapper;

import com.esprit.springjwt.dto.ChapterDto;
import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;

public class ChapterMapper {

    public static ChapterDto mapToDto(Chapter chapter, Course course) {
        return ChapterDto.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .courseTitle(course.getTitle())
                .build();
    }
}
