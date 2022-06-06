package com.flashk.bots.rsstracker.services.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagedResponse<T> {

	private List<T> data = new ArrayList<>();
	private Pagination pagination;
	
	@JsonIgnore
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(data);
	}
}
