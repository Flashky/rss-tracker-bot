package com.flashk.bots.rsstracker.services;

import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;

public interface ItemService {

	/**
	 * Retrieve the items of a feed.
	 * @param feedUrl telegram unique user identifier and owner of the feed.
	 * @param page zero-based page index, must not be negative.
	 * @param size the size of the page to be returned, must be greater than 0.
	 * @return a page of items.
	 */
	PagedResponse<Item> listItems(String feedUrl, int page, int size);
	
}
