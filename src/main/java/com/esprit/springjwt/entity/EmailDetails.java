package com.esprit.springjwt.entity;

// Importing required classes
        import lombok.*;

        import javax.persistence.Entity;

// Annotations
@Data

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// Class
public class EmailDetails {


    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}