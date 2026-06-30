package com.esprit.springjwt.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChapterDto {
    private Long id;
    private String title;
    private String courseTitle;
}
