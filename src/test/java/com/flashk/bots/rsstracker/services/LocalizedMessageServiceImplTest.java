package com.flashk.bots.rsstracker.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
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

@ExtendWith(MockitoExtension.class)
class LocalizedMessageServiceImplTest {

	private static final String MESSAGE_PROPERTY = "a.b.c";
	private static final String LANGUAGE_CODE = "es";
	private static final String MESSAGE = "Sample message";
	private static final String MESSAGE_WITH_EMOJI_ALIAS = ":orange_book: Sample message";
	private static final String MESSAGE_WITH_EMOJI_UNICODE = "📙 Sample message";

	private static final String ARGUMENT = "ok";
	
	@Spy
	@InjectMocks
	private LocalizedMessageServiceImpl messageService = new LocalizedMessageServiceImpl();
	
	@Mock
	private MessageSource messageSource;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetTextStringString() {
		
		// Prepare mocks
		Mockito.doReturn(MESSAGE).when(messageSource).getMessage(any(), any(), any());
		
		// Execute method
		String response = messageService.getText(MESSAGE_PROPERTY, LANGUAGE_CODE);
		
		// Assertions
		assertNotNull(response);
		assertEquals(MESSAGE, response);
	}

	@Test
	void testGetTextStringStringWithEmoji() {
		
		// Prepare mocks
		Mockito.doReturn(MESSAGE_WITH_EMOJI_ALIAS).when(messageSource).getMessage(any(), any(), any());
		
		// Execute method
		String response = messageService.getText(MESSAGE_PROPERTY, LANGUAGE_CODE);
		
		// Assertions
		assertNotNull(response);
		assertEquals(MESSAGE_WITH_EMOJI_UNICODE, response);
	}
	
	@Test
	void testGetTextStringStringObjectArray() {
		
		// Prepare mocks
		Mockito.doReturn(MESSAGE).when(messageSource).getMessage(any(), any(), any());
		
		// Execute method
		String response = messageService.getText(MESSAGE_PROPERTY, LANGUAGE_CODE, ARGUMENT);
		
		// Assertions
		assertNotNull(response);
		assertEquals(MESSAGE, response);
		
	}
	
	@Test
	void testGetTextStringStringObjectArrayWithEmoji() {
		
		// Prepare mocks
		Mockito.doReturn(MESSAGE_WITH_EMOJI_ALIAS).when(messageSource).getMessage(any(), any(), any());
		
		// Execute method
		String response = messageService.getText(MESSAGE_PROPERTY, LANGUAGE_CODE, ARGUMENT);
		
		// Assertions
		assertNotNull(response);
		assertEquals(MESSAGE_WITH_EMOJI_UNICODE, response);
		
	}


}
