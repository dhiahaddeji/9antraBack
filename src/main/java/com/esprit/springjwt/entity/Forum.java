package com.esprit.springjwt.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Forum {
	

 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 	
 	private String post;
 	
 	private String subject;
 	
	@CreationTimestamp
	private Timestamp created_at;
	
	 @ColumnDefault("0")
	 private int status;
	
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="user_envoi")
   User userEnvoi;
   
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(  name = "forum_responses",
         joinColumns = @JoinColumn(name = "forum_id"),
         inverseJoinColumns = @JoinColumn(name = "response_id"))
   private Set<Response> responses = new HashSet<>();

}
