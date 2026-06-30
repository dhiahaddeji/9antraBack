package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Chapter;
import com.esprit.springjwt.entity.e_learning.Course;
import com.esprit.springjwt.entity.e_learning.Lesson;
import com.esprit.springjwt.repository.e_learning.IChapterRepository;
import com.esprit.springjwt.repository.e_learning.ICourseRepository;
import com.esprit.springjwt.repository.e_learning.ILessonRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChapterServiceImpl implements IChapterService{

    @Autowired
    IChapterRepository chapterRepository;

    @Autowired
    ICourseRepository courseRepository;

    @Autowired
    ILessonRepository lessonRepository;

    @Value("${files.folder}")
    String filesFolder;


    @Override
    public List<Chapter> getChapters() {
        return chapterRepository.findAll();
    }

    @Override
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Lesson addLesson(Lesson l, Long idChapter, MultipartFile thumbNail, MultipartFile video) {

        Lesson lesson = new Lesson();
        String contentTypeImage = thumbNail.getContentType();
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
            Chapter c = chapterRepository.findById(idChapter).orElseThrow(null);

            l.setChapter(c);
            lesson = lessonRepository.save(l);
            chapterRepository.save(c);

            boolean isExit = new File(filesFolder + "/Lessons/").exists();
            if(!isExit)
                new File (filesFolder + "/Lessons/").mkdir();

            String lessonStorage = filesFolder + "/Lessons/Lesson_" + lesson.getId();

            File lFolder = new File(lessonStorage);
            if (!lFolder.exists()) {
                boolean created = lFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create lesson folder");
                }
            }

            String lessonImage = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + thumbNail.getOriginalFilename();
            String lessonVideo = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + video.getOriginalFilename();

            try {
                FileUtils.writeByteArrayToFile(new File( lessonStorage + "\\" + lessonImage) , thumbNail.getBytes() );
                FileUtils.writeByteArrayToFile(new File( lessonStorage + "\\" + lessonVideo) , video.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            lesson.setThumbNail(lessonImage);
            lesson.setVideoLesson(lessonVideo);

            lessonRepository.save(lesson);
        } else
            throw new IllegalArgumentException("Invalid file type");




        return lesson;
    }

    @Override
    public List<Chapter> getChaptersByCourse(Long idCourse) {
        Course course = courseRepository.findById(idCourse).orElseThrow(null);

        if(!course.getChapters().isEmpty())
            return course.getChapters();

        return null;
    }

    @Override
    public List<Lesson> getLessonsByChapter(Long idChapter) {
        Chapter chapter = chapterRepository.findById(idChapter).orElseThrow(null);

        if(!chapter.getLessons().isEmpty())
            return chapter.getLessons();

        return null;
    }


}
