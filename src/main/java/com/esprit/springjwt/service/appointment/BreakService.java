package com.esprit.springjwt.service.appointment;

import com.esprit.springjwt.entity.appointment.Break;
import com.esprit.springjwt.repository.appointment.BreakRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BreakService {
    @Resource
    private BreakRepository breakRepository;

    public List<Break> getAllBreaks() {
        return breakRepository.findAll();
    }

    public Break addBreak(Break breakz) {
        return breakRepository.save(breakz);
    }

    public void deleteBreak(Long id) {
        breakRepository.deleteById(id);
    }
}
