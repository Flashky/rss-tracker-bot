package com.flashk.bots.rsstracker.services.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaginationTest {

	public static final int FIRST_PAGE = 0;
	public static final int ONE_PAGE = 1;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testPagination() {

		// Prepare POJOs
		int page = 2;
		int size = 5;
		long totalElements = 50;
		long totalPages = 10;
		
		// Execute method
		Pagination result = new Pagination(page, size, totalElements, totalPages);
		
		// Assertions
		assertNotNull(result);
		assertEquals(page, result.getPage());
		assertTrue(result.getPreviousPage().isPresent());
		assertTrue(result.getNextPage().isPresent());
		assertEquals(page-1, result.getPreviousPage().get());
		assertEquals(page+1, result.getNextPage().get());
		assertEquals(size, result.getSize());
		assertEquals(totalElements, result.getTotalElements());
		assertEquals(totalPages, result.getTotalPages());
	}

	@Test
	void testGetPreviousPageIsEmptyOnFirstPage() {
		
		// Prepare POJOs
		int page = FIRST_PAGE;
		int size = 5;
		long totalElements = 50;
		long totalPages = 10;
		
		// Execute method
		Pagination result = new Pagination(page, size, totalElements, totalPages);
		
		// Assertions
		assertNotNull(result);
		assertEquals(page, result.getPage());
		assertFalse(result.getPreviousPage().isPresent());
		
	}

	@Test
	void testGetNextPageIsEmptyOnLastPage() {
		
		// Prepare POJOs
		int page = FIRST_PAGE;
		int size = 5;
		long totalElements = 5;
		long totalPages = ONE_PAGE;
		
		// Execute method
		Pagination result = new Pagination(page, size, totalElements, totalPages);
		
		// Assertions
		assertNotNull(result);
		assertEquals(page, result.getPage());
		assertFalse(result.getNextPage().isPresent());
	}

}
