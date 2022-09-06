package com.flashk.bots.rsstracker.services.mappers;

import java.util.List;

import org.jsoup.Jsoup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.flashk.bots.rsstracker.repositories.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.rometools.rome.feed.synd.SyndEntry;

@Mapper(componentModel = "spring")
public abstract class FeedMapper {	
	
	public abstract Feed map(FeedEntity feedEntity, List<SyndEntry> items);
	
	@Mapping(source = "title", target = "title", qualifiedByName = "cleanHtml")
	public abstract Item map(SyndEntry syndEntry);
	
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
