package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Candidacy;
import com.esprit.springjwt.entity.Company;
import com.esprit.springjwt.entity.Offers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffersRepository extends JpaRepository<Offers,Long> {
    List<Offers> findByCompany(Company projectOwner);
    List<Offers> findAllByOrderByDateAsc(); // for ascending order

    List<Offers> findAllByOrderByDateDesc(); // for descending order
}
