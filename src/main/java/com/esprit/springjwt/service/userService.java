package com.esprit.springjwt.service;
import com.esprit.springjwt.dto.CourseStatsDto;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Service
public class userService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FormateurRepository formateurRepository;

    @Value("${files.folder}")
    String filesFolder;

    public static String UPLOAD_DOCUMENTS = "C:\\Users\\zied1\\OneDrive\\Bureau\\9antra alternative\\Master\\src\\assets\\Documents\\";


    //get All Users
    public List<User> getAllUsers() {
        return userRepository.findAllWithRoles();
    }
    
   


    public User changeEnabledUser(Long id) {
        User user = userRepository.findById(id).get();
        if (user.getEnabled() == 1) {
            user.setEnabled(0);
        } else {
            user.setEnabled(1);
        }
        return userRepository.save(user);
    }
    // add note to user




    public User getUserById(Long id){
        return userRepository.findById(id).get();
    }

    public List<User> findByTypeFormationAndStatus(String typeFormation, Long status) {
    	if(typeFormation.isEmpty()==false && status!=5) {
    		
    		return userRepository.findByTypeFormationAndStatus(typeFormation,status);
    	}
    	else if(typeFormation.isEmpty()==true&& status!=5) {
    		
    		return userRepository.getStudentByStatus(status);
    	}
    	else if(typeFormation.isEmpty()==false && status==5) {
    		
    		return userRepository.getStudentByFormation(typeFormation);
    	}else {
    		
    		return userRepository.getAllStudents();
    	}
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //update user image by id

    public User updateUserImageById(  MultipartFile image ,Long id) throws IOException {


        String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String filesName = currentDate + image.getOriginalFilename();
        byte[] bytes1 = image.getBytes();
        //Path path1 = Paths.get(UPLOAD_DOCUMENTS + filesName);
        Path path1 = Paths.get(filesFolder + "\\Documents\\" + filesName);
        Files.write(path1, bytes1);
        User user = userRepository.findById(id).get();

        user.setImage(filesName);
        return userRepository.save(user);
    }

    public Integer updateEnabled (Long enabled,Long id)
    {
        if (enabled == 1) {
            User user = userRepository.findById(id).get();

            String subject = "Welcome messaoge";
            String body = "A new contact has been added:\n\n" +

                    "Email: " + user.getUsername()+ "\n" +
                    "Password: " + user.getNumeroTel()+ "\n" ;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getUsername()); // Replace with the recipient email address
            message.setSubject(subject);
            message.setText(body);

            emailSender.send(message);    }

    return userRepository.updateEnabled(enabled,id);
}
    public User updateUser(User updatedUser) {
        // Fetch the existing user from the database
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update the fields of the existing user entity with the new values
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            // Hash the new password
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getNumeroTel() != null) {
            existingUser.setNumeroTel(updatedUser.getNumeroTel());
        }
        if (updatedUser.getTypeFormation() != null) {
            existingUser.setTypeFormation(updatedUser.getTypeFormation());
        }
        if (updatedUser.getImage() != null) {
            existingUser.setImage(updatedUser.getImage());
        }
        if (updatedUser.getCountry() != null) {
            existingUser.setCountry(updatedUser.getCountry());
        }
        if (updatedUser.getEnabled() != 0) {
            existingUser.setEnabled(updatedUser.getEnabled());
        }

        // Merge roles only if provided
        if (updatedUser.getRoles() != null) {
            Set<Role> mergedRoles = mergeRoles(existingUser.getRoles(), updatedUser.getRoles());
            existingUser.setRoles(mergedRoles);
        }

        if (updatedUser.getAbout() != null) {
            existingUser.setAbout(updatedUser.getAbout());
        }

        // Save the updated user
        return userRepository.save(existingUser);
    }

    // Method to merge roles without duplicates
    private Set<Role> mergeRoles(Set<Role> existingRoles, Set<Role> updatedRoles) {
        Set<Role> mergedRoles = new HashSet<>(existingRoles);
        mergedRoles.addAll(updatedRoles);
        return mergedRoles;
    }

    public List<Groups> getGroupsForUser(Long userId) {
        return userRepository.findGroupsByUserId(userId);
    }


    public User updateSimpleuser(User user){
        return userRepository.save(user);
    }

    public Long getNumberOfAccounts(String type) {

        if(type.equals("Coach"))
            return userRepository.countNUmberOfCoaches();

        return userRepository.countNumberOfStudents();
    }

    public Long getNumberOfActivatedAccounts(ERole type) {

        return userRepository.countNumberOfActivatedAccounts(type);
    }

    public List<CourseStatsDto> getTopCourses() throws NullPointerException{

        //Get all users
        List<User> users = userRepository.findAll();
        List<CourseStatsDto> stats = new ArrayList<>();

        for (User u: users) {
            if (u.getTypeFormation() == null || u.getTypeFormation().isEmpty()) continue;

            //Check if stats is Empty if not, check if that CourseName is existing in it
            if(!stats.isEmpty()){
                Optional<CourseStatsDto> foundCourse = stats.stream()
                        .filter(courseStats -> u.getTypeFormation().equals(courseStats.getCourseName()))
                        .findFirst();

                //if Course is found increment its number of attendees
                if(foundCourse.isPresent()) {
                    CourseStatsDto courseToModify = foundCourse.get();
                    stats.remove(courseToModify);
                    courseToModify.setCourseAttendees( courseToModify.getCourseAttendees() + 1);
                    stats.add(courseToModify);
                } else  {
                    //if not found just Add to stats
                    stats.add(new CourseStatsDto(u.getTypeFormation(), 1));
                }
            } else {
                //if stats is Empty just add the first course with 1 attendee
                stats.add(new CourseStatsDto(u.getTypeFormation(), 1));
            }
        }

        //Sort and get only the first five
        if(!stats.isEmpty()) {
            Comparator<CourseStatsDto> byScoreDesc = Comparator.comparingInt(CourseStatsDto::getCourseAttendees).reversed();
            stats = stats.stream()
                    .sorted(byScoreDesc)
                    .limit(5)
                    .collect(Collectors.toList());
        }
        //return stats
        return stats;
    }
    
    public void VerifyEmail(String email) throws Exception {
		  User user=userRepository.findByEmail(email);
		  if(user==null) {
			  throw new Exception("User Not Found");
		  }else {
			  if(user.getEmail_verified_at()!=null) {
				  throw new Exception("User Already Verified");
			  }else {
				  user.setEmail_verified_at(new Date());

				  userRepository.save(user);
			  }
		  }
	  }
    public String saveImageFromUrl(String imageUrl) {
        try {
            String filename = UUID.randomUUID().toString() + ".png";
            Path uploadDir = Paths.get(filesFolder);
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(filename);
            try (InputStream inputStream = new URL(imageUrl).openStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (Exception e) {
            return "avatarStudent.png";
        }
    }
}
