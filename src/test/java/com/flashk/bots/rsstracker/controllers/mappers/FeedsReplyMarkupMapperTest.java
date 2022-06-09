package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class FeedsReplyMarkupMapperTest {

	private static final int NO_ELEMENTS = 0;
	private static final int TOTAL_ELEMENTS = 15;
	private static final int SIZE = 5;
	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;
	private static final int LAST_PAGE = 2;
	
	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private FeedsReplyMarkupMapper feedReplyMarkupMapper = new FeedsReplyMarkupMapper();
	
	@Mock
	private LocalizedMessageService messageService;
	
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
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(SECOND_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}

	@Test
	void testMapFirstPage() {

		// Prepare POJOs
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(FIRST_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}
	
	@Test
	void testMapLastPage() {

		// Prepare POJOs
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(LAST_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}
	
	
	@Test
	void testMapEmptyFeedsIsMappedToEmptyReplyMarkup() {

		// Prepare POJOs
		List<Feed> feeds = new ArrayList<>();
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(FIRST_PAGE, SIZE), NO_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = feedReplyMarkupMapper.map(user, expected);
		
		// Assertions
		assertTrue(result.isEmpty());
		
	}
	
}
