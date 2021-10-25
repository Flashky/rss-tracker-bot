package com.flashk.bots.rsstracker.services;

import java.util.List;
import java.util.Optional;

import com.flashk.bots.rsstracker.services.model.Feed;

public interface FeedService {

	List<Feed> listFeeds();
	Optional<Feed> getFeed(String feedId);
	void deleteFeed(String feedId);
	
}
