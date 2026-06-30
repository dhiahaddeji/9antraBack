package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Formation;

import com.esprit.springjwt.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation,Long> {



    @Query("SELECT f FROM Formation f WHERE f.nomFormation = ?1")
    Formation findByNomFormation(String nomFormation);

    List<Formation> findByNomFormationContains(String nomFormation);

    @Query("SELECT f FROM Formation f WHERE f.categorie.id = ?1")
    List<Formation> findByCategorieId(Long id);

    List<Formation> findByUserId(Long id);
    
    @Query(value = "SELECT COUNT(*) FROM user_formations u JOIN formation f ON u.formations_id = f.id WHERE f.status = 1 AND u.user_id = :id", nativeQuery = true)
    Integer getCountFormationsInProgressByUserId(Long id);
    
    @Query(value = "SELECT COUNT(*) FROM user_formations u JOIN formation f ON u.formations_id = f.id WHERE f.status = -1 AND u.user_id = :id", nativeQuery = true)
    Integer getCountFormationsCompletedByUserId(Long id);

    @Query(value="select formations_id from user_formations where user_id =:id",nativeQuery=true)
	List<Long> getFormationIdByUserId(Long id);


    @Query(value="select id from formation where user_id =:id",nativeQuery=true)
	List<Long> getFormationIdByCoachId(Long id);
    
    @Query(value="select * from formation where status=1",nativeQuery=true)
    List<Formation> getAllFormations();
    
    @Query(value = "SELECT COUNT(*) FROM formation where status = -1 and user_id=:id", nativeQuery = true)
    Integer getCountFormationsCompletedByCoach(Long id);
    
    @Query(value = "SELECT COUNT(*) FROM formation where status = 1 and user_id=:id", nativeQuery = true)
    Integer getCountFormationsInProgressCoach(Long id);
    
    @Query(value="select * from formation where status=1 or status=0 ",nativeQuery=true)
	Page<Formation> getFormationPaginate(Pageable pageable );
      Formation getFormationByNomFormation(String nom);
    
}
