package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.SpecificProject;
import com.esprit.springjwt.repository.ProjectOwnerRepository;
import com.esprit.springjwt.repository.SpecificProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecificProjectService {
    @Autowired
    private SpecificProjectRepository specificProjectRepository;
    public SpecificProject save(SpecificProject food) {
        return specificProjectRepository.save(food);
    }
    public List<SpecificProject> getAll() {
        return specificProjectRepository.findAll();
    }
    public void updateStatus(Long id, boolean newValue) {
        // Recherche de l'objet MyClass correspondant à l'identifiant "id"
        SpecificProject myObject = specificProjectRepository.findById(id).orElse(null);
        // Vérification que l'objet a été trouvé
        if (myObject != null) {
            // Modification de la propriété "property2"
            myObject.setStatus(newValue);
            specificProjectRepository.save(myObject);
        }


    }
    public List<SpecificProject> getClaimsByStatus(Boolean status) {
        return specificProjectRepository.findByStatus(status);
    }
    public List<SpecificProject> getAllClaimsSortedByDateAsc() {
        return specificProjectRepository.findAllByOrderByDateAsc();
    }
    public List<SpecificProject> getAllClaimsSortedByDateDesc() {
        return specificProjectRepository.findAllByOrderByDateDesc();
    }
    public Optional<SpecificProject> findById(Long id) {
        return specificProjectRepository.findById(id);
    }

}
