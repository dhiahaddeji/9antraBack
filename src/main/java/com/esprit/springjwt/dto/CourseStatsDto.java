package com.esprit.springjwt.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CourseStatsDto {

    private String CourseName;
    private Integer CourseAttendees;
}
