package com.flashk.bots.rsstracker.services;

import com.flashk.bots.rsstracker.services.model.Feed;

public interface FeedService {

	/**
	 * Creates a feed for the specified user and chat.
	 * @param userId The owner of the feed.
	 * @param chatId Unique chat identifier between user and bot.
	 * @param feedUrl The feed to add.
	 * @return The created feed information.
	 */
	Feed createFeed(Long userId, Long chatId, String feedUrl);
}
