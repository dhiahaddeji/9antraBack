    package com.esprit.springjwt.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import lombok.*;

    import javax.persistence.*;
    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.List;

    @Data
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class AdminProjects implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String titre;
        private String description;
        private String technologies ;
        private String video ;
        private String image ;
        private float price  ;
        private String name;
        private String ownerImage;

        @ManyToOne
        @JsonIgnore
        public ProjectOwner projectOwner;
        @ManyToMany(mappedBy = "adminProjects")
        @JsonIgnore

        private List<ProjectClient> projectClients;
        public void addProjectClient(ProjectClient projectClient) {
            if (projectClients == null) {
                projectClients = new ArrayList<>();
            }
            projectClients.add(projectClient);
        }

        public void removeProjectClient(ProjectClient projectClient) {
            if (projectClients != null) {
                projectClients.remove(projectClient);
            }
        }


    }
