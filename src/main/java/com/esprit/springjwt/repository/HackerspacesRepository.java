package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Hackerspaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HackerspacesRepository extends JpaRepository<Hackerspaces,Long> {
    //query select * from Hackerspaces where region = region
    @Query("select h from Hackerspaces h where h.Region = ?1")
    Hackerspaces getHackerspacesByRegion(String Region);



}
