package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Candidacy;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.Company;
import com.esprit.springjwt.repository.CandidacyRepository;
import com.esprit.springjwt.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class CandidacyService {
    @Autowired
    CandidacyRepository candidacyRepository;
    public Candidacy save(Candidacy food) {
        return candidacyRepository.save(food);
    }
    public List<Candidacy> getAll() {
        return candidacyRepository.findAll();
    }
    public Optional<Candidacy > findById(Long id) {
        return candidacyRepository.findById(id);
    }
    public List<Candidacy> getAllClaimsSortedByDateAsc() {
        return candidacyRepository.findAllByOrderByDateAsc();
    }
    public List<Candidacy> getAllClaimsSortedByDateDesc() {
        return candidacyRepository.findAllByOrderByDateDesc();
    }
}
