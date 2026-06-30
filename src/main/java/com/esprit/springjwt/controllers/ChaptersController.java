package com.esprit.springjwt.controllers;


import com.esprit.springjwt.entity.Chapters;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.service.ChaptersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/chapters")
public class ChaptersController {
    @Autowired
    ChaptersService ChaptersService;

    @GetMapping("/allChapters")
    public List<Chapters> getAllChapters()
    {
        return ChaptersService.getAllChapters();
    }
    //add chapters by id formation
@PostMapping("/addChapters/{id}")
    public Chapters addChapters(@RequestBody Chapters Chapters,@PathVariable("id") Long id){
        return ChaptersService.addChapters(Chapters,id);
    }
    @GetMapping("/getChaptersById/{id}")
    public Chapters getChaptersById(@PathVariable("id") Long id)
    {
        return ChaptersService.getChaptersById(id);
    }
    @DeleteMapping("/deleteChapters/{id}")
    public void deleteChapters(@PathVariable("id") Long id)
    {
        ChaptersService.deleteChapters(id);
    }

    //get chapters by  id formation
    //@GetMapping("/getChaptersByFormationId/{id}")
    //public List<Chapters> getChaptersByFormationId(@PathVariable("id") Long id)
    //{
      //  return ChaptersService.getChaptersByFormationId(id);
   // }

//get chapters by nomFormation
    @GetMapping("/getChaptersByNomFormation/{nomFormation}")
    public List<Chapters> getChaptersByNomFormation(@PathVariable("nomFormation") String nomFormation)
    {
        return ChaptersService.getChaptersByNomFormation(nomFormation);
    }

    @PutMapping("/updateChapter/{id}")
    public ResponseEntity<String> updateChapter(
            @PathVariable Long id,
            @RequestBody Chapters updatedChapter
    ) {
        ChaptersService.updateChapters(updatedChapter, id);
        return ResponseEntity.ok("Chapter updated successfully.");
    }

}




