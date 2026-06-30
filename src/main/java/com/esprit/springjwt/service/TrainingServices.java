package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Categorie;
import com.esprit.springjwt.entity.Training;
import com.esprit.springjwt.repository.CategorieRepository;
import com.esprit.springjwt.repository.ChaptersRepository;
import com.esprit.springjwt.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServices{

   /* @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private CategorieRepository categorieRepository;


    public Training addTraining( Training training){

        Categorie categorie = categorieRepository.findById(training.getCategorie().getId()).get();
        training.setCategorie(categorie);
     return   trainingRepository.save(training);
    }

    public void deleteTrainingById(Long id){
        trainingRepository.deleteById(id);
    }

    //updqte Training with checking if the Categorie exist
    public Training updateTraining(Training training){
        Categorie categorie = categorieRepository.findById(training.getCategorie().getId()).get();
        training.setCategorie(categorie);
        return trainingRepository.save(training);
    }


    public Training getTrainingById(Long id){
        return trainingRepository.findById(id).get();
    }

    public List<Training> getAllTraining(){
        return trainingRepository.findAll();
    }

*/

}
