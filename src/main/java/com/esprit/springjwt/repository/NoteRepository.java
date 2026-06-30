package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    // Custom query methods, if needed
}