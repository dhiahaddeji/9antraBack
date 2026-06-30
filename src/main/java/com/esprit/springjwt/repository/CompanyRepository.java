package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    List<Company> findByStatus(boolean status);
    List<Company> findAllByOrderByDateAsc(); // for ascending order

    List<Company> findAllByOrderByDateDesc(); // for descending order


}
