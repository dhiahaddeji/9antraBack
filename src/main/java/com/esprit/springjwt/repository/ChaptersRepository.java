package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Chapters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChaptersRepository extends JpaRepository<Chapters, Long> {


    List<Chapters> findByFormationNomFormation(String nomFormation);
}
