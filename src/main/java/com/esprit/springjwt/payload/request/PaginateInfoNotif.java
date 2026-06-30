package com.esprit.springjwt.payload.request;

import org.springframework.data.domain.Page;

import com.esprit.springjwt.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginateInfoNotif {

	private int[] count_page;
	private Page<Notification> notifs;
	int page;
}
