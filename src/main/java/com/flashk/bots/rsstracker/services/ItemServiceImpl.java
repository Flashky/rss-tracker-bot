package com.flashk.bots.rsstracker.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.flashk.bots.rsstracker.services.mappers.ItemMapper;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemMapper itemMapper;
	
	@Override
	public PagedResponse<Item> listItems(String feedUrl, int page, int size) {
		
		SyndFeed feed = readRss(feedUrl);
		
		List<SyndEntry> entries = feed.getEntries();
	
		if(entries.isEmpty()) {
			return new PagedResponse<>();
		} else {
			
			// Obtain the elements of the requested page
			long skipNumber = page * size;
			List<SyndEntry> filteredEntries = entries.stream().skip(skipNumber).limit(size).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(page, size);
			Page<SyndEntry> page2 = new PageImpl<>(filteredEntries, pageable, entries.size());
			
			// Prepare the result
			List<Item> items = itemMapper.map(filteredEntries);
			
			
			
			PagedResponse<Item> response = new PagedResponse<>();
			response.setPagination(new Pagination(page, size, entries.size()));
			response.setData(items);
			
			return response;
		}
		
	}
	
	private SyndFeed readRss(String feedUrl) {
		
		try {
			
			URL url = new URL(feedUrl);
			SyndFeedInput input = new SyndFeedInput();	
			return input.build(new XmlReader(url));
		
		} catch (IllegalArgumentException | IOException | FeedException e) {
			throw new InvalidRssException(e);
		}
		
	}

}
