package com.esprit.springjwt.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat{
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	 	
	 	private String message;
	 	
 	    @CreationTimestamp
		private Timestamp created_at;
 	    
 	   @ManyToOne(fetch = FetchType.EAGER)
 	   @JoinColumn(name="user_envoi")
 	   User userEnvoi;
 	   
 	  @ColumnDefault("0")
 	  private int status;
 	  
 	  @ManyToOne(fetch = FetchType.EAGER)
 	  @JoinColumn(name="id_group")
 	  Groups groupe;

}
