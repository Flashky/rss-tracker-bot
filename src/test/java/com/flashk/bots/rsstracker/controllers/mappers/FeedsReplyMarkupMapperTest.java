package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class FeedsReplyMarkupMapperTest {

	private static final int TOTAL_ELEMENTS = 15;
	private static final int SIZE = 5;
	private static final int SECOND_PAGE = 1;
	
	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private FeedsReplyMarkupMapper feedReplyMarkupMapper = new FeedsReplyMarkupMapper();
	
	@Spy
	private UrlBuilder urlBuilder;
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(TOTAL_ELEMENTS);
	    
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMap() {

		// Prepare POJOs
		PagedResponse<Feed> expected = podamFactory.manufacturePojo(PagedResponse.class, Feed.class);
		Pagination pagination = new Pagination(SECOND_PAGE, SIZE, TOTAL_ELEMENTS);
		expected.setPagination(pagination);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}

	@Test
	void testMapEmptyFeedsIsMappedToEmptyReplyMarkup() {

		// Prepare POJOs
		PagedResponse<Feed> expected = podamFactory.manufacturePojo(PagedResponse.class, Feed.class);
		expected.setData(new ArrayList<>());
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(expected);
		
		// Assertions
		assertTrue(result.isEmpty());
		
	}
	
}
