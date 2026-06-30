package com.esprit.springjwt.repository.e_learning;

import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.Event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
@EnableJpaRepositories
public interface IEventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e.image from Event e where e.id = :id")
    String getImageById(@Param("id") Long id);
    
    @Query(value="SELECT * FROM Event",nativeQuery=true)
    List<Event> getAllEvents();
    
    @Query(value="SELECT * FROM Event where id=:id",nativeQuery=true)
    Event getEvent(Long id);

    //List<Event> findEventsByUsers(@Param("user") User user);
    
    @Query(value="select count(*) from user_events where user_id =:id",nativeQuery=true)
   	int getCountEventsByUserId(Long id);
    
    
    @Query(value = "SELECT * FROM event e JOIN user_events uv ON e.id = uv.events_id JOIN user u ON uv.user_id = u.id WHERE u.id =:id", nativeQuery = true)
    List<Event> getEventByUser(Long id);
    
    
}
