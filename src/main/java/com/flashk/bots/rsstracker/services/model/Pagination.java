package com.flashk.bots.rsstracker.services.model;

import java.util.Optional;

import lombok.Getter;

@Getter
public class Pagination {
	
	public final static int FIRST_PAGE = 0;
	private static final int PAGE_INCREMENT = 1;
	
	private int page;
	private Optional<Integer> previousPage;
	private Optional<Integer> nextPage;
	private int size;
	private long totalElements;
	private long totalPages;
	
	public Pagination(int page, int size, long totalElements, long totalPages) {
		
		if(page < FIRST_PAGE) {
			throw new IllegalArgumentException("Page cannot be less than zero.");
		}
		
		this.page = page;
		this.previousPage = (page > FIRST_PAGE) ? Optional.of(page - PAGE_INCREMENT) : Optional.empty();
		this.nextPage = (page < totalPages-1) ? Optional.of(page + PAGE_INCREMENT) : Optional.empty();
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;	
		
	}
	
	public boolean isLastPage() {
		return !nextPage.isPresent();
	}
	
}
