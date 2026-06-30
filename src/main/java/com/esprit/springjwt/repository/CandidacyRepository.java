package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Candidacy;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidacyRepository extends JpaRepository<Candidacy,Long> {
    List<Candidacy> findAllByOrderByDateAsc(); // for ascending order

    List<Candidacy> findAllByOrderByDateDesc(); // for descending order

}
