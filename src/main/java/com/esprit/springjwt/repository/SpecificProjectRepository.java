package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.SpecificProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificProjectRepository extends JpaRepository<SpecificProject,Long> {
    List<SpecificProject> findByStatus(boolean status);
    List<SpecificProject> findAllByOrderByDateAsc(); // for ascending order

    List<SpecificProject> findAllByOrderByDateDesc(); // for descending order

}
