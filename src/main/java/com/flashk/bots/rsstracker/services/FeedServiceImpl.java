package com.flashk.bots.rsstracker.services;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.flashk.bots.rsstracker.repositories.feeds.FeedRepository;
import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.repositories.feeds.entities.TelegramEntity;
import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.flashk.bots.rsstracker.services.mappers.FeedMapper;
import com.flashk.bots.rsstracker.services.model.Feed;
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
