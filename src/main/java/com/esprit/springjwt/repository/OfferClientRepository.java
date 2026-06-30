package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.OfferClient;
import com.esprit.springjwt.entity.ProjectClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferClientRepository extends JpaRepository<OfferClient, Long> {
    List<OfferClient> findByStatus(boolean status);
    List<OfferClient> findByOffersId(Long adminProjectId);


}
