package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICourseService {

    Course addCourse(MultipartFile image, MultipartFile video, Course c);

    Chapter addChapter(Chapter chapter, Long idCourse);

    Course updateCourse(MultipartFile trailer, MultipartFile image, Course c) throws Exception;

    List<Course> getAll();

    Course getCourseById(Long id);

    void deleteCourse(Long id) throws Exception;

    Course updateCourseWithoutImage(Course c);

    void addTrailerToCourse(MultipartFile file, Long id);
    
    void addCourseToWishList(Long user_id , Long course_id) throws Exception;
    
    void deleteCourseToWishList(Long user_id , Long course_id) throws Exception;
}
