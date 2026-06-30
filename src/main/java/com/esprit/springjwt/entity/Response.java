package com.esprit.springjwt.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String content;
	
	@CreationTimestamp
	private Timestamp created_at;
	
	 @ColumnDefault("0")
	 private int status;
	 
	 @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="user_envoi")
   User userEnvoi;
}
