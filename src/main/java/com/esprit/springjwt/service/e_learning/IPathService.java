package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Path;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IPathService {
    List<Path> getAll();

    Path addPath(MultipartFile image, Path p);

    Path updatePath(MultipartFile image, Path p) throws FileNotFoundException;

    void deletePath(Long idPath) throws IOException;

    Path getOne(Long id);
}
