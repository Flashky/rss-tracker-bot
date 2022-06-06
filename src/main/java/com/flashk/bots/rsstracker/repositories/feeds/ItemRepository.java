package com.flashk.bots.rsstracker.repositories.feeds;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.flashk.bots.rsstracker.repositories.feeds.entities.ItemEntity;

public interface ItemRepository {

	/**
	 * Finds all items from the specified feed.
	 * @param userId unique telegram user identifier.
	 * @param pageable the pageable to request a paged result, can be Pageable.unpaged(), must not be null.
	 * @return
	 */
	Page<ItemEntity> findByFeedUrl(String feedUrl, Pageable pageable);
	
}
