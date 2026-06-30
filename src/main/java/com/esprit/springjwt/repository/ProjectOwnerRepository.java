package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.ProjectOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectOwnerRepository extends JpaRepository<ProjectOwner,Long> {
    List<ProjectOwner> findByStatus(boolean status);

}
