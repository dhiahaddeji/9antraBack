package com.esprit.springjwt.payload.request;

import org.springframework.data.domain.Page;

import com.esprit.springjwt.entity.Formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginateInfo {

	private int[] count_page;
	private Page<Formation> formation;
	int page;
}
