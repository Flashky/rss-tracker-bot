package com.flashk.bots.rsstracker.core.services;

import java.util.List;
import java.util.Optional;

import com.flashk.bots.rsstracker.core.services.model.Feed;

public interface FeedService {

	List<Feed> listFeeds();
	Optional<Feed> getFeed(String feedId);

	
}
