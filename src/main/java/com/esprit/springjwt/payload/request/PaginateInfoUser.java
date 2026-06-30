package com.esprit.springjwt.payload.request;

import org.springframework.data.domain.Page;

import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginateInfoUser {

	private int[] count_page;
	private Page<Formateur> formateurs;
	int page;
}
