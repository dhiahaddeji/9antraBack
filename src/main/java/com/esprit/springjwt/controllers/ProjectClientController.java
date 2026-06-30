package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.AdminProjectsRepository;
import com.esprit.springjwt.repository.ProjectClientRepository;
import com.esprit.springjwt.service.AdminProjectsService;
import com.esprit.springjwt.service.ProjectClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ProjectClient")
@CrossOrigin("*")
public class ProjectClientController {
    @Autowired
    ProjectClientService projectClientServices;
    @Autowired
    ProjectClientRepository projectClientRepository;
    @Autowired
    AdminProjectsService adminProjectsService;
    @Autowired
    private AdminProjectsRepository adminProjectsRepository;

    /*******add****/

    @PostMapping("/add/{adminProjectId}")
    public ProjectClient add(@RequestBody ProjectClient projectClient, @PathVariable("adminProjectId") Long adminProjectId) {
        AdminProjects adminProject = adminProjectsService.findById(adminProjectId)
                .orElseThrow(() -> new ResourceNotFoundException("AdminProjects not found with ID: " + adminProjectId));

        projectClient.addAdminProject(adminProject);
        projectClient = projectClientServices.save(projectClient);

        adminProject.addProjectClient(projectClient);
        adminProjectsService.save(adminProject);

        return projectClient;
    }
    /*******DeleteRelation****/

    @DeleteMapping("/removeRelation/{adminProjectId}/{projectClientId}")
    public ProjectClient removeRelation(@PathVariable("adminProjectId") Long adminProjectId, @PathVariable("projectClientId") Long projectClientId) {
        AdminProjects adminProject = adminProjectsService.findById(adminProjectId)
                .orElseThrow(() -> new ResourceNotFoundException("AdminProjects not found with ID: " + adminProjectId));

        ProjectClient projectClient = projectClientServices.findById(projectClientId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectClient not found with ID: " + projectClientId));

        adminProject.removeProjectClient(projectClient);
        adminProjectsService.save(adminProject);

        projectClient.removeAdminProject(adminProject);
        projectClientServices.save(projectClient);

        return projectClient;
    }
    /*******DeleteClient****/

    @DeleteMapping("/removeClient/{projectClientId}")
    public ProjectClient remove(@PathVariable("projectClientId") Long projectClientId) {
        ProjectClient projectClient = projectClientServices.findById(projectClientId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectClient not found with ID: " + projectClientId));

        List<AdminProjects> adminProjects = projectClient.getAdminProjects();
        if (adminProjects != null && !adminProjects.isEmpty()) {
            for (AdminProjects adminProject : adminProjects) {
                adminProject.removeProjectClient(projectClient);
                adminProjectsService.save(adminProject);
            }
        }

        projectClientServices.delete(projectClient);

        return projectClient;
    }
/*******Update****/
    @PutMapping("/update/{projectClientId}")
    public ProjectClient update(@PathVariable("projectClientId") Long projectClientId, @RequestBody ProjectClient updatedProjectClient) {
        ProjectClient projectClient = projectClientServices.findById(projectClientId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectClient not found with ID: " + projectClientId));

        List<AdminProjects> updatedAdminProjects = updatedProjectClient.getAdminProjects();
        if (updatedAdminProjects != null && !updatedAdminProjects.isEmpty()) {
            projectClient.setAdminProjects(updatedAdminProjects);
        }
        projectClient.setNom(updatedProjectClient.getNom());
        projectClient.setNumtel(updatedProjectClient.getNumtel());
        projectClient.setRemark(updatedProjectClient.getRemark());
        projectClient.setEmail(updatedProjectClient.getEmail());

        projectClient = projectClientServices.save(projectClient);

        return projectClient;
    }
    @GetMapping("/All")
    @ResponseBody
    public List<ProjectClient> getAll(){
        return projectClientServices.getAll();
    }
    @GetMapping("/adminProjects/{adminProjectId}/projectClients")
    @ResponseBody
    public List<ProjectClient> getProjectClientsByAdminProjectId(@PathVariable("adminProjectId") Long adminProjectId) {
        return projectClientServices.findByAdminProjectId(adminProjectId);
    }
    @PutMapping ("/updateStatus/{id}")

    public ResponseEntity<Void> UpdateComplaintAdmin(@PathVariable Long id, @RequestParam boolean status) {
        projectClientServices.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getStatus/{status}")
    public ResponseEntity<List<ProjectClient>> getClaimsByStatus(@PathVariable boolean status) {
        List<ProjectClient> claims = projectClientServices.getClaimsByStatus(status);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<ProjectClient>> getClaimsSortedByDate(@PathVariable String order) {
        List<ProjectClient> claims;
        if (order.equalsIgnoreCase("asc")) {
            claims = projectClientServices.getAllClaimsSortedByDateAsc();
        } else if (order.equalsIgnoreCase("desc")) {
            claims = projectClientServices.getAllClaimsSortedByDateDesc();
        } else {
            // Handle invalid order parameter
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
        @GetMapping("/project-admins/{id}/project-clients")
        public ResponseEntity<List<ProjectClient>> getProjectClientsByProjectAdminId(@PathVariable long id) {
            AdminProjects projectAdmin = adminProjectsRepository.findById(id);
            if (projectAdmin == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<ProjectClient> projectClients = projectAdmin.getProjectClients();
            return new ResponseEntity<>(projectClients, HttpStatus.OK);
        }


}
