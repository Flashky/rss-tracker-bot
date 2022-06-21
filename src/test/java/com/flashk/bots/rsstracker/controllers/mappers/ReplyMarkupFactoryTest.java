package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.*;

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

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.test.utils.Util;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ch.qos.logback.classic.Level;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ReplyMarkupFactoryTest {

	private static final int NO_ELEMENTS = 0;
	private static final int TOTAL_ELEMENTS = 15;
	private static final int SIZE = 5;
	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;
	private static final int LAST_PAGE = 2;
	
	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private ReplyMarkupFactory replyMarkupFactory;
	
	@Mock 
	private InlineKeyboardButtonFactory buttonFactory;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Util.setTestingLogLevel(Level.OFF);
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(2);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testCreateFeedPage() {

		// Prepare POJOs
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(SECOND_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = replyMarkupFactory.createFeedPage(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}

	@Test
	void testCreateFeedPageFirstPage() {

		// Prepare POJOs
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(FIRST_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = replyMarkupFactory.createFeedPage(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}
	
	@Test
	void testCreateFeedPageLastPage() {

		// Prepare POJOs
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(LAST_PAGE, SIZE), TOTAL_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = replyMarkupFactory.createFeedPage(user, expected);
		
		// Assertions
		assertTrue(result.isPresent());
		
	}
	
	
	@Test
	void testCreateFeedPageEmptyReplyMarkup() {

		// Prepare POJOs
		List<Feed> feeds = new ArrayList<>();
		Page<Feed> expected = new PageImpl<Feed>(feeds, PageRequest.of(FIRST_PAGE, SIZE), NO_ELEMENTS);
		User user = new User(23L);
		
		// Execute method
		Optional<InlineKeyboardMarkup> result = replyMarkupFactory.createFeedPage(user, expected);
		
		// Assertions
		assertTrue(result.isEmpty());
		
	}

	@Test
	void testCreateItemPage() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createItemPage(user, feed, SECOND_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testCreateItemPageFirstPage() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createItemPage(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testCreateItemPageLastPage() {
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createItemPage(user, feed, LAST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testCreateItemPageUniquePage() {
		
		// Limit size to generate just one page
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(SIZE);
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createItemPage(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testCreateItemPageNoItems() {
		
		// Limit size to generate just one page
		podamFactory.getStrategy().setDefaultNumberOfCollectionElements(NO_ELEMENTS);
		
		// Prepare mocks
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createItemPage(user, feed, FIRST_PAGE, SIZE);
		
		// Assertions
		assertNotNull(result);
		
	}
	
	@Test
	void testCreateDialogDeleteFeed() {
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = replyMarkupFactory.createDialogDeleteFeed(user, feed);
	
		// Assertions
		assertNotNull(result);
	}

}
