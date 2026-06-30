package com.esprit.springjwt.service;


import com.esprit.springjwt.entity.Progress;
import com.esprit.springjwt.repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {
    @Autowired
    private ProgressRepository progressRepository;
    public Progress addProgress(Progress progress){ return progressRepository.save(progress);
    }

        public List<Progress> getAllProgress(){return progressRepository.findAll();
    }
    public Progress updateProgress(Progress progress){return progressRepository.save(progress);
    }
    public Progress getProgressById(Long id){return progressRepository.findById(id).get();
    }
    public void deleteProgress(Long id) {
        progressRepository.deleteById(id);
    }
}
