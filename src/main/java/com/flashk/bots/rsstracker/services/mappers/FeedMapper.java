package com.flashk.bots.rsstracker.services.mappers;

import java.util.List;

import org.jsoup.Jsoup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.flashk.bots.rsstracker.repositories.entities.FeedEntity;
import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

@Mapper(componentModel = "spring")
public abstract class FeedMapper {	
	
	// TODO Split mapper in two mappers: FeedEntityMapper and FeedMapper
	
	@Mapping(source = "userId", target = "telegram.userId")
	@Mapping(source = "chatId", target = "telegram.chatId")
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "syndFeed.title", target = "title", qualifiedByName = "cleanHtml")
	@Mapping(target = "isEnabled", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(source = "syndFeed.entries", target = "notifiedItems")
	public abstract FeedEntity map(Long userId, Long chatId, String sourceLink, SyndFeed syndFeed);
	public abstract Feed map(FeedEntity feedEntity, List<SyndEntry> items);
	
	@Mapping(source = "title", target = "title", qualifiedByName = "cleanHtml")
	public abstract ItemEntity map(SyndEntry syndEntry);
	
	@Mapping(source = "title", target = "title", qualifiedByName = "cleanHtml")
	public abstract Item syndEntryToItem(SyndEntry syndEntry);
	
	@Mapping(source = "notifiedItems", target = "items")
	public abstract Feed map(FeedEntity feedEntity);
	
	@Mapping(target = "items", ignore = true)
	public abstract List<Feed> map(List<FeedEntity> feedEntities);
    
	/**
	 * Maps a <code>Page&lt;FeedEntity&gt;</code> to a <code>PagedResponse&lt;Feed&gt;</code>.
	 * @param feedEntitiesPage the feed entities page to map.
	 * @return a paged response feed.
	 */
	public Page<Feed> map(Page<FeedEntity> feedEntitiesPage) {
		
		// No data
		if(feedEntitiesPage == null || feedEntitiesPage.isEmpty()) {
			return Page.empty(feedEntitiesPage.getPageable());
		}
		
		// Map the data
		Page<Feed> feedPage = new PageImpl<>(map(feedEntitiesPage.getContent()), 
												feedEntitiesPage.getPageable(), 
												feedEntitiesPage.getTotalElements());
		
		return feedPage;
	}

	@Named("cleanHtml")
	public String cleanHtml(String html) {
		return (html == null) ? null : Jsoup.parse(html).text();
	}
	
}
