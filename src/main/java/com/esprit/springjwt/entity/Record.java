package com.esprit.springjwt.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Record implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    private String videoLink;

    @JsonIgnore

    @ManyToOne(fetch = FetchType.EAGER)
    private Groups groups;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
   
}

