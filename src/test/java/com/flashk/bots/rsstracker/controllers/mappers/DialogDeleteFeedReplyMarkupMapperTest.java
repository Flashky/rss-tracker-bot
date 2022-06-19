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

import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.test.utils.Util;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ch.qos.logback.classic.Level;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class DialogDeleteFeedReplyMarkupMapperTest {

	private static PodamFactory podamFactory;
	
	@Spy
	@InjectMocks
	private DialogDeleteFeedReplyMarkupMapper dialogDeleteFeedReplyMarkupMapper;
	
	@Mock
	private LocalizedMessageService messageService;
	
	@Spy
	private UrlBuilder urlBuilder;
	
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
	void testMap() {

		// Prepare POJOs
		User user = new User(23L);
		Feed feed = podamFactory.manufacturePojo(Feed.class);
		
		// Execute method
		InlineKeyboardMarkup result = dialogDeleteFeedReplyMarkupMapper.map(user, feed);
	
		// Assertions
		assertNotNull(result);
		
	}

}
