package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.test.utils.Util;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ch.qos.logback.classic.Level;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class ItemsReplyMarkupMapperTest {

	private static final int NO_ELEMENTS = 0;
	private static final int TOTAL_ELEMENTS = 15;
	private static final int SIZE = 5;
	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;
	private static final int LAST_PAGE = 2;
	
	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private ItemsReplyMarkupMapper itemsReplyMarkupMapper = new ItemsReplyMarkupMapper();
    
	@Mock 
	private InlineKeyboardButtonFactory buttonFactory;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		Util.setTestingLogLevel(Level.OFF);
        
	    podamFactory = new PodamFactoryImpl();
	    
	}

	@BeforeEach
	void setUp() throws Exception {
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(TOTAL_ELEMENTS);
	}

	@Test
	void testMap() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = itemsReplyMarkupMapper.map(user, feed, SECOND_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testMapFirstPage() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = itemsReplyMarkupMapper.map(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testMapLastPage() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = itemsReplyMarkupMapper.map(user, feed, LAST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testMapUniquePage() {
		
		// Limit size to generate just one page
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(SIZE);
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = itemsReplyMarkupMapper.map(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testMapNoItems() {
		
		// Limit size to generate just one page
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(NO_ELEMENTS);
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = itemsReplyMarkupMapper.map(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	

}
