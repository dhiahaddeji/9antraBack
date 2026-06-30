package com.esprit.springjwt.repository.e_learning;

import com.esprit.springjwt.entity.e_learning.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportRepository extends JpaRepository<Report, Long> {
}
