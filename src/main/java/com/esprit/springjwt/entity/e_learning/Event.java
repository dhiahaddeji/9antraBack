package com.esprit.springjwt.entity.e_learning;

import com.esprit.springjwt.entity.Role;
import com.esprit.springjwt.entity.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8mb4")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private Double price;

    @Column(name = "image")
    private String image;
    
    @Column(name = "place")
    private String place;
    
    

    @Column(name = "facebook_link")
    private String facebookLink;

    @Column(name = "google_meet_link")
    private String googleMeetLink;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_events",
    joinColumns = @JoinColumn(name = "events_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) return false;
//        Event event = (Event) o;
//        return getId() != null && Objects.equals(getId(), event.getId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return getClass().hashCode();
//    }
}
