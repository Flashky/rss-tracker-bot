package com.flashk.bots.rsstracker.services;

import java.util.Optional;

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;

public interface FeedService {

	/**
	 * Creates a feed for the specified user and chat.
	 * @param userId telegram unique user identifier and owner of the feed.
	 * @param chatId telegram unique chat identifier between user and bot.
	 * @param feedUrl feed to be created for the user.
	 * @return the created feed.
	 */
	Feed createFeed(Long userId, Long chatId, String feedUrl);
	
	/**
	 * Retrieve a paged list of feeds.
	 * @param userId telegram unique user identifier and owner of the feed.
	 * @param page zero-based page index, must not be negative.
	 * @param size the size of the page to be returned, must be greater than 0.
	 * @return a page of feeds.
	 */
	PagedResponse<Feed> listFeeds(Long userId, int page, int size);
	
	/**
	 * Retrieve a single feed.
	 * @param feedId unique feed identifier.
	 * @return a feed.
	 */
	Optional<Feed> getFeed(String feedId);
}
