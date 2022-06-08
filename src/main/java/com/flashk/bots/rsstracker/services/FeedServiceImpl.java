package com.flashk.bots.rsstracker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.flashk.bots.rsstracker.repositories.FeedRepository;
import com.flashk.bots.rsstracker.repositories.entities.FeedEntity;
import com.flashk.bots.rsstracker.repositories.entities.TelegramEntity;
import com.flashk.bots.rsstracker.services.mappers.FeedMapper;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.util.FeedReader;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

@Service
@Validated
public class FeedServiceImpl implements FeedService {
	
	@Autowired
	private FeedReader feedReader;
	
	@Autowired
	private FeedRepository feedRepository;
	
	@Autowired
	private FeedMapper feedMapper;
	
	@Override
	public Feed createFeed(Long userId, Long chatId, String feedUrl) {
		
		
		// Obtain feed data
		SyndFeed feed = feedReader.read(feedUrl);
		
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
		
		return feedMapper.map(savedFeedEntity, feed.getEntries());
	}
	
	@Override
	public PagedResponse<Feed> listFeeds(Long userId, int page, int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<FeedEntity> feedEntitiesPage = feedRepository.findByTelegramUserId(userId, pageable);
		
		return feedMapper.map(feedEntitiesPage);
	}
	
	@Override
	public Optional<Feed> getFeed(String feedId) {

		Optional<FeedEntity> feedEntity = feedRepository.findById(feedId);
		
		if(feedEntity.isPresent()) {
			
			// Obtain items and map the merge the result into response object
			List<SyndEntry> syndEntries = feedReader.read(feedEntity.get().getSourceLink()).getEntries();
			Feed feed = feedMapper.map(feedEntity.get(), syndEntries);
			
			return Optional.ofNullable(feed);
		} else {
			return Optional.empty();
		}

	}

}
