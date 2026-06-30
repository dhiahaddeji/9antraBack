package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Note;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.NoteRepository;
import com.esprit.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public Note addNoteToUser(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Note note = new Note();
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());

        // Set the user for the note to establish the bidirectional relationship
        note.setUser(user);
        user.addNote(note);

        return noteRepository.save(note);
    }

    public List<Note> getAllNotesForUser(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    // Other note-related methods, if needed
}
