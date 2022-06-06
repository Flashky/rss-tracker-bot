package com.flashk.bots.rsstracker.repositories.feeds.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.flashk.bots.rsstracker.repositories.feeds.entities.ItemEntity;
import com.rometools.rome.feed.synd.SyndEntry;

@Mapper(componentModel = "spring")
public abstract class ItemEntityMapper {

	public abstract List<ItemEntity> map(List<SyndEntry> items);

	
	/**
	 * Maps a <code>Page&lt;SnyDentry&gt;</code> to a <code>PagedResponse&lt;ItemEntity&gt;</code>.
	 * @param syndEntriesPage the synd entries  page to map.
	 * @return a page of item entities.
	 */
	public Page<ItemEntity> map(Page<SyndEntry> entriesPage) {
		
		if(entriesPage == null || entriesPage.isEmpty()) {
			return Page.empty(entriesPage.getPageable());
		}
		
		// Map the data
		List<ItemEntity> itemEntities = map(entriesPage.getContent());
		
		return new PageImpl<>(itemEntities, entriesPage.getPageable(), entriesPage.getTotalElements());
		
	}
}
