package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.ProjectClient;

import java.util.List;

public interface IProjectClientService {
     ProjectClient createProjectClient(ProjectClient projectClient, List<Long> adminProjectIds);
     ProjectClient add( ProjectClient projectClient);
      ProjectClient addProjectClient(ProjectClient projectClient, List<Long> adminProjectIds) ;

}
