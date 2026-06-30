package com.esprit.springjwt.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chapters implements Serializable {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private String Title ;
    @Column(columnDefinition = "LONGTEXT")
    private String Description ;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idFormation")
    private Formation formation;

}



