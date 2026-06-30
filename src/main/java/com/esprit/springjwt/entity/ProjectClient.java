package com.esprit.springjwt.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectClient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private int numtel;
    public String remark ;
    private String email ;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")

    private LocalDateTime date = LocalDateTime.now();
    @Value("#{false}")
    private boolean status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_client_admin_projects",
            joinColumns = @JoinColumn(name = "project_client_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_projects_id")
    )
    private List<AdminProjects> adminProjects;

    public void addAdminProject(AdminProjects adminProject) {
        if (adminProjects == null) {
            adminProjects = new ArrayList<>();
        }
        adminProjects.add(adminProject);
    }
    public void removeAdminProject(AdminProjects adminProject) {
        if (adminProjects != null) {
            adminProjects.remove(adminProject);
        }
    }


}
