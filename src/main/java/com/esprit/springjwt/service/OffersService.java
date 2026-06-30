package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.Candidacy;
import com.esprit.springjwt.entity.Offers;
import com.esprit.springjwt.repository.AdminProjectsRepository;
import com.esprit.springjwt.repository.OffersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OffersService {
    @Autowired
    private OffersRepository offersRepository;
    @Autowired
    private NotificationService notificationService;
    
    public Offers save(Offers adminProjects) {
    	notificationService.sendNotifToAllUsers("Exciting Announcement: New Offer is out! Register Now!", "./offers-details/"+adminProjects.getId(), "New offer");
        return offersRepository.save(adminProjects);
    }
    public List<Offers> getAll() {
        return (List<Offers>) offersRepository.findAll();
    }
    public Optional<Offers> findById(Long id) {
        return offersRepository.findById(id);
    }
    public void delete(Long id) {
        Offers project = offersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));


        // Supprimer l'entrée de la base de données
        offersRepository.deleteById(id);
    }
    public List<Offers> getAllClaimsSortedByDateAsc() {
        return offersRepository.findAllByOrderByDateAsc();
    }
    public List<Offers> getAllClaimsSortedByDateDesc() {
        return offersRepository.findAllByOrderByDateDesc();
    }
}
