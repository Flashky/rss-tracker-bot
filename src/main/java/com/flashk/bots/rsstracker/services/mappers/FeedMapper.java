package com.flashk.bots.rsstracker.services.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.model.Feed;

@Mapper(componentModel = "spring")
public interface FeedMapper {	
	
	Feed map(FeedEntity feedEntity);
	List<Feed> map(List<FeedEntity> feedEntities);

	
}
