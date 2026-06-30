package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.GenCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GencodeRepository extends JpaRepository<GenCode, Long> {

  Boolean existsByCode(String email);

}
