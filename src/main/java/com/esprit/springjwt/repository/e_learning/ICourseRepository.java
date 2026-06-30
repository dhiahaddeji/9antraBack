package com.esprit.springjwt.repository.e_learning;

import com.esprit.springjwt.entity.e_learning.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {

    @Modifying
    @Query("update Course c set c.trailer = :trailer where c.id = :id")
    void setTrailer(@Param("trailer") String trailer,@Param("id") Long id);
}
