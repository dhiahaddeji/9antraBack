package com.esprit.springjwt.controllers;


import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.esprit.springjwt.dto.RequestDto;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.FormationRepository;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.service.*;
import org.springdoc.webmvc.core.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esprit.springjwt.service.FormationService;
import com.esprit.springjwt.service.GroupsService;
import com.esprit.springjwt.service.SessionService;

@RestController
@RequestMapping("/api/groups")
public class GroupsController {
    private final GroupsService groupsService;
    @Autowired
    private FormationService trainingService;
    @Autowired
    private  SessionService sessionService;

    @Autowired
    private userService userService;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    
@Resource
private FormationRepository formationRepository;
@Resource
private IRequestService requestService;

    @Autowired
    public GroupsController(GroupsService groupsService) {
        this.groupsService = groupsService;
    }
   /* @GetMapping("/user/{userId}")
    public List<Groups> findGroupsByUserId(@PathVariable Long userId) {
        return groupsRepository.findGroupsByUserId(userId);
    }*/
    @GetMapping("/all")
    public List<Groups> getAllGroups() {
        return groupsService.getAllGroups();
    }
    
   /* @GetMapping("/session/{sessionId}")

    @GetMapping("/session/{sessionId}")

    public ResponseEntity<List<Groups>> getGroupsBySessionId(@PathVariable Long sessionId) {
        List<Groups> groups = groupsService.getGroupsBySessionId(sessionId);
        if (!groups.isEmpty()) {
            return ResponseEntity.ok(groups);
        }
        return ResponseEntity.noContent().build();

    }*/
   @GetMapping("/session/{sessionId}")
   public ResponseEntity<List<Groups>> getGroupsBySessionId(@PathVariable Long sessionId) {
       List<Groups> groups = groupsService.getGroupsBySessionId(sessionId);
       if (!groups.isEmpty()) {
           return ResponseEntity.ok(groups);
       }
       return ResponseEntity.noContent().build();
   }



    @PostMapping("/add")
    public ResponseEntity<?> addGroups(@Valid @RequestBody Groups groups) {
        try {
            String GroupName = groups.getGroupName();
            boolean groupNameExists = groupsService.checkIfGroupNameExists(GroupName);
            groups.setCertificatesGenerated(false);
            
            // Check if the groupName already exists
            if (groupNameExists) {
                return ResponseEntity.badRequest().body("Group name already exists");
            }
            
            // Validate that required fields are not null
            if (groups.getFormation() == null || groups.getFormation().getId() == null) {
                return ResponseEntity.badRequest().body("Formation is required");
            }
            if (groups.getFormateur() == null || groups.getFormateur().getId() == null) {
                return ResponseEntity.badRequest().body("Formateur is required");
            }
            if (groups.getGroupName() == null || groups.getGroupName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Group name is required");
            }

            Groups createdGroup = groupsService.addGroups(groups);
            //njib list mt3 requests  ken fma request b user paid bnfs lperiod mt3 group yajoutih
            List<RequestDto> requests = requestService.getAll();
            if (!requests.isEmpty()) {
                for (RequestDto request : requests) {
                    Long formationId = formationRepository.getFormationByNomFormation(request.getFormationName()).getId();

                    // Add null check for training period
                    if (request.getTrainingPeriod() != null &&
                            (request.getTrainingPeriod().equals(createdGroup.getPeriod()) ||
                            request.getTrainingPeriod().equals("2months") ||
                            request.getTrainingPeriod().equals("month2")) &&
                            request.getRequestStatus() == RequestStatus.PAID &&
                            formationId == createdGroup.getFormation().getId()) {

                        User user = userService.getUserByEmail(request.getEmail());

                        // Fetch groups where the user is present
                        List<Groups> userGroups = groupsRepository.findAll().stream()
                                .filter(group -> group.getEtudiants().contains(user))
                                .collect(Collectors.toList());
                        
                        // Check if the user is already in a group with the same period and formation
                        boolean userAlreadyInGroupWithSamePeriodAndFormation = userGroups.stream()
                                .anyMatch(group ->
                                        group.getPeriod().equals(createdGroup.getPeriod()) &&
                                                group.getFormation().getId()==(createdGroup.getFormation().getId())
                                );
                        if (!userAlreadyInGroupWithSamePeriodAndFormation) {
                            groupsService.addEtudiantToGroup(createdGroup.getId(), user.getId());
                        }
                    }
                }
            }

            return ResponseEntity.ok(createdGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating group: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Groups getGroupsById(@PathVariable("id") Long id) {
        return groupsService.getGroupsById(id);
    }

    
    @GetMapping("/by-formation/{id}")
    public List<Groups> getGroupsByFormation(@PathVariable("id") Long Id) {
        return groupsService.getGroupsByFormation(Id);
    }
    

    @PutMapping("/update")
    public Groups updateGroups(@Valid @RequestBody Groups groups) {
        groups.setCertificatesGenerated(false);


        return groupsService.updateGroups(groups);
    }

    @DeleteMapping("/{id}")
    public void deleteGroups(@PathVariable("id") Long id) {
        groupsService.deleteGroups(id);
    }

    @PostMapping("/{groupId}/etudiants/{etudiantId}")
    public ResponseEntity<?> addEtudiantToGroup(@PathVariable Long groupId, @PathVariable Long etudiantId) {
        User result= new User();
        try {
           result= groupsService.addEtudiantToGroup(groupId, etudiantId);
            return ResponseEntity.ok( result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{groupId}/etudiants/{etudiantId}")
    public ResponseEntity<?> removeEtudiantFromGroup(@PathVariable Long groupId, @PathVariable Long etudiantId) {
        return ResponseEntity.ok( groupsService.removeEtudiantFromGroup(groupId, etudiantId));
    }


    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Groups>> getGroupsByUserId(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Groups> userGroups = user.getGroups();
        return ResponseEntity.ok(userGroups);
    }

    @GetMapping("/by-formateur/{formateurId}")
    public ResponseEntity<List<Groups>> getGroupsByFormateurId(@PathVariable Long formateurId) {
        List<Groups> groups = groupsService.getGroupsByFormateurId(formateurId);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }
    
   /* @GetMapping("/getCounMembers/{id}")
    public ResponseEntity<?> getCounMembersByGroupId(@PathVariable("id") Long id){
        Integer size = groupsService.getCountMembersByGroupId(id);
		return ResponseEntity.ok(size);
   }*/

    @GetMapping("/getGroupsByStudentId/{id}")
    public ResponseEntity<?> getGroupsByStudentId(@PathVariable("id") Long id){
    	try {
    		List<Groups> groups = groupsService.getGroupsByStudentId(id);
    		return ResponseEntity.ok(groups);
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return ResponseEntity.ok("Error while fetching data");
    	}
    }

    @PostMapping("/{groupId}/add-students")
    public ResponseEntity<?> addStudentsToGroup(@PathVariable Long groupId, @RequestParam List<Long> studentIds) {
        try {
            Groups group = groupsRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            
            int count = 0;
            for (Long studentId : studentIds) {
                User student = userService.getUserById(studentId);
                if (student != null && !group.getEtudiants().contains(student)) {
                    group.getEtudiants().add(student);
                    student.getGroups().add(group);
                    count++;
                }
            }
            
            groupsRepository.save(group);
            System.out.println("Added " + count + " students to group " + groupId);
            return ResponseEntity.ok("Added " + count + " students to the group");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{groupId}/add-students-by-formation")
    public ResponseEntity<?> addStudentsByFormationToGroup(@PathVariable Long groupId) {
        try {
            Groups group = groupsRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            
            if (group.getFormation() == null) {
                return ResponseEntity.badRequest().body("Group has no formation assigned");
            }
            
            // Get all users first
            List<User> allUsers = userService.getAllUsers();
            System.out.println("=== DEBUG START ===");
            System.out.println("Total users in database: " + allUsers.size());
            
            for (User u : allUsers) {
                System.out.println("\nUser ID: " + u.getId() + ", Name: " + u.getFirstName() + " " + u.getLastName());
                System.out.println("  Enabled: " + u.getEnabled());
                System.out.println("  Roles count: " + u.getRoles().size());
                if (u.getRoles() != null && u.getRoles().size() > 0) {
                    for (Role r : u.getRoles()) {
                        System.out.println("    Role: " + r.getName() + " (ID: " + r.getId() + ")");
                    }
                } else {
                    System.out.println("    NO ROLES ASSIGNED!");
                }
            }
            System.out.println("=== DEBUG END ===\n");
            
            // Get all students with ETUDIANT role
            List<User> allStudents = allUsers.stream()
                    .filter(u -> {
                        boolean hasEtudiantRole = u.getRoles() != null && u.getRoles().stream()
                                .anyMatch(r -> r.getName() == ERole.ETUDIANT);
                        if (hasEtudiantRole && u.getEnabled() == 1) {
                            System.out.println("✓ MATCHED - User: " + u.getFirstName() + " " + u.getLastName() + " has ETUDIANT role");
                        }
                        return hasEtudiantRole && u.getEnabled() == 1;
                    })
                    .collect(Collectors.toList());
            
            System.out.println("\nTotal ETUDIANT students found: " + allStudents.size());
            
            int count = 0;
            for (User student : allStudents) {
                // Add student to group only if not already there
                if (!group.getEtudiants().contains(student)) {
                    group.getEtudiants().add(student);
                    student.getGroups().add(group);
                    count++;
                    System.out.println("Added student: " + student.getFirstName() + " " + student.getLastName());
                }
            }
            
            if (count > 0) {
                groupsRepository.save(group);
            }
            System.out.println("Final: Added " + count + " students to group " + groupId);
            return ResponseEntity.ok("Added " + count + " students to the group");
        } catch (Exception e) {
            System.err.println("EXCEPTION in addStudentsByFormationToGroup: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{groupId}/available-students")
    public ResponseEntity<?> getAvailableStudents(@PathVariable Long groupId) {
        try {
            Groups group = groupsRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            
            // Get all students with ETUDIANT role
            List<User> allStudents = userService.getAllUsers().stream()
                    .filter(u -> u.getRoles() != null && u.getRoles().stream()
                            .anyMatch(r -> r.getName() == ERole.ETUDIANT))
                    .filter(u -> u.getEnabled() == 1)
                    .collect(Collectors.toList());
            
            System.out.println("\n=== GET AVAILABLE STUDENTS FOR GROUP " + groupId + " ===");
            System.out.println("Total ETUDIANT students: " + allStudents.size());
            System.out.println("Students already in group " + groupId + ": " + (group.getEtudiants() != null ? group.getEtudiants().size() : 0));
            
            if (group.getEtudiants() != null && group.getEtudiants().size() > 0) {
                System.out.println("Students already in this group:");
                for (User u : group.getEtudiants()) {
                    System.out.println("  - " + u.getFirstName() + " " + u.getLastName() + " (ID: " + u.getId() + ")");
                }
            }
            
            // Get available students (not already in this group)
            List<User> availableStudents = allStudents.stream()
                    .filter(u -> group.getEtudiants() == null || !group.getEtudiants().contains(u))
                    .collect(Collectors.toList());
            
            System.out.println("Available students: " + availableStudents.size());
            for (User u : availableStudents) {
                System.out.println("  ✓ " + u.getFirstName() + " " + u.getLastName() + " (ID: " + u.getId() + ")");
            }
            System.out.println("=== END DEBUG ===\n");
            
            return ResponseEntity.ok(availableStudents);
        } catch (Exception e) {
            System.err.println("EXCEPTION in getAvailableStudents: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all-students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<User> allStudents = userService.getAllUsers().stream()
                    .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ETUDIANT")))
                    .filter(u -> u.getEnabled() == 1)
                    .collect(Collectors.toList());
            
            System.out.println("Total students: " + allStudents.size());
            return ResponseEntity.ok(allStudents);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/create-test-students")
    public ResponseEntity<?> createTestStudents() {
        try {
            // Create 5 test students
            for (int i = 1; i <= 5; i++) {
                String email = "student" + i + "@test.com";
                
                // Check if student already exists
                if (userRepository.existsByUsername(email)) {
                    System.out.println("Student " + email + " already exists");
                    continue;
                }
                
                User student = new User();
                student.setUsername(email);
                student.setPassword(encoder.encode("Student123!"));
                student.setFirstName("Student");
                student.setLastName("Test" + i);
                student.setNumeroTel("216" + String.format("%08d", i));
                student.setTypeFormation("Web Development");
                student.setCountry("Tunisia");
                student.setImage("avatarStudent.png");
                student.setEnabled(1);
                
                Set<Role> roles = new HashSet<>();
                Role studentRole = roleRepository.findByName(ERole.ETUDIANT)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(studentRole);
                student.setRoles(roles);
                
                userRepository.save(student);
                System.out.println("Created test student: " + email);
            }
            
            return ResponseEntity.ok("Test students created successfully!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
