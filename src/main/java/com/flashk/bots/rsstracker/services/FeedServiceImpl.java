package com.flashk.bots.rsstracker.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.flashk.bots.rsstracker.repositories.feeds.FeedRepository;
import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.repositories.feeds.entities.TelegramEntity;
import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.flashk.bots.rsstracker.services.mappers.FeedMapper;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
@Validated
public class FeedServiceImpl implements FeedService {
	
	@Autowired
	private FeedRepository feedRepository;
	
	@Autowired
	private FeedMapper feedMapper;
	
	@Override
	@Validated
	public Feed createFeed(Long userId, Long chatId, String feedUrl) {
		
		// Obtain feed data
		SyndFeed feed = readRss(feedUrl);
		
		// Prepare the entity to save
		TelegramEntity telegramEntity = TelegramEntity.builder()
				.userId(userId)
				.chatId(chatId)
				.build();
		
		FeedEntity feedEntity = FeedEntity.builder()
				.title(feed.getTitle())
				.description(feed.getDescription())
				.link(feed.getLink())
				.telegram(telegramEntity)
				.sourceLink(feedUrl)
				.isEnabled(true)
				.build();

		// Save entity and return it
		FeedEntity savedFeedEntity = feedRepository.save(feedEntity);
		
		return feedMapper.map(savedFeedEntity);
	}
	
	@Override
	public PagedResponse<Feed> listFeeds(Long userId, int page, int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<FeedEntity> feedEntitiesPage = feedRepository.findByTelegramUserId(userId, pageable);

		
		// TODO refactor into a mapper
		// Map data and pagination objects
		List<Feed> feeds = feedMapper.map(feedEntitiesPage.getContent());

		Pagination pagination = new Pagination(feedEntitiesPage.getNumber(), 
												feedEntitiesPage.getSize(), 
												feedEntitiesPage.getTotalElements(), 
												feedEntitiesPage.getTotalPages());
		
		PagedResponse<Feed> pagedResponse = new PagedResponse<>();
		pagedResponse.setData(feeds);
		pagedResponse.setPagination(pagination);
		
		return pagedResponse;
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
