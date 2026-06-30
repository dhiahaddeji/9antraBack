package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

//@Query("SELECT fr FROM Formation fr JOIN fr.categorie cat WHERE cat.id = :categoryId")

   // List<Categorie> findByNomCate(String nomCate);


    @Query(value = "SELECT * FROM categories where id=:id", nativeQuery = true)
    Categorie getCategorieById(Long id);


    List<Categorie> findByNomCateContains(String nomCate);
}


