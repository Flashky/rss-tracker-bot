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

import com.flashk.bots.rsstracker.controllers.mappers.FeedsReplyMarkupMapper;
import com.flashk.bots.rsstracker.controllers.mappers.ItemsReplyMarkupMapper;
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
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class FeedControllerTest {
	
	private final static int FIRST_PAGE = 0;
	private final static int SIZE = 5;
	private final static int TOTAL_ELEMENTS = 15;
	
	private static PodamFactory podamFactory;
	
	@InjectMocks
	@Spy
	private FeedController feedController = new FeedController();
	
	@Mock
	private FeedService feedService;
	
	@Mock
    private LocalizedMessageService messageService;
    
	@Mock
    private ItemsReplyMarkupMapper itemsReplyMarkupMapper;
    
	@Mock
    private FeedsReplyMarkupMapper feedsReplyMarkupMapper;
    
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		Util.disablePodamLogs();
		
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
		Optional<InlineKeyboardMarkup> replyMarkup = Optional.of(new InlineKeyboardMarkup());
		
		// Mocks
		Mockito.doReturn(replyMarkup).when(feedsReplyMarkupMapper).map(any(), any());
		Mockito.doReturn(feedsPage).when(feedService).listFeeds(any(), anyInt(), anyInt());
		
		// Execute method
		SendMessage result = feedController.listFeeds(user, chat);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
		assertNotNull(result); // A message is sent
		
	}
	
	@Test
	void testListFeedsNoFeeds() {
		
		// Prepare POJOs
		User user = new User(23L);
		Chat chat = new Chat();
		
		// Execute method
		SendMessage result = feedController.listFeeds(user, chat);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
		assertNotNull(result); // A message is sent
		
	}
	
	private Page<Feed> manufacturePojoPageFeed() {
		List<Feed> feedList = podamFactory.manufacturePojo(ArrayList.class, Feed.class);
		return new PageImpl<Feed>(feedList, PageRequest.of(FIRST_PAGE, SIZE), TOTAL_ELEMENTS);
	}
	
	@Test
	void testListFeedsCallback() {
		
		// Prepare POJOs
		Page<Feed> feedsPage = manufacturePojoPageFeed();
		Optional<InlineKeyboardMarkup> replyMarkup = Optional.of(new InlineKeyboardMarkup());
		
		// Prepare mocks 
		TelegramRequest request = mockTelegramRequest();
		
		Mockito.doReturn(replyMarkup).when(feedsReplyMarkupMapper).map(any(), any());
		Mockito.doReturn(feedsPage).when(feedService).listFeeds(any(), anyInt(), anyInt());
		
		// Execute method
		feedController.listFeedsCallback(request, FIRST_PAGE, SIZE);
		
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
	}

	@Test
	void testListFeedsCallbackNoFeeds() {
		
		// Prepare mocks 
		TelegramRequest request = mockTelegramRequest();
	
		// Execute method
		feedController.listFeedsCallback(request, FIRST_PAGE, SIZE);
		
		// Assertions
		Mockito.verify(feedService).listFeeds(any(), anyInt(), anyInt()); // feeds are obtained
		
	}
	
	private TelegramRequest mockTelegramRequest() {
		
		// Mocks
		TelegramRequest request = Mockito.mock(TelegramRequest.class);
		TelegramBot bot = Mockito.mock(TelegramBot.class);
		Update update = Mockito.mock(Update.class);
		CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
		Message message = Mockito.mock(Message.class);
		
		
		// Prepare mocks
		Mockito.doReturn(bot).when(request).getTelegramBot();
		Mockito.doReturn(new User(23L)).when(request).getUser();
		Mockito.doReturn(new Chat()).when(request).getChat();
		Mockito.doReturn(update).when(request).getUpdate();
		Mockito.doReturn(callbackQuery).when(update).callbackQuery();
		Mockito.doReturn(message).when(callbackQuery).message();
		Mockito.doReturn(0).when(message).messageId();
		Mockito.doReturn(null).when(bot).execute(any());
		
		return request;
	}
	
	
}
