package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestPath;

@Repository
public interface RequestPathRepository extends JpaRepository<RequestPath, Long>{
	
	 @Query(value = "SELECT * FROM request_path WHERE email =:email", nativeQuery = true)
	    List<RequestPath> getRequestsByEmail(String email);

}
