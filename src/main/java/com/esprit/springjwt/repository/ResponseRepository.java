package com.esprit.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

}
