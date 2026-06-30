package com.esprit.springjwt.entity.appointment;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
 @Data
public class WorkHours  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; // e.g., "Monday"
    private String start; // e.g., "09:00"
    private String end; // e.g., "17:00"

}
