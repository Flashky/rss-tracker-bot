package com.flashk.bots.rsstracker.controllers.mappers;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.flashk.bots.rsstracker.controllers.constants.PathConstants;

@Component
public class UrlBuilder {

	public String getFeedItemsUri(String feedId, int page, int size) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ITEMS)
									.buildAndExpand(feedId, page, size)
									.toString();
	}
	
	public String getFeedsUri(int page, int size) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEEDS)
									.buildAndExpand(page, size)
									.toString();
	}
}
