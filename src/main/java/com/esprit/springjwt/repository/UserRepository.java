package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.ERole;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.esprit.springjwt.entity.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);


    @Query(value = "SELECT * FROM user WHERE username = ?1 AND enabled = 1", nativeQuery = true)
    Optional<User> findUserByEnabled(String username);

    //update password by email
    @Query(value = "UPDATE user SET password = ?1 WHERE email = ?2", nativeQuery = true)
    Integer updatePasswordByEmail(String password, String email);


    //Query requette get by typeFormation and status
    @Query(value = "SELECT * FROM user u JOIN user_roles us ON u.id = us.user_id JOIN roles r ON us.role_id = r.id WHERE r.name = 'ETUDIANT' and u.type_formation=?1 and enabled=?2", nativeQuery = true)
    List<User> findByTypeFormationAndStatus(String typeFormation, Long status);
    
    @Query(value = "SELECT * FROM user u JOIN user_roles us ON u.id = us.user_id JOIN roles r ON us.role_id = r.id WHERE r.name = 'ETUDIANT'", nativeQuery = true)
    List<User> getAllStudents();
    
    @Query(value = "SELECT * FROM user u JOIN user_roles us ON u.id = us.user_id JOIN roles r ON us.role_id = r.id WHERE r.name = 'ETUDIANT' and u.enabled=?1", nativeQuery = true)
    List<User> getStudentByStatus(Long status);
    
    @Query(value = "SELECT * FROM user u JOIN user_roles us ON u.id = us.user_id JOIN roles r ON us.role_id = r.id WHERE r.name = 'ETUDIANT' and u.type_formation=?1", nativeQuery = true)
    List<User> getStudentByFormation(String formation);
    //Query requette get by role
    @Query(value = "SELECT * FROM user WHERE role = ?1", nativeQuery = true)
    List<User> findByRoles(String role);

    //Query requette get by email
    @Query(value = "SELECT * FROM user WHERE username = ?1", nativeQuery = true)
    User findByEmail(String email);
    @Query(value = "SELECT * FROM user WHERE username = ?1", nativeQuery = true)
    Optional<User> isfindByEmail (String email);
    //query update enabled
    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET enabled = ?1 WHERE id = ?2", nativeQuery = true)
    Integer updateEnabled(Long enabled, Long id);


    @Query("SELECT DISTINCT g FROM Groups g JOIN g.etudiants e WHERE e.id = :userId")
    List<Groups> findGroupsByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ETUDIANT'")
    Long countNumberOfStudents();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :type AND u.enabled = 1")
    Long countNumberOfActivatedAccounts(@Param("type") ERole type);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'FORMATEUR'")
    Long countNUmberOfCoaches();
    
    // Find user by first name and last name (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) = LOWER(?1)")
    List<User> findByFirstNameAndLastName(String fullName);
    
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();
   
}
