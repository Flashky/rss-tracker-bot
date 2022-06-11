package com.flashk.bots.rsstracker.repositories.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.flashk.bots.rsstracker.test.utils.Util;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("unchecked")
class PageBuilderTest {

	// Pages
	private final static int FIRST_PAGE = 0;
	private final static int SECOND_PAGE = 1;
	
	// Counters
	private final static int SIZE = 5;
	private final static int NO_PAGES = 0;
	private final static int SINGLE_PAGE = 1;
	private final static int TWO_PAGES = 2;
	private final static int NO_ELEMENTS = 0;
	private final static int TOTAL_ELEMENTS = 10;
	
	private static PodamFactory podamFactory;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		Util.disablePodamLogs();
		
	    podamFactory = new PodamFactoryImpl();
	}

	@BeforeEach
	void setUp() throws Exception {
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(TOTAL_ELEMENTS);
	}
	
	@Test
	void testBuildOfSizeFirstPage() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.ofSize(SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TOTAL_ELEMENTS / SIZE, result.getTotalPages());
		
	}
	
	@Test
	void testBuildOfPageSizeFirstPage() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(FIRST_PAGE, SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}

	@Test
	void testBuildOfPageableFirstPage1() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		Pageable pageable = PageRequest.of(FIRST_PAGE, SIZE);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(pageable)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}
	
	@Test
	void testBuildOfPageableFirstPage2() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		Pageable pageable = PageRequest.ofSize(SIZE);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(pageable)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}
	
	
	@Test
	void testBuildOfPageSizeSecondPage() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(SECOND_PAGE, SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(SECOND_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}

	@Test
	void testBuildOfPageableSecondPage() {

		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		Pageable pageable = PageRequest.of(SECOND_PAGE, SIZE);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(pageable)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(SECOND_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}
	
	@Test
	void testBuildOfSizeIncompleteFirstPage() {

		// Prepare POJOs
		int numberOfElements = SIZE-1;
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(numberOfElements);
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.ofSize(SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(numberOfElements, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(numberOfElements, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(numberOfElements, result.getTotalElements());
		assertEquals(SINGLE_PAGE, result.getTotalPages());
		
	}

	@Test
	void testBuildOfPageSizeIncompleteFirstPage() {

		// Prepare POJOs
		int numberOfElements = SIZE-1;
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(numberOfElements);
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(FIRST_PAGE, SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(numberOfElements, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(numberOfElements, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(numberOfElements, result.getTotalElements());
		assertEquals(SINGLE_PAGE, result.getTotalPages());
		
	}
	
	@Test
	void testBuildOfPageSizeIncompleteSecondPage() {

		// Prepare POJOs
		int numberOfElements = SIZE-1;
		int totalElements = TOTAL_ELEMENTS-1;
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(totalElements);
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(SECOND_PAGE, SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(numberOfElements, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(SECOND_PAGE, result.getNumber());
		assertEquals(numberOfElements, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(totalElements, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}

	@Test
	void testBuildOfPageableIncompleteSecondPage() {

		// Prepare POJOs
		int numberOfElements = SIZE-1;
		int totalElements = TOTAL_ELEMENTS-1;
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(totalElements);
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		Pageable pageable = PageRequest.of(SECOND_PAGE, SIZE);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(pageable)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(numberOfElements, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(SECOND_PAGE, result.getNumber());
		assertEquals(numberOfElements, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(totalElements, result.getTotalElements());
		assertEquals(TWO_PAGES, result.getTotalPages());
		
	}


	@Test
	void testBuildEmptyContent() {

		// Prepare POJOs
		List<String> elements = new ArrayList<>();
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.ofSize(SIZE)
									.build();
		// Assertions
		assertNotNull(result);
		assertEquals(NO_ELEMENTS, result.getContent().size());
		assertTrue(result.getPageable().isPaged());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(NO_ELEMENTS, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(NO_ELEMENTS, result.getTotalElements());
		assertEquals(NO_PAGES, result.getTotalPages());
		
	}
	
	@Test
	void testBuildNoPageableData() {
		
		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.build();
		
		// Assertions
		assertNotNull(result);
		assertEquals(TOTAL_ELEMENTS, result.getContent().size());
		assertTrue(result.getPageable().isUnpaged());
	}

	@Test
	void testBuildOfPageableUnpaged() {
		
		// Prepare POJOs
		List<String> elements = podamFactory.manufacturePojo(ArrayList.class, String.class);
		
		// Execute method
		Page<String> result = new PageBuilder<>(elements)
									.of(Pageable.unpaged())
									.build();
		
		// Assertions
		assertNotNull(result);
		assertEquals(TOTAL_ELEMENTS, result.getContent().size());
		assertTrue(result.getPageable().isUnpaged());
	}

	
}
