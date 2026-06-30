package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestStatus;

import java.util.List;
import java.util.Optional;

import com.esprit.springjwt.entity.e_learning.PaiementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IRequestRepository extends JpaRepository<Request, Long> {

    @Modifying
    @Query("update Request r set r.requestStatus = :status where r.id = :id")
    void changeStatus(@Param("status") RequestStatus status, @Param("id") Long id);
    @Modifying
    @Query("update Request r set r.trainingPeriod = :period where r.id = :id")
    void changePeriod(@Param("period") String period, @Param("id") Long id);
    @Modifying
    @Query("update Request r set r.paiementType = :paytype where r.id = :id")
    void changePaytype(@Param("paytype") PaiementType paytype, @Param("id") Long id);
    @Modifying
    @Query("update Request r set r.educationPlace = :educationplace where r.id = :id")
    void changeEduPlace(@Param("educationplace") String educationplace, @Param("id") Long id);
    
    @Query(value = "SELECT * FROM request WHERE email =:email", nativeQuery = true)
    List<Request> getRequestsByEmail(String email);

    Request findByEmailAndFormationAndTrainingPeriod(String e, Formation formationId,String t);
    List<Request> findByEmailAndFormationAndRequestStatus(String e, Formation formationId,RequestStatus r);
 /*   List<Request> findByEmailAndRequestStatusAndAndPaiementType(String email, RequestStatus status, PaiementType p);*/


}
