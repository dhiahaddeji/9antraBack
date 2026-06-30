package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Categorie;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.service.CategorieServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categorie")
public class CategorieController {

    @Autowired
    CategorieServices categorieServices;


    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories()
    {
        try {
            List<Categorie> categories = categorieServices.getAllCategotries();
            if (categories.isEmpty()) {
                System.out.println("No categories found in database");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            System.err.println("Error in getAllCategories endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching categories: " + e.getMessage());
        }
    }

    @PostMapping("/addCategories")
    public Categorie addCategories(@RequestBody Categorie categorie){
        return categorieServices.addCategorie(categorie);
    }

    @GetMapping("/getCategoriesById/{id}")
    public Categorie getCategorieById(@PathVariable("id") Long id)
    {
        return categorieServices.getCategorieById(id);
    }

    /*@PutMapping("/updateCategories")
    public Categorie updateCategories(@RequestBody Categorie categorie){
        return categorieServices.updateCategorie(categorie);
    }*/

    @DeleteMapping("/deleteCategories/{id}")
    public void deleteCategories(@PathVariable("id") Long id){
         categorieServices.deleteCategorie(id);
    }

    @GetMapping("/categories/search/{nomCate}")
    public List<Categorie> getCategoriesByNomCateContains(@PathVariable String nomCate) {
        return categorieServices.getCategoriesByNomCateContains(nomCate);
    }


    @PutMapping("/updateCategories/{id}")
    public ResponseEntity<Categorie> updateCategories(@PathVariable("id") Long id, @RequestBody Categorie updatedCategorie) {
        Categorie updatedCategory = categorieServices.updateCategorie(id, updatedCategorie);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        } else {
            // Category with the given ID not found
            return ResponseEntity.notFound().build();
        }
    }
}
