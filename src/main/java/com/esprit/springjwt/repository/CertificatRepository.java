package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Certificat;
import com.esprit.springjwt.entity.Etudiant;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificatRepository extends JpaRepository<Certificat, Long> {
   Certificat findByUserAndMonthAndPeriode(User user, String month, String periode);
   List<Certificat> findByUser(User group);


   long countByUser_Groups_Id(Long groupId);


}
