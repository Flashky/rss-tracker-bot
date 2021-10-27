package com.flashk.bots.rsstracker.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.flashk.bots.rsstracker.services.model.Feed;

@Service
public class FeedServiceMock implements FeedService {

	private Map<String, Feed> feeds = new HashMap<>();
	
	public FeedServiceMock() {
		
		Feed feed;
		for(int i = 0; i < 9; i ++) {
			
			feed = new Feed();
			feed.setId(String.valueOf(i));
			feed.setUrl("https://hd-olimpo.club/rss/1631.4ff22951d0562feb3b966d7e74172c44");
			feed.setTitle("HD-Olimpo: Notification - Invasión");
		
			feeds.put(String.valueOf(i), feed);
		}
		
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

	@Override
	public void deleteFeed(String feedId) {
		
		feeds.remove(feedId);
		
	}

	@Override
	public Feed createFeed(Feed feed) {
		
		Assert.notNull(feed, "Feed must not be null!");
		Assert.isTrue(UrlValidator.getInstance().isValid(feed.getUrl()), "Feed URL must be a valid url.");
		
		feed.setId("1234");
		feed.setTitle("Torrentracker: The Walking Dead");
		
		feeds.put(feed.getId(), feed);
		
		return feed;
		
	}

}
