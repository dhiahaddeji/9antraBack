package com.esprit.springjwt.repository.e_learning;

import com.esprit.springjwt.entity.e_learning.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPathRepository extends JpaRepository<Path, Long> {
}
