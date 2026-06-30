package com.esprit.springjwt.repository.e_learning;

import com.esprit.springjwt.entity.e_learning.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDemandRepository extends JpaRepository<Demand, Long> {
}
