package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

class FeedsReplyMarkupMapperTest {

	private static PodamFactory podamFactory;
	
	private FeedsReplyMarkupMapper feedReplyMarkupMapper = new FeedsReplyMarkupMapperImpl();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(2);
	    
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testMap() {

		// Prepare POJOs
		PagedResponse<Feed> expected = podamFactory.manufacturePojo(PagedResponse.class, Feed.class);
		
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
