package com.flashk.bots.rsstracker.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.flashk.bots.rsstracker.repositories.mappers.ItemEntityMapper;
import com.flashk.bots.rsstracker.repositories.mappers.ItemEntityMapperImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class SyndEntryItemRepositoryTest {

	private final static int NO_ELEMENTS = 0;
	private final static int FIRST_PAGE = 0;
	private final static int SIZE = 5;
	private final static int TOTAL_ELEMENTS = 10;
	
	private static PodamFactory podamFactory;

	@Spy
	@InjectMocks
	private SyndEntryItemRepository itemRepository = new SyndEntryItemRepository();
	
	@Spy
	public ItemEntityMapper itemEntityMapper = new ItemEntityMapperImpl();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    podamFactory = new PodamFactoryImpl();
	    podamFactory.getStrategy().setDefaultNumberOfCollectionElements(TOTAL_ELEMENTS);
	    
	}
	
	@Test
	void testFindByFeedUrl() {

		// Prepare POJOs
		String feedUrl = "https://an-rss-feed.com";
		Pageable pageable =  PageRequest.of(FIRST_PAGE, SIZE);
		SyndFeed feed = manufacturePojoSyndFeed(TOTAL_ELEMENTS);
		
		// Mock
		Mockito.doReturn(feed).when(itemRepository).readRss(any());
		
		// Execute method
		Page<ItemEntity> result = itemRepository.findByFeedUrl(feedUrl, pageable);
		
		// Assertions
		assertNotNull(result);
		assertEquals(SIZE, result.getContent().size());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(SIZE, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize());
		assertEquals(TOTAL_ELEMENTS, result.getTotalElements());
		assertEquals(TOTAL_ELEMENTS / SIZE, result.getTotalPages());
		
	}
	
	@Test
	void testFindByFeedUrlOneIncompletePage() {

		// Prepare POJOs
		String feedUrl = "https://an-rss-feed.com";
		int numberOfElements = SIZE-1; // Create a feed with less entries than the page size
		Pageable pageable =  PageRequest.of(FIRST_PAGE, SIZE);
		SyndFeed feed = manufacturePojoSyndFeed(numberOfElements); 
		
		// Mock
		Mockito.doReturn(feed).when(itemRepository).readRss(any());
		
		// Execute method
		Page<ItemEntity> result = itemRepository.findByFeedUrl(feedUrl, pageable);
		
		// Assertions
		assertNotNull(result);
		assertEquals(numberOfElements, result.getContent().size());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(numberOfElements, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize()); // Page size
		assertEquals(numberOfElements, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		
	}
	
	@Test
	void testFindByFeedUrlEmptyPage() {

		// Prepare POJOs
		String feedUrl = "https://an-rss-feed.com";
		Pageable pageable =  PageRequest.of(FIRST_PAGE, SIZE);
		SyndFeed feed = manufacturePojoSyndFeed(NO_ELEMENTS); // No entries
		
		// Mock
		Mockito.doReturn(feed).when(itemRepository).readRss(any());
		
		// Execute method
		Page<ItemEntity> result = itemRepository.findByFeedUrl(feedUrl, pageable);
		
		// Assertions
		assertNotNull(result);
		assertEquals(NO_ELEMENTS, result.getContent().size());
		assertEquals(FIRST_PAGE, result.getNumber());
		assertEquals(NO_ELEMENTS, result.getNumberOfElements());
		assertEquals(SIZE, result.getSize()); // Page size
		assertEquals(NO_ELEMENTS, result.getTotalElements());
		assertEquals(NO_ELEMENTS, result.getTotalPages());
		
	}
	
	private SyndFeed manufacturePojoSyndFeed(int numberOfItems) {
		
		SyndFeed feed = new SyndFeedImpl();
		feed.setEntries(manufacturePojoSyndEntryList(numberOfItems));
		
		return feed;
	}
	
	private List<SyndEntry> manufacturePojoSyndEntryList(int number) {
	
		List<SyndEntry> entries = new ArrayList<>();
		
		for(int i = 0; i < number; i++) {
			
			SyndEntry syndEntry = new SyndEntryImpl();
			syndEntry.setTitle(podamFactory.manufacturePojo(String.class));
			syndEntry.setLink(podamFactory.manufacturePojo(String.class));
			syndEntry.setPublishedDate(new Date());
			
			entries.add(syndEntry);
		}
		
		return entries;
	}

}
