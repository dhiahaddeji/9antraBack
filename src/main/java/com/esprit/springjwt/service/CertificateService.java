package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Certificat;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.CertificatRepository;
import com.esprit.springjwt.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CertificateService {
    @Autowired
    private CertificatRepository certificatRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    
    public boolean checkIfCertificatesGenerated(Long groupId) {
        System.out.println("Checking certificates for group: " + groupId);
        long certificateCount = certificatRepository.countByUser_Groups_Id(groupId);
        System.out.println("Certificate count for group " + groupId + ": " + certificateCount);
        return certificateCount > 0;
    }
    
    public List<Certificat> getCertificatByGroupId(Long groupId) {
        Groups group = groupsRepository.findById(groupId).orElse(null);
        if (group != null) {
            List<User> users = group.getEtudiants();
            if (!users.isEmpty()) {
                return certificatRepository.findByUser(users.get(0));
            }
        }
        return null;
    }

    // Save certificate safely with transaction management
    @Transactional
    public void saveCertificate(Certificat certificat) {
        try {
            certificatRepository.save(certificat);
            System.out.println("Certificate saved successfully for student: " + (certificat.getUser() != null ? certificat.getUser().getFirstName() + " " + certificat.getUser().getLastName() : "Unknown"));
        } catch (Exception e) {
            System.err.println("Error saving certificate: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save certificate", e);
        }
    }
}
