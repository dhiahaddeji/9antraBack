package com.esprit.springjwt.repository;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecordRepository extends JpaRepository<Record, Long>  {


    //query request get all records by group id
    @Query("select r from Record r where r.groups.id = ?1")
    Iterable<Record> findByGroups(Long groupId);
}
