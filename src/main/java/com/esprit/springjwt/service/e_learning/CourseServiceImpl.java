package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.repository.e_learning.IChapterRepository;
import com.esprit.springjwt.repository.e_learning.ICourseRepository;
import com.esprit.springjwt.repository.e_learning.ILessonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CourseServiceImpl implements ICourseService{

    @Autowired
    ICourseRepository courseRepository;

    @Autowired
    IChapterRepository chapterRepository;

    @Autowired
    ILessonRepository lessonRepository;

    @Value("${files.folder}")
    String filesFolder;

    @Autowired
    UserRepository userRepository;

    @Override
    public Course addCourse(MultipartFile image, MultipartFile video, Course c) {

        Course sc = new Course();
        String contentTypeImage = image.getContentType();
        String contentTypeVideo = video.getContentType();
        if (contentTypeImage != null && (
                contentTypeImage.equals("image/jpeg") ||
                        contentTypeImage.equals("image/jpg") ||
                        contentTypeImage.equals("image/png") ||
                        contentTypeImage.equals("image/avif")
        ) && contentTypeVideo != null &&(
                contentTypeVideo.equals("video/mp4") ||
                        contentTypeVideo.equals("video/webm") ||
                        contentTypeVideo.equals("video/ogg") ||
                        contentTypeVideo.equals("video/x-msvideo") ||
                        contentTypeVideo.equals("video/quicktime") ||
                        contentTypeVideo.equals("video/mpeg") ||
                        contentTypeVideo.equals("video/3gpp") ||
                        contentTypeVideo.equals("video/x-ms-asf") ||
                        contentTypeVideo.equals("video/x-flv") ||
                        contentTypeVideo.equals("video/x-ms-wmv")
                )) {

            sc = courseRepository.save(c);
            boolean isExit = new File(filesFolder + "/Courses_E_Learning/").exists();
            if(!isExit)
                new File (filesFolder + "/Courses_E_Learning/").mkdir();

            String coursesStorage = filesFolder + "/Courses_E_Learning/Course_" + sc.getId();

            File cFolder = new File(coursesStorage);
            if (!cFolder.exists()) {
                boolean created = cFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create Course folder");
                }
            }

            String courseImage = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();
            String courseTrailer = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + video.getOriginalFilename();

            try {
                FileUtils.writeByteArrayToFile(new File( coursesStorage + "\\" + courseImage) , image.getBytes() );
                FileUtils.writeByteArrayToFile(new File( coursesStorage + "\\" + courseTrailer) , video.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sc.setImage(courseImage);
            sc.setTrailer(courseTrailer);

            courseRepository.save(sc);


        } else
            throw new IllegalArgumentException("Invalid file type");



        return sc;
    }

    @Override
    @Transactional
    public Chapter addChapter(Chapter chapter, Long idCourse) {

        Course c = courseRepository.findById(idCourse).get();

        if(c != null) {
            chapter.setCourse(c);
            chapter = chapterRepository.save(chapter);
            courseRepository.save(c);

            return chapter;
        }
        return null;
    }


    @Override
    public Course updateCourse(MultipartFile trailer, MultipartFile image, Course c) throws Exception {
        Course cc = courseRepository.findById(c.getId()).orElseThrow(null);
        if( cc != null) {
            String contentTypeImage = image.getContentType();
            String contentTypeVideo = trailer.getContentType();
            if (contentTypeImage != null && (
                    contentTypeImage.equals("image/jpeg") ||
                            contentTypeImage.equals("image/jpg") ||
                            contentTypeImage.equals("image/png") ||
                            contentTypeImage.equals("image/avif")
            ) && contentTypeVideo != null &&(
                    contentTypeVideo.equals("video/mp4") ||
                            contentTypeVideo.equals("video/webm") ||
                            contentTypeVideo.equals("video/ogg") ||
                            contentTypeVideo.equals("video/x-msvideo") ||
                            contentTypeVideo.equals("video/quicktime") ||
                            contentTypeVideo.equals("video/mpeg") ||
                            contentTypeVideo.equals("video/3gpp") ||
                            contentTypeVideo.equals("video/x-ms-asf") ||
                            contentTypeVideo.equals("video/x-flv") ||
                            contentTypeVideo.equals("video/x-ms-wmv")
            )) {
                boolean isExit = new File(filesFolder + "/Courses_E_Learning/").exists();
                if(!isExit)
                    throw new FileNotFoundException("Course folder not found !!");
                File originalImage = new File( filesFolder + "/Courses_E_Learning/Course_" + cc.getId() + "\\" + cc.getImage());
                File orirginalTrailer = new File( filesFolder + "/Courses_E_Learning/Course_" + cc.getId() + "\\" + cc.getTrailer());
                if (originalImage.delete())
                    log.info("image deleted successfully");
                else
                    log.error("Error while deleting old image");

                if (orirginalTrailer.delete())
                    log.info("Trailer deleted successfully");
                else
                    log.error("Error while deleting old trailer");

                String coursesStorage = filesFolder + "/Courses_E_Learning/Course_" + cc.getId();

                File cFolder = new File(coursesStorage);
                if (!cFolder.exists()) {
                    boolean created = cFolder.mkdirs();
                    if (!created) {
                        throw new RuntimeException("Failed to create Course folder");
                    }
                }

                String courseImage;
                if(!cc.getImage().equals(image.getOriginalFilename())) {
                    courseImage = LocalDateTime.now()
                            .format(DateTimeFormatter.
                                    ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();
                } else
                    courseImage = cc.getImage();

                String courseTrailer;
                if( !cc.getTrailer().equals(trailer.getOriginalFilename())) {
                    courseTrailer = LocalDateTime.now()
                            .format(DateTimeFormatter.
                                    ofPattern("yyyyMMddHHmmss")) + "_" + trailer.getOriginalFilename();
                } else
                    courseTrailer = cc.getTrailer();


                try {
                    FileUtils.writeByteArrayToFile(new File( coursesStorage + "\\" + courseImage) , image.getBytes() );
                    FileUtils.writeByteArrayToFile(new File( coursesStorage + "\\" + courseTrailer) , trailer.getBytes() );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                cc.setGoal(c.getGoal());
                cc.setDuration(c.getDuration());
                cc.setDescription(c.getDescription());
                cc.setLanguage(c.getLanguage());
                cc.setTitle(c.getTitle());
                cc.setImage(courseImage);
                cc.setTrailer(courseTrailer);

                return courseRepository.save(cc);


            } else {
                throw new IllegalArgumentException("Invalid file type");
            }
        } else {
            throw new Exception("Invalid Course");
        }
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(null);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) throws Exception {

        Course c = courseRepository.findById(id).orElseThrow(null);
        File originalImage = new File( filesFolder + "/Courses_E_Learning/Course_" + c.getId());

        FileUtils.deleteDirectory(originalImage);
        courseRepository.deleteById(id);
    }

    @Override
    public Course updateCourseWithoutImage(Course c) {
        return null;
    }

    @Override
    public void addTrailerToCourse(MultipartFile file, Long id) {


        String contentType = file.getContentType();
        if (contentType != null && (
                contentType.equals("video/mp4") ||
                        contentType.equals("video/webm") ||
                        contentType.equals("video/ogg") ||
                        contentType.equals("video/x-msvideo") ||
                        contentType.equals("video/quicktime") ||
                        contentType.equals("video/mpeg") ||
                        contentType.equals("video/3gpp") ||
                        contentType.equals("video/x-ms-asf") ||
                        contentType.equals("video/x-flv") ||
                        contentType.equals("video/x-ms-wmv")
        )) {

            Course course = courseRepository.findById(id).orElseThrow(null);
            if(course != null) {
                boolean isExit = new File(filesFolder + "/Courses_E_Learning/").exists();
                if(!isExit)
                    new File (filesFolder + "/Courses_E_Learning/").mkdir();

                String coursesStorage = filesFolder + "/Courses_E_Learning/Course_" + id;

                File cFolder = new File(coursesStorage);
                if (!cFolder.exists()) {
                    boolean created = cFolder.mkdirs();
                    if (!created) {
                        throw new RuntimeException("Failed to create Course folder");
                    }
                }

                String courseTrailer = LocalDateTime.now()
                        .format(DateTimeFormatter.
                                ofPattern("yyyyMMddHHmmss")) + "_" + file.getOriginalFilename();

                try {
                    FileUtils.writeByteArrayToFile(new File( coursesStorage + "\\" + courseTrailer) , file.getBytes() );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //course.setTrailer(courseTrailer);
                courseRepository.setTrailer(courseTrailer, id);
                //return courseRepository.save(course);
            }
        } else
            throw new IllegalArgumentException("Invalid file type");

    }

	@Override
	public void addCourseToWishList(Long user_id, Long course_id) throws Exception {
		Course course = courseRepository.getById(course_id);
		User user = userRepository.getById(user_id);
		if(course==null) {
			throw new Exception("course not found");
		}
		
		if(user==null) {
			throw new Exception("user not found");
		}
		List<Course> courseList = new ArrayList<>();
		courseList = user.getCourses();
		courseList.add(course);
		user.setCourses(courseList);
		userRepository.save(user);
	}

	@Override
	public void deleteCourseToWishList(Long user_id, Long course_id) throws Exception {
		User user = userRepository.getById(user_id);
		if(user==null) {
			throw new Exception("user not found");
		}
		List<Course> courses = new ArrayList<>();
		courses = user.getCourses();
		for(Course c :courses) {
			if(c.getId()==course_id) {
			courses.remove(c);
			user.setCourses(courses);
			userRepository.save(user);
			}
		}
		
	}


}
