package com.flashk.bots.rsstracker.repositories;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.flashk.bots.rsstracker.repositories.mappers.ItemEntityMapper;
import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * ItemRepository based on the rome SyndFeed implementation.
 * 
 */
@Repository
public class SyndEntryItemRepository implements ItemRepository {

	@Autowired
	public ItemEntityMapper itemEntityMapper;
	
	@Override
	public Page<ItemEntity> findByFeedUrl(String feedUrl, Pageable pageable) {
		
		// Obtain the feed
		SyndFeed feed = readRss(feedUrl);
		
		// Page the results
		Page<SyndEntry> page = getPage(pageable, feed.getEntries());
		
		return itemEntityMapper.map(page);
	}

	/**
	 * Reads a RSS feed.
	 * @param feedUrl the feed url.
	 * @return a SyndFeed object that represents the content of the feed.
	 */
	private SyndFeed readRss(String feedUrl) {
		
		try {
			
			URL url = new URL(feedUrl);
			SyndFeedInput input = new SyndFeedInput();	
			return input.build(new XmlReader(url));
		
		} catch (IllegalArgumentException | IOException | FeedException e) {
			throw new InvalidRssException(e);
		}
		
	}
	
	/**
	 * Builds a page from the input pageable data and list of entries.
	 * @param pageable the pagination information.
	 * @param entries the list of entries.
	 * @return a page of entries.
	 */
	private Page<SyndEntry> getPage(Pageable pageable, List<SyndEntry> entries) {
		
		long skipNumber = pageable.getPageNumber() * pageable.getPageSize();
		List<SyndEntry> filteredEntries = entries.stream().skip(skipNumber).limit(pageable.getPageSize()).collect(Collectors.toList()); 
		
		return new PageImpl<>(filteredEntries, pageable, entries.size());
	}
	
	
	



}
