package com.flashk.bots.rsstracker.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.flashk.bots.rsstracker.services.FeedService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {
	
	private static PodamFactory podamFactory;
	
	@InjectMocks
	@Spy
	private FeedController feedController = new FeedController();
	
	@Mock
	private MessageSource messageSource;
	
    @Mock
    private FeedService feedService;
    
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(2);
	    
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testAddFeed() {
		
		// Prepare POJOs
		User user = podamFactory.manufacturePojo(User.class);
		Chat chat = podamFactory.manufacturePojo(Chat.class);
		
		// Prepare mocks
		Mockito.when(messageSource.getMessage(any(), any(), any())).thenReturn("TEST_MESSAGE");
		 
		// Execute method
		SendMessage result = feedController.addFeed(user, chat);
		
		// Assertions
		assertNotNull(result); // A message is sent
		
	}

}
