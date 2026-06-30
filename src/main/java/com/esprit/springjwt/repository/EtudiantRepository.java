package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    @Query("Select e from Etudiant e where e.name like %:name%")
    Etudiant findByName(String name);
}
