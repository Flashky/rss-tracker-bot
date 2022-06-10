package com.flashk.bots.rsstracker.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

@ExtendWith(MockitoExtension.class)
class WelcomeControllerTest {
	
	@InjectMocks
	@Spy
	private WelcomeController welcomeController = new WelcomeController();
	
	@Mock
    private LocalizedMessageService messageService;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testHelp() {
		
		// Prepare POJOs
		User user = new User(23L);
		Chat chat = new Chat();
		
		// Execute method
		SendMessage message = welcomeController.help(user, chat);
		
		// Assertions
		assertNotNull(message);
		
	}

	@Test
	void testStart() {
			
		// Prepare POJOs
		User user = new User(23L);
		Chat chat = new Chat();
		
		// Execute method
		SendMessage message = welcomeController.help(user, chat);
		
		// Assertions
		assertNotNull(message);
	}

}
