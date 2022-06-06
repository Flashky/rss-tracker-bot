package com.flashk.bots.rsstracker.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeedReaderServiceImplTest {

	private ItemService feedReaderService = new ItemServiceImpl();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testListFeedItems() {
		
		// Prepare POJOs
		String feedUrl = "https://hd-olimpo.club/rss/1825.4ff22951d0562feb3b966d7e74172c44";
		
		// Execute method
		feedReaderService.listItems(feedUrl, 0, 1);
	}

}
