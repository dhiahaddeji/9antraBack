package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Lesson;
import com.esprit.springjwt.repository.e_learning.ILessonRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class LessonServiceImpl implements ILessonService {

    @Autowired
    ILessonRepository lessonRepository;

    @Value("${files.folder}")
    String filesFolder;

    @Override
    public List<Lesson> getLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public void deleteLesson(Long id) throws IOException {
        Lesson l = lessonRepository.findById(id).orElseThrow(null);
        File lessonStorage = new File( filesFolder + "/Lessons/Lesson_" + l.getId());

        FileUtils.deleteDirectory(lessonStorage);
        lessonRepository.deleteById(id);
    }

    @Override
    public Lesson getOne(Long id) {
        return lessonRepository.findById(id).orElseThrow(null);
    }


}
