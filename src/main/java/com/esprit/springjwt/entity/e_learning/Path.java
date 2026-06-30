package com.esprit.springjwt.entity.e_learning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "price")
    private Double price;

    @ElementCollection
    @CollectionTable(name = "path_learning_goals", joinColumns = @JoinColumn(name = "path_id"))
    private Set<Goal> learningGoals;

    @Lob
    @Column(name = "career_development")
    @Type(type = "org.hibernate.type.TextType")
    private String careerDevelopment;


    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "path", cascade =  CascadeType.ALL)
    @JsonIgnore
    private Set<Demand> demands;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Path path = (Path) o;
        return getId() != null && Objects.equals(getId(), path.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
