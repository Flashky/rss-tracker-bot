package com.flashk.bots.rsstracker.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flashk.bots.rsstracker.core.services.model.Feed;

@Service
public class FeedServiceMock implements FeedService {

	private Map<String, Feed> feeds = new HashMap<>();
	
	public FeedServiceMock() {
		
		Feed feed = new Feed();
		feed.setId("61746f2c9095ec51f15994e3");
		feed.setUrl("https://hd-olimpo.club/rss/1631.4ff22951d0562feb3b966d7e74172c44");
		feed.setTitle("HD-Olimpo: Notification - Invasión");
		
		feeds.put("61746f2c9095ec51f15994e3", feed);
		
		feed = new Feed();
		feed.setId("6152094e254752701b0544d3");
		feed.setUrl("https://torrentland.li/rss/300.648f76680ba17a9459c8365dba5757df");
		feed.setTitle("Star Wars - Visions");
		
		feeds.put("6152094e254752701b0544d3", feed);
		
	}
	
	@Override
	public List<Feed> listFeeds() {
		
		return new ArrayList<>(feeds.values());
		
	}

	@Override
	public Optional<Feed> getFeed(String feedId) {

		Feed feed = feeds.get(feedId);
		return Optional.ofNullable(feed);
	
	}

}