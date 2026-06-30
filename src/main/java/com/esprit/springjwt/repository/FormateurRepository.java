package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
	
	 @Query(value="SELECT * FROM formateur",nativeQuery=true)
	 Page<Formateur> getFormateurPaginate(Pageable pageable);
	 
	 Formateur findByUserId(Long userId);
}
