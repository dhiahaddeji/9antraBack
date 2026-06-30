package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.OfferClient;
import com.esprit.springjwt.entity.Offers;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.repository.OfferClientRepository;
import com.esprit.springjwt.repository.ProjectClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OfferClientService {
    @Autowired
    private OfferClientRepository offerClientRepository;
    public OfferClient save(OfferClient projectClient) {
        return offerClientRepository.save(projectClient);
    }
    public void updateStatus(Long id, boolean newValue) {
        // Recherche de l'objet MyClass correspondant à l'identifiant "id"
        OfferClient myObject = offerClientRepository.findById(id).orElse(null);
        // Vérification que l'objet a été trouvé
        if (myObject != null) {
            // Modification de la propriété "property2"
            myObject.setStatus(newValue);
            offerClientRepository.save(myObject);
        }


    }
    public List<OfferClient> getClaimsByStatus(Boolean status) {
        return offerClientRepository.findByStatus(status);
    }
    public List<OfferClient> findByOffersId(Long adminProjectId) {

        return offerClientRepository.findByOffersId(adminProjectId);
    }
    public Optional<OfferClient> findById(Long id) {
        return offerClientRepository.findById(id);
    }
}
