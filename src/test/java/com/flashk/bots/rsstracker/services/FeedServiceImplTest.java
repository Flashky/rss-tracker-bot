package com.flashk.bots.rsstracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flashk.bots.rsstracker.repositories.feeds.FeedRepository;
import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.flashk.bots.rsstracker.services.mappers.FeedMapper;
import com.flashk.bots.rsstracker.services.mappers.FeedMapperImpl;
import com.flashk.bots.rsstracker.services.model.Feed;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

	private static PodamFactory podamFactory;
	
	// RSS feed "integration test" generated via https://rss.app
	private static final String FEED_URL = "https://rss.app/feeds/tyjLcmBKeSw9wfxu.xml";
	
	@InjectMocks
	@Spy
	private FeedServiceImpl feedService = new FeedServiceImpl();

	@Spy
	private FeedMapper feedMapper = new FeedMapperImpl();
	
	@Spy
	private FeedRepository feedRepository;
	
	@Captor
	private ArgumentCaptor<FeedEntity> feedEntityCaptor;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(2);
	    
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testCreateFeed() {

		// Prepare POJOs
		Long userId = podamFactory.manufacturePojo(Long.class);
		Long chatId = podamFactory.manufacturePojo(Long.class);
		FeedEntity expected = podamFactory.manufacturePojo(FeedEntity.class);
		
		// Prepare mocks
		Mockito.doReturn(expected).when(feedRepository).save(any());
		
		// Execute method
		Feed result = feedService.createFeed(userId, chatId, FEED_URL);
		
		// Assertions
		
		Mockito.verify(feedRepository).save(any()); // The feed has been saved
		
		assertNotNull(result);
		assertEquals(expected.getTitle(), result.getTitle());
		assertEquals(expected.getDescription(), result.getDescription());
		assertEquals(expected.getSourceLink(), result.getSourceLink());
		
		
	}
	
	@Test
	void testCreateFeedInvalidFeedUrlThrowsInvalidRssException() {

		// Prepare POJOs
		Long userId = podamFactory.manufacturePojo(Long.class);
		Long chatId = podamFactory.manufacturePojo(Long.class);
		String feedUrl = podamFactory.manufacturePojo(String.class);
		
		// Assertions
		Assertions.assertThrows(InvalidRssException.class, () -> feedService.createFeed(userId, chatId, feedUrl));
		
	}


}
