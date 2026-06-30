package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Path;
import com.esprit.springjwt.entity.e_learning.Report;
import com.esprit.springjwt.exception.FileTypeErrorException;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.repository.e_learning.IPathRepository;
import com.esprit.springjwt.service.NotificationService;

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
import java.util.List;

@Service
@Slf4j
public class PathServiceImpl implements IPathService {

    @Autowired
    IPathRepository pathRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Value("${files.folder}")
    String filesFolder;


    @Override
    public List<Path> getAll() {

        List<Path> paths = pathRepository.findAll();

        if(paths.isEmpty())
            throw new RecordNotFoundException("No Paths found");

        return paths;
    }

    @Override
    public Path addPath(MultipartFile image, Path p) {

        Path sp = new Path();

        String contentTypeImage = image.getContentType();

        if (contentTypeImage != null && (
                contentTypeImage.equals("image/jpeg") ||
                        contentTypeImage.equals("image/jpg") ||
                        contentTypeImage.equals("image/png") ||
                        contentTypeImage.equals("image/avif")
        )) {
            sp = pathRepository.save(p);
            boolean isExit = new File(filesFolder + "/Paths/").exists();
            if(!isExit)
                new File (filesFolder + "/Paths/").mkdir();

            String pathStorage = filesFolder + "/Paths/Path_" + sp.getId();

            File pFolder = new File(pathStorage);
            if (!pFolder.exists()) {
                boolean created = pFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create Path folder");
                }
            }

            String pathImage = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();

            try {
                FileUtils.writeByteArrayToFile(new File( pathStorage + "\\" + pathImage) , image.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sp.setImage(pathImage);

            pathRepository.save(sp);
            
            notificationService.sendNotifToAllUsers("New path is released ! check it now", "./paths/details/"+sp.getId(), "New path");

        } else
            throw new FileTypeErrorException("image type is not allowed");

        return sp;
    }

    @Override
    @Transactional
    public Path updatePath(MultipartFile image, Path p) throws FileNotFoundException {

        Path path = pathRepository.findById(p.getId()).orElseThrow(() -> new RecordNotFoundException("Path Not found"));

        String contentTypeImage = image.getContentType();

        if (contentTypeImage != null && (
                contentTypeImage.equals("image/jpeg") ||
                        contentTypeImage.equals("image/jpg") ||
                        contentTypeImage.equals("image/png") ||
                        contentTypeImage.equals("image/avif")
        )) {

            boolean isExit = new File(filesFolder + "/Paths/").exists();
            if(!isExit)
                throw new FileNotFoundException("Paths folder not found !!");

            File originalImage = new File( filesFolder + "/Paths/Path_" + path.getId() + "\\" + path.getImage());

            if (originalImage.delete())
                log.info("image deleted successfully");
            else
                log.error("Error while deleting old file");

            String pathStorage = filesFolder + "/Paths/Path_" + path.getId();

            File pFolder = new File(pathStorage);
            if (!pFolder.exists()) {
                boolean created = pFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create Path folder");
                }
            }

            String pathImage;
            if(!path.getImage().equals(image.getOriginalFilename())) {
                pathImage = LocalDateTime.now()
                        .format(DateTimeFormatter.
                                ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();
            } else
                pathImage = path.getImage();

            try {
                FileUtils.writeByteArrayToFile(new File( pathStorage + "\\" + pathImage) , image.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            path = p;
            path.setImage(pathImage);


            return pathRepository.save(path);

        } else
            throw new FileTypeErrorException("image type is not allowed");


    }

    @Override
    @Transactional
    public void deletePath(Long idPath) throws IOException {
        Path p = pathRepository.findById(idPath).orElseThrow(() -> new RecordNotFoundException("Path not found"));

        File pathStorage = new File( filesFolder + "/Paths/Path_" + p.getId());

        pathRepository.deleteById(idPath);
        FileUtils.deleteDirectory(pathStorage);
    }

    @Override
    public Path getOne(Long id) {
        Path p = pathRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Path not found"));

        return p;
    }
}
