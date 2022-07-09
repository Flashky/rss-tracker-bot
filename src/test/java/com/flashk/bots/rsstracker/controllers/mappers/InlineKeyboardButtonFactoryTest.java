package com.flashk.bots.rsstracker.controllers.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.test.utils.Util;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import ch.qos.logback.classic.Level;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class InlineKeyboardButtonFactoryTest {

	private static final int TOTAL_ELEMENTS = 15;
	private static final int SIZE = 5;
	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;
	private static final int LAST_PAGE = 2;
	
	private static final String CALLBACK_FIRST_FEED_PAGE = "/feeds?page=0&size=0";
	private static final Object CALLBACK_LAST_FEED_PAGE = "/feeds?page=2&size=0";
	private static final String CALLBACK_DIALOG_DELETE_PATTERN = "/feeds/%s?action=dialog_delete";
	private static final String CALLBACK_DELETE_PATTERN = "/feeds/%s?action=delete";
	private static final String CALLBACK_ITEM_PAGE_PATTERN = "/feeds/%s/items?page=%s&size=0";
	
	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private InlineKeyboardButtonFactory buttonFactory = new InlineKeyboardButtonFactory();
	
    @Mock
    private LocalizedMessageService messageService;
    
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Util.setTestingLogLevel(Level.OFF);
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(TOTAL_ELEMENTS);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testCreateFirstFeedPageButtonFeed() {
		
		// Prepare POJOs
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createFirstFeedPageButton(feed);
		
		// Assertions
		assertNotNull(result);
		assertEquals(feed.getTitle(), result.text());
		assertEquals(CALLBACK_FIRST_FEED_PAGE, result.callbackData());
		
	}

	@Test
	void testCreateFirstFeedPageButtonStringUser() {
		
		// Prepare POJOs
		String label = "label";
		User user = new User(23L);
		String buttonText = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createFirstFeedPageButton(label, user);
		
		// Assertions
		assertNotNull(result);
		assertEquals(buttonText, result.text());
		assertEquals(CALLBACK_FIRST_FEED_PAGE, result.callbackData());
	}

	@Test
	void testCreatePreviousFeedPageButton() {
	
		// Prepare POJOs
		User user = new User(23L);
		Page<Feed> feedPage = manufactureFeedPagePojo(SECOND_PAGE, SIZE);
		String buttonText = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createPreviousFeedPageButton(user, feedPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(buttonText, result.get().text());
		assertEquals(CALLBACK_FIRST_FEED_PAGE, result.get().callbackData());
		
	}
	
	@Test
	void testCreatePreviousFeedPageButtonNoPreviousPage() {
		
		// Prepare POJOs
		User user = new User(23L);
		Page<Feed> feedPage = manufactureFeedPagePojo(FIRST_PAGE, SIZE); // First page has no previous page
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createPreviousFeedPageButton(user, feedPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isEmpty());
		
	}
	
	@Test
	void testCreateNextFeedPageButton() {
		
		// Prepare POJOs
		User user = new User(23L);
		Page<Feed> feedPage = manufactureFeedPagePojo(SECOND_PAGE, SIZE);
		String buttonText = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createNextFeedPageButton(user, feedPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(buttonText, result.get().text());
		assertEquals(CALLBACK_LAST_FEED_PAGE, result.get().callbackData());
		
	}

	@Test
	void testCreateNextFeedPageButtonNoNextPage() {
		
		// Prepare POJOs
		User user = new User(23L);
		Page<Feed> feedPage = manufactureFeedPagePojo(LAST_PAGE, SIZE); // Last page has no next page
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createNextFeedPageButton(user, feedPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isEmpty());

	}

	
	@Test
	void testCreateOpenDialogDeleteFeedButton() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		String buttonText = podamFactory.manufacturePojo(String.class);
		String expectedCallback = String.format(CALLBACK_DIALOG_DELETE_PATTERN, feed.getId());
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createOpenDialogDeleteFeedButton(user, feed);
		
		// Assertions
		assertNotNull(result);
		assertEquals(buttonText, result.text());
		assertEquals(expectedCallback, result.callbackData());
	}

	@Test
	void testCreateItemUrlButton() {
		
		// Prepare POJOs
		Item item = podamFactory.manufacturePojo(Item.class);
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createItemUrlButton(item);
		
		// Assertions
		assertNotNull(result);
		assertEquals(item.getTitle(), result.text());
		assertEquals(item.getLink(), result.url());
		
	}

	@Test
	void testCreateFirstItemPageButtonFeed() {
		
		// Prepare POJOs
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		String expectedCallback = String.format(CALLBACK_ITEM_PAGE_PATTERN, feed.getId(), FIRST_PAGE);

		// Execute method
		InlineKeyboardButton result = buttonFactory.createFirstItemPageButton(feed);
		
		// Assertions
		assertNotNull(result);
		assertEquals(feed.getTitle(), result.text());
		assertEquals(expectedCallback, result.callbackData());
	}

	@Test
	void testCreateFirstItemPageButtonStringUserFeed() {
		
		// Prepare POJOs
		String label = podamFactory.manufacturePojo(String.class);
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		String expectedCallback = String.format(CALLBACK_ITEM_PAGE_PATTERN, feed.getId(), FIRST_PAGE);
		String buttonText = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createFirstItemPageButton(label, user, feed);
		
		// Assertions
		assertNotNull(result);
		assertEquals(buttonText, result.text());
		assertEquals(expectedCallback, result.callbackData());
	}

	@Test
	void testCreatePreviousItemPageButton() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		Page<Item> itemPage = manufactureItemPagePojo(LAST_PAGE, SIZE);
		String buttonText = podamFactory.manufacturePojo(String.class);
		String expectedCallback = String.format(CALLBACK_ITEM_PAGE_PATTERN, feed.getId(), SECOND_PAGE);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createPreviousItemPageButton(user, feed, itemPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(buttonText, result.get().text());
		assertEquals(expectedCallback, result.get().callbackData());
	}

	@Test
	void testCreatePreviousItemPageButtonNoPreviousPages() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		Page<Item> itemPage = manufactureItemPagePojo(FIRST_PAGE, SIZE);

		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createPreviousItemPageButton(user, feed, itemPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void testCreateNextItemPageButton() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		Page<Item> itemPage = manufactureItemPagePojo(FIRST_PAGE, SIZE);
		String buttonText = podamFactory.manufacturePojo(String.class);
		String expectedCallback = String.format(CALLBACK_ITEM_PAGE_PATTERN, feed.getId(), SECOND_PAGE);
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createNextItemPageButton(user, feed, itemPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(buttonText, result.get().text());
		assertEquals(expectedCallback, result.get().callbackData());
		
	}

	@Test
	void testCreateNextItemPageButtonNoNextPage() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		Page<Item> itemPage = manufactureItemPagePojo(LAST_PAGE, SIZE);
		
		// Execute method
		Optional<InlineKeyboardButton> result = buttonFactory.createNextItemPageButton(user, feed, itemPage);
		
		// Assertions
		assertNotNull(result);
		assertTrue(result.isEmpty());
		
	}
	
	@Test
	void testCreateDeleteFeedButton() {
		
		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		String buttonText = podamFactory.manufacturePojo(String.class);
		String expectedCallback = String.format(CALLBACK_DELETE_PATTERN, feed.getId());
		
		// Mocks
		Mockito.doReturn(buttonText).when(messageService).getText(any(), any());
		
		// Execute method
		InlineKeyboardButton result = buttonFactory.createDeleteFeedButton(user, feed);
		
		// Assertions
		assertNotNull(result);
		assertEquals(buttonText, result.text());
		assertEquals(expectedCallback, result.callbackData());
	}

	private Page<Feed> manufactureFeedPagePojo(int page, int size) {
		
		List<Feed> feeds = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Pageable pageable = PageRequest.of(page, size);

		return new PageImpl<>(feeds, pageable, TOTAL_ELEMENTS);
	}
	
	private Page<Item> manufactureItemPagePojo(int page, int size) {
		
		List<Item> items = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		Pageable pageable = PageRequest.of(page, size);

		return new PageImpl<>(items, pageable, TOTAL_ELEMENTS);
	}
}
