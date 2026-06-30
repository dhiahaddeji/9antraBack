package com.esprit.springjwt.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LessonDto {
    private Long id;
    private String title;
    private String videoLesson;
    private String thumbNail;
    private String chapterTitle;
}
