package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Contact;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact,Long> {
}
