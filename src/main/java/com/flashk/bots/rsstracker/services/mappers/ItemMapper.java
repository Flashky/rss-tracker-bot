package com.flashk.bots.rsstracker.services.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
	
	public abstract List<Item> map(List<ItemEntity> items);
	
	/**
	 * Maps a <code>Page&lt;SnyDentry&gt;</code> to a <code>PagedResponse&lt;Item&gt;</code>.
	 * @param syndEntriesPage the synd entries  page to map.
	 * @return a paged response item.
	 */
	public PagedResponse<Item> map(Page<ItemEntity> itemsPage) {
		
		PagedResponse<Item> pagedResponse = new PagedResponse<>();
		
		if(itemsPage == null || itemsPage.isEmpty()) {
			pagedResponse.setData(new ArrayList<>());
			return pagedResponse;
		}
		
		// Map the data
		pagedResponse.setData(map(itemsPage.getContent()));
		
		// Map the pagination
		Pagination pagination = new Pagination(itemsPage.getNumber(), 
				itemsPage.getSize(), 
				itemsPage.getTotalElements(), 
				itemsPage.getTotalPages());

		
		pagedResponse.setPagination(pagination);
		
		return pagedResponse;

		
	}
}
