package com.flashk.bots.rsstracker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.flashk.bots.rsstracker.repositories.ItemRepository;
import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.flashk.bots.rsstracker.services.mappers.ItemMapper;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Override
	public PagedResponse<Item> listItems(String feedUrl, int page, int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		Page<ItemEntity> itemEntitiesPage = itemRepository.findByFeedUrl(feedUrl, pageable);
		
		return itemMapper.map(itemEntitiesPage);
		
	}
	

}
