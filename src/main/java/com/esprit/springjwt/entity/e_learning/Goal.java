package com.esprit.springjwt.entity.e_learning;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class Goal {

    private String description;

    public Goal(String desc) {
        this.description = desc;
    }

    public Goal() {

    }
}
