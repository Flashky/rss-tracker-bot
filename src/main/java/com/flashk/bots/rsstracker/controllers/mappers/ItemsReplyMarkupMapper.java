package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.springframework.web.util.UriComponentsBuilder;

import com.flashk.bots.rsstracker.controllers.constants.Constants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Mapper(componentModel = "spring")
public abstract class ItemsReplyMarkupMapper {

	public Optional<InlineKeyboardMarkup> map(Feed feed, PagedResponse<Item> items) {
		
		if(items.getData().isEmpty()) {
			return Optional.empty();
		}

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Feeds
		map(replyMarkup, items.getData());
		
		// Buttons
		map(replyMarkup, items.getPagination(), feed.getId());
		
		return Optional.of(replyMarkup);
	}

	private void map(InlineKeyboardMarkup replyMarkup, List<Item> itemData) {
		
		for(Item item : itemData) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(item.getTitle())
											.url(item.getLink());
			
			replyMarkup.addRow(button);
			
		}
	}
	
	private void map(InlineKeyboardMarkup replyMarkup, Pagination pagination, String feedId) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(!pagination.isFirst()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.PREVIOUS_PAGE).callbackData(getFeedItemsUri(feedId, pagination.getPreviousPage().get(), pagination.getSize()));
			paginationButtons.add(button);
			
		}
		
		if(!pagination.isLast()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.NEXT_PAGE).callbackData(getFeedItemsUri(feedId, pagination.getNextPage().get(), pagination.getSize()));		
			paginationButtons.add(button);
			
		}
		
		if(!paginationButtons.isEmpty()) {
			InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
			paginationButtons.toArray(paginationButtonsArray);
		
			replyMarkup.addRow(paginationButtonsArray);
		}
		
	}
	
	private String getFeedItemsUri(String feedId, int page, int size) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ITEMS)
									.buildAndExpand(feedId, page, size)
									.toString();
	}
	
	private String getFeedsUri(int page, int size) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEEDS)
									.buildAndExpand(page, size)
									.toString();
	}
}
