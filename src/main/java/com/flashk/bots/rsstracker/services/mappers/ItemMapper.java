package com.flashk.bots.rsstracker.services.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.rometools.rome.feed.synd.SyndEntry;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
	
	public abstract List<Item> map(List<SyndEntry> syndEntry);
	
	public PagedResponse<Item> map(List<SyndEntry> syndEntries) {
		
		PagedResponse<Item> pagedResponse = new PagedResponse<>();
		
		if(CollectionUtils.isEmpty(syndEntries)) {
			pagedResponse.setData(new ArrayList<>());
			return pagedResponse;
		}
		
		// Map the data
		pagedResponse.setData(map(syndEntries));
		
		// Map the pagination
		pagedResponse.setPagination(new Pagination(page, size, entries.size()));

		
	}
}
