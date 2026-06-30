package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Categorie;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieServices {

    @Autowired
    private CategorieRepository categorieRepository;

    public Categorie addCategorie(Categorie Categorie){
        return categorieRepository.save(Categorie);
    }
    
    public List<Categorie> getAllCategotries(){
        try {
            return categorieRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching all categories: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }

    public Categorie updateCategorie(Categorie Categorie){
        return categorieRepository.save(Categorie);
    }

    public Categorie getCategorieById(Long id){
        try {
            return categorieRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching category by id: " + e.getMessage());
            return null;
        }
    }

    public void deleteCategorie(Long id){
        try {
            categorieRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
    }
    
    public List<Categorie> getCategoriesByNomCateContains(String nomCate) {
        try {
            return categorieRepository.findByNomCateContains(nomCate);
        } catch (Exception e) {
            System.err.println("Error searching categories: " + e.getMessage());
            return List.of();
        }
    }


   //update categorie
   public Categorie updateCategorie(Long id, Categorie updatedCategorie) {
       try {
           Optional<Categorie> existingCategorieOptional = categorieRepository.findById(id);
           if (existingCategorieOptional.isPresent()) {
               Categorie existingCategorie = existingCategorieOptional.get();
               existingCategorie.setNomCate(updatedCategorie.getNomCate());
               return categorieRepository.save(existingCategorie);
           } else {
               return null;
           }
       } catch (Exception e) {
           System.err.println("Error updating category: " + e.getMessage());
           e.printStackTrace();
           return null;
       }
   }



}
