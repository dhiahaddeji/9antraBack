package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Certificat")
public class Certificat {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idCertificat;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String periode;
    private String month;
    private String path;



    public Certificat(String periode, String month) {
        this.periode = periode;
        this.month = month;
    }
    @Column(name = "user_or_group_id")
    private Long userOrGroupId;

}
