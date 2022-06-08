package com.flashk.bots.rsstracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import com.flashk.bots.rsstracker.repositories.FeedRepository;
import com.flashk.bots.rsstracker.repositories.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.flashk.bots.rsstracker.services.mappers.FeedMapper;
import com.flashk.bots.rsstracker.services.mappers.FeedMapperImpl;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.util.FeedReader;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class FeedServiceImplTest {

	private static PodamFactory podamFactory;
	
	// RSS feed "integration test" generated via https://rss.app
	private static final String FEED_URL = "https://rss.app/feeds/tyjLcmBKeSw9wfxu.xml";
	private static final String INVALID_FEED_URL = "https://google.com";
	
	@InjectMocks
	@Spy
	private FeedServiceImpl feedService = new FeedServiceImpl();

	@Spy
	private FeedReader feedReader;
	
	@Spy
	private FeedMapper feedMapper = new FeedMapperImpl();
	
	@Mock
	private FeedRepository feedRepository;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(2);
	    
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
	
	
	@Test
	void testListFeeds() {
		
		// Prepare POJOs
		Long userId = podamFactory.manufacturePojo(Long.class);
		int page = 0;
		int size = 5;
		Page<FeedEntity> feedEntitiesPage = manufacturePagePojo(page, size);
		
		// Prepare mocks
		Mockito.doReturn(feedEntitiesPage).when(feedRepository).findByTelegramUserId(any(), any());
		
		// Execute method
		PagedResponse<Feed> result = feedService.listFeeds(userId, page, size);
		
		// Assertions
		Mockito.verify(feedRepository).findByTelegramUserId(any(), any()); // Feeds have been searched
		
		assertNotNull(result);
		assertEquals(feedEntitiesPage.getContent().size(), result.getData().size());
	}

	@Test
	void testListFeedsEmpty() {
		
		// Prepare POJOs
		Long userId = podamFactory.manufacturePojo(Long.class);
		int page = 0;
		int size = 5;
		Page<FeedEntity> feedEntitiesPage = manufacturePageEmptyPojo(page, size);
		
		// Prepare mocks
		Mockito.doReturn(feedEntitiesPage).when(feedRepository).findByTelegramUserId(any(), any());
		
		// Execute method
		PagedResponse<Feed> result = feedService.listFeeds(userId, page, size);
		
		// Assertions
		Mockito.verify(feedRepository).findByTelegramUserId(any(), any()); // Feeds have been searched
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void testGetFeed() {
	
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		FeedEntity feedEntity = podamFactory.manufacturePojo(FeedEntity.class);
		feedEntity.setId(feedId);
		feedEntity.setSourceLink(FEED_URL);
		Optional<FeedEntity> expected = Optional.ofNullable(feedEntity);
		
		// Prepare mocks
		Mockito.doReturn(expected).when(feedRepository).findById(any());
		
		// Execute method
		Optional<Feed> result = feedService.getFeed(feedId);
		
		// Assertions
		Mockito.verify(feedRepository).findById(any());
		
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(feedId, result.get().getId());
		
	}
	
	@Test
	void testGetFeedNonExisting() {
	
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		Optional<FeedEntity> expected = Optional.empty();
		
		// Prepare mocks
		Mockito.doReturn(expected).when(feedRepository).findById(any());
		
		// Execute method
		Optional<Feed> result = feedService.getFeed(feedId);
		
		// Assertions
		Mockito.verify(feedRepository).findById(any());
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
		
	}
	
	@Test
	void testGetFeedInvalidUrl() {
	
		// Prepare POJOs
		String feedId = podamFactory.manufacturePojo(String.class);
		
		FeedEntity feedEntity = podamFactory.manufacturePojo(FeedEntity.class);
		feedEntity.setId(feedId);
		feedEntity.setSourceLink(INVALID_FEED_URL);
		Optional<FeedEntity> expected = Optional.ofNullable(feedEntity);
		
		// Prepare mocks
		Mockito.doReturn(expected).when(feedRepository).findById(any());
		
		// Execute method
		assertThrows(InvalidRssException.class, () -> feedService.getFeed(any()));
		
	}
	
	
	private Page<FeedEntity> manufacturePagePojo(int page, int size) {
		
		List<FeedEntity> feeds = podamFactory.manufacturePojo(ArrayList.class, FeedEntity.class);
		Pageable pageable = PageRequest.of(page, size);

		return new PageImpl<>(feeds, pageable, 30);
	}
	
	private Page<FeedEntity> manufacturePageEmptyPojo(int page, int size) {
		
		List<FeedEntity> feeds = new ArrayList<>();
		Pageable pageable = PageRequest.of(page, size);

		return new PageImpl<>(feeds, pageable, 30);
	}


}
