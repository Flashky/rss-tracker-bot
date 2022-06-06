package com.flashk.bots.rsstracker.services.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;

@Mapper(componentModel = "spring")
public abstract class FeedMapper {	
	
	public abstract Feed map(FeedEntity feedEntity);
	public abstract List<Feed> map(List<FeedEntity> feedEntities);
	
	/**
	 * Maps a <code>Page&lt;FeedEntity&gt;</code> to a <code>PagedResponse&lt;Feed&gt;</code>.
	 * @param feedEntitiesPage the feed entities page to map.
	 * @return a paged response feed.
	 */
	public PagedResponse<Feed> map(Page<FeedEntity> feedEntitiesPage) {
		
		PagedResponse<Feed> pagedResponse = new PagedResponse<>();
		
		if(feedEntitiesPage == null || feedEntitiesPage.isEmpty()) {
			pagedResponse.setData(new ArrayList<>());
			return pagedResponse;
		}
		
		// Map the data
		pagedResponse.setData(map(feedEntitiesPage.getContent()));
		
		// Map the pagination
		Pagination pagination = new Pagination(feedEntitiesPage.getNumber(), 
												feedEntitiesPage.getSize(), 
												feedEntitiesPage.getTotalElements(), 
												feedEntitiesPage.getTotalPages());
		
		pagedResponse.setPagination(pagination);
		
		return pagedResponse;
	}

	
}
