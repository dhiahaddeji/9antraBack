package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.Projects;
import com.esprit.springjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectsRepository extends JpaRepository<Projects,Long> {
    List<Projects> findByUser(User user);


}
