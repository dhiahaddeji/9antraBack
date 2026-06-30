package com.esprit.springjwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.User;

public interface GroupsRepository extends JpaRepository<Groups,Long> {
	boolean existsByGroupNameIgnoreCase(String groupName);
	List<Groups> findByEtudiantsContaining(User user);
    @Query("SELECT g FROM Groups g WHERE g.formation.Id = :formationId")
    List<Groups> findByFormationId(@Param("formationId") Long formationId);
    List<Groups> findByFormateur(User formateur);
    @Query("SELECT g FROM Groups g WHERE g.formateur.id = :userId")
    List<Groups> findByFormateurId(@Param("userId") Long userId);
   
    @Query(value = "SELECT COUNT(*) FROM etudiant_groups where group_id=:id", nativeQuery = true)
    Integer getCountMembersByGroupId(Long id);
    
    @Query(value = "SELECT * FROM groups g JOIN etudiant_groups eg ON g.id = eg.groups_id JOIN user u ON eg.user_id = u.id WHERE u.id =:id", nativeQuery = true)
    List<Groups> getGroupsByStudentId(Long id);
    @Query(value ="SELECT * FROM  etudiant_groups e WHERE e.user_id = :userId", nativeQuery = true)
    List<Groups> findGroupsByUserId( Long userId);

}
