package com.esprit.springjwt.entity.e_learning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "responded")
    private Boolean responded;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone_nr")
    @Digits(integer = 8, fraction = 0)
    private String phoneNr;

    @Column(name = "subject", length = 150)
    private String subject;

    @Lob
    @Column(name = "message", columnDefinition = "TEXT CHARACTER SET utf8mb4")
    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private DemandCategory category;



    @ToString.Exclude
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JsonIgnore
    @JoinColumn(name = "path_id")
    private Path path;

}
