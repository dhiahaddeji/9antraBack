package com.esprit.springjwt.entity.appointment;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Break  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recurrenceRule;
    private Boolean isBlock;
    private Integer airlineId;

   
}
