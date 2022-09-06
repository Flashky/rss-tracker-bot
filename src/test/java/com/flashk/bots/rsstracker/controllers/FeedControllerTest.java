package com.flashk.bots.rsstracker.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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

import com.flashk.bots.rsstracker.controllers.mappers.MessageFactory;
import com.flashk.bots.rsstracker.controllers.mappers.ReplyMarkupFactory;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.test.utils.Util;
import com.github.kshashov.telegram.api.TelegramRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

import ch.qos.logback.classic.Level;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class FeedControllerTest {
	
	private final static int FIRST_PAGE = 0;
	private final static int SIZE = 5;
	private final static int TOTAL_ELEMENTS = 15;
	private final static String REPLY_TEXT = "reply-text";
	
	private static PodamFactory podamFactory;
	
	@InjectMocks
	@Spy
	private FeedController feedController = new FeedController();
	
	@Mock
	private FeedService feedService;
	
	@Mock
    private LocalizedMessageService messageService;
    
	@Mock
	private MessageFactory messageFactory;
	
	@Mock
    private ReplyMarkupFactory replyMarkupFactory;
	
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
	void testListFeeds() {
		
		// Prepare POJOs
		User user = new User(23L);
		Chat chat = new Chat();
		Page<Feed> feedsPage = manufacturePojoPageFeed();
		SendMessage expected = podamFactory.manufacturePojo(SendMessage.class);
		
		// Mocks
		Mockito.doReturn(feedsPage).when(feedService).listFeeds(any(), anyInt(), anyInt());
		Mockito.doReturn(expected).when(messageFactory).createFeedListSendMessage(any(), any(), any());
		
		// Execute method
		SendMessage result = feedController.listFeeds(user, chat);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		Mockito.verify(messageFactory).createFeedListSendMessage(any(), any(), any());
		
		assertNotNull(result); // A message is sent
		
	}
	
	@Test
	void testListFeedsNoFeeds() {
		
		// Prepare POJOs
		User user = new User(23L);
		Chat chat = new Chat();
		SendMessage expected = podamFactory.manufacturePojo(SendMessage.class);
		
		// Mocks
		Mockito.doReturn(expected).when(messageFactory).createFeedListSendMessage(any(), any(), any());
		
		// Execute method
		SendMessage result = feedController.listFeeds(user, chat);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		Mockito.verify(messageFactory).createFeedListSendMessage(any(), any(), any());
		
		assertNotNull(result); // A message is sent
		
	}

	
	@Test
	void testListFeedsCallback() {
		
		// Prepare POJOs
		Page<Feed> feedsPage = manufacturePojoPageFeed();
		
		// Prepare mocks 
		TelegramRequest request = mockTelegramRequestCallbackQuery(true);
		
		Mockito.doReturn(feedsPage).when(feedService).listFeeds(any(), anyInt(), anyInt());
		
		// Execute method
		feedController.listFeedsCallback(request, FIRST_PAGE, SIZE);
		
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
	}

	@Test
	void testListFeedsCallbackNoFeeds() {
		
		// Prepare mocks 
		TelegramRequest request = mockTelegramRequestCallbackQuery(true);
	
		// Execute method
		feedController.listFeedsCallback(request, FIRST_PAGE, SIZE);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
	}
	
	@Test
	void testListFeedItems() {
		
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		Optional<Feed> feed = Optional.of(podamFactory.manufacturePojo(Feed.class));

		// Prepare mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		
		Mockito.doReturn(feed).when(feedService).getFeed(any());
		
		// Execute method
		feedController.listFeedItems(request, feedId, FIRST_PAGE, SIZE);
		
		
		// Assertions
		Mockito.verify(feedService).getFeed(any()); // feed is obtained
		
	}
	
	@Test
	void testListFeedItemsNoItems() {
		
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		
		// Prepare mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		
		// Execute method
		feedController.listFeedItems(request, feedId, FIRST_PAGE, SIZE);
		
		
		// Assertions
		Mockito.verify(feedService).getFeed(any()); // feed is obtained
		
	}
	
	@Test
	void testAddFeed() {
		
		// Prepare Pojo
		SendMessage expected = podamFactory.manufacturePojo(SendMessage.class);
		
		// Prepare mocks
		Mockito.doReturn(expected).when(messageFactory).createAddFeedSendMessage(any(), any());
		
		// Execute method
		SendMessage result = feedController.addFeed(new User(23L), new Chat());
		
		// Assertions
		assertNotNull(result);
		Mockito.verify(messageFactory).createAddFeedSendMessage(any(), any());
		
	}
	
	@Test
	void testReplyNoReplyMessage() {
		
		// Mocks
		TelegramRequest request = Mockito.mock(TelegramRequest.class);
		Mockito.doReturn(new Message()).when(request).getMessage();

		// Execute method
		feedController.reply(request);
		
	}
	
	@Test
	void testReplyCreateFeed() {
		
		// Prepare POJOs
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Mocks
		TelegramRequest request = mockTelegramRequestReplyToMessage();
		
		Mockito.doReturn(REPLY_TEXT).when(messageService).getText(any(), any());
		Mockito.doReturn(feed).when(feedService).createFeed(any(), any(), any());
		
		// Execute method
		feedController.reply(request);
		
		// Assertions
		Mockito.verify(feedService).createFeed(any(), any(), any());
	}
	
	@Test 
	void testShowDeleteFeedDialog() {
		
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		Optional<Feed> feed = Optional.of(podamFactory.manufacturePojo(Feed.class));
		
		// Prepare mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		Mockito.doReturn(feed).when(feedService).getFeed(any());
		
		// Execute method
		feedController.showDeleteFeedDialog(request, feedId);
		
		// Assertions 
		Mockito.verify(feedService).getFeed(any());
	}
	
	@Test 
	void testShowDeleteFeedDialogNoFeed() {
		
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		
		// Execute method
		feedController.showDeleteFeedDialog(request, feedId);
		
 				
		// Assertions
		Mockito.verify(feedService).getFeed(any());

	}
	
	@Test
	void testDeleteFeed() {
					
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		
		// Mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		
		// Execute method
		feedController.deleteFeed(request, feedId);
		
		// Assertions
		Mockito.verify(feedService).deleteFeed(any());
				
	}
	
	@Test
	void testDeleteFeedNoFeed() {
					
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		Optional<Feed> feed = Optional.of(podamFactory.manufacturePojo(Feed.class));
		
		// Mocks
		TelegramRequest request = mockTelegramRequestCallbackQuery(false);
		Mockito.doReturn(feed).when(feedService).deleteFeed(any());
		
		// Execute method
		feedController.deleteFeed(request, feedId);
		
		// Assertions
		Mockito.verify(feedService).deleteFeed(any());
				
	}
	
	private Page<Feed> manufacturePojoPageFeed() {
		List<Feed> feedList = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		return new PageImpl<Feed>(feedList, PageRequest.of(FIRST_PAGE, SIZE), TOTAL_ELEMENTS);
	}
	
	private TelegramRequest mockTelegramRequestCallbackQuery(boolean mockUser) {
		
		// Mocks
		TelegramRequest request = Mockito.mock(TelegramRequest.class);
		TelegramBot bot = Mockito.mock(TelegramBot.class);
		Update update = Mockito.mock(Update.class);
		CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
		//Message message = Mockito.mock(Message.class);
		
		// Prepare mocks
		Mockito.doReturn(bot).when(request).getTelegramBot();
		if(mockUser) {
			Mockito.doReturn(new User(23L)).when(request).getUser();
		}
		//Mockito.doReturn(new Chat()).when(request).getChat();
		Mockito.doReturn(update).when(request).getUpdate();
		
		// CallbackQuery related mocks
		Mockito.doReturn(callbackQuery).when(update).callbackQuery();
		//Mockito.doReturn(message).when(callbackQuery).message();
		//Mockito.doReturn(0).when(message).messageId();
		
		// Bot execution
		Mockito.doReturn(null).when(bot).execute(any());
		
		return request;
	}
	
	private TelegramRequest mockTelegramRequestReplyToMessage() {
		
		// Mocks
		TelegramRequest request = Mockito.mock(TelegramRequest.class);
		TelegramBot bot = Mockito.mock(TelegramBot.class);
		Message message = Mockito.mock(Message.class);
		Message replyToMessage = Mockito.mock(Message.class);
		
		// Prepare mocks
		Mockito.doReturn(bot).when(request).getTelegramBot();
		Mockito.doReturn(new User(23L)).when(request).getUser();
		Mockito.doReturn(new Chat()).when(request).getChat();
		Mockito.doReturn(message).when(request).getMessage();
		
		// Message replyToMessage() related mocks
		Mockito.doReturn(replyToMessage).when(message).replyToMessage();
		Mockito.doReturn(REPLY_TEXT).when(replyToMessage).text();
		
		// Bot execution
		Mockito.doReturn(null).when(bot).execute(any());
		
		return request;
	}
	

	
	
}
