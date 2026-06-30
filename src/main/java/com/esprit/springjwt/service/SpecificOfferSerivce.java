package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Offers;
import com.esprit.springjwt.entity.SpecificOffer;
import com.esprit.springjwt.entity.SpecificProject;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.CompanyRepository;
import com.esprit.springjwt.repository.SpecificOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service

public class SpecificOfferSerivce {
    @Autowired
    SpecificOfferRepository specificOfferRepository;
    public SpecificOffer save(SpecificOffer food) {
        return specificOfferRepository.save(food);
    }
    public List<SpecificOffer> getAll() {
        return specificOfferRepository.findAll();
    }
    public Optional<SpecificOffer> findById(Long id) {
        return specificOfferRepository.findById(id);
    }
    public List<SpecificOffer> getAllClaimsSortedByDateAsc() {
        return specificOfferRepository.findAllByOrderByDateAsc();
    }
    public List<SpecificOffer> getAllClaimsSortedByDateDesc() {
        return specificOfferRepository.findAllByOrderByDateDesc();
    }
}
