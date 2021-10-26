package com.flashk.bots.rsstracker.services;

import java.util.List;
import java.util.Optional;

import com.flashk.bots.rsstracker.services.model.Feed;

public interface FeedService {

	List<Feed> listFeeds();
	
	/** 
	 * Saves a given feed. 
	 * Use the returned instance for further operations as the save operation might have changed the feed instance completely.
	 * @param feed must not be null
	 * @return the created feed; will never be null
	 * @throws IllegalArgumentException - in case the given feed or the feed url is null
	 */
	Feed createFeed(Feed feed);
	Optional<Feed> getFeed(String feedId);
	void deleteFeed(String feedId);
	
}
