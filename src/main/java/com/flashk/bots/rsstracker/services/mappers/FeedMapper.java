package com.flashk.bots.rsstracker.services.mappers;

import org.mapstruct.Mapper;

import com.flashk.bots.rsstracker.repositories.feeds.entities.FeedEntity;
import com.flashk.bots.rsstracker.services.model.Feed;

@Mapper(componentModel = "spring")
public interface FeedMapper {	
	
	Feed map(FeedEntity feedEntity);

	
}
