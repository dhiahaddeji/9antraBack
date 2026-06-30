package com.esprit.springjwt.mapper;

import com.esprit.springjwt.dto.CourseStatsDto;

public class CourseStatsMapper {

    public static CourseStatsDto mapToDto(String name, Integer numberOfAttendees) {
        return CourseStatsDto.builder()
                .CourseName(name)
                .CourseAttendees(numberOfAttendees)
                .build();
    }
}
