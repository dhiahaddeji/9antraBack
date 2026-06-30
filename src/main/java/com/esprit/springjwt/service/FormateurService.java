package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormateurService {


    @Autowired
    private FormateurRepository formateurRepository;

    @Autowired
    private UserRepository userRepository;

    public Formateur addFormateur(Formateur formateur) {
        return formateurRepository.save(formateur);
    }
    
    public Page<Formateur> getAllFormateurPaginate(Pageable pageable) {
        return formateurRepository.getFormateurPaginate(pageable);
    }

    public Formateur updateFormateur(Formateur formateur) {
        return formateurRepository.save(formateur);
    }

    public void deleteFormateur(Long id) {
        formateurRepository.deleteById(id);
    }

    public Formateur getFormateur(Long id) {
        return formateurRepository.findById(id).get();
    }

    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }

    public Formateur updateFormateurByIdUser(Formateur formateur) {


        //if field is null, keep the old value
        Formateur oldFormateur = formateurRepository.findById(formateur.getId()).get();
        if (formateur.getSkills() == null) {
            formateur.setSkills(oldFormateur.getSkills());
        } else {
            formateur.setSkills(formateur.getSkills());
        }

        if (formateur.getGithub() == null) {
            formateur.setGithub(oldFormateur.getGithub());
        } else {
            formateur.setGithub(formateur.getGithub());
        }


        if (formateur.getLinkedin() == null) {
            formateur.setLinkedin(oldFormateur.getLinkedin());
        } else {
            formateur.setLinkedin(formateur.getLinkedin());
        }

        if (formateur.getCV() == null) {
            formateur.setCV(oldFormateur.getCV());
        } else {
            formateur.setCV(formateur.getCV());
        }
        return formateurRepository.save(formateur);
    }

    //get formateur by id

    public Formateur getFormateurbyid(Long id){
        return formateurRepository.findById(id).get();
    }
}
