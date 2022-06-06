package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;

import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Mapper(componentModel = "spring")
public class FeedsReplyMarkupMapper {

	public Optional<InlineKeyboardMarkup> map(PagedResponse<Feed> feeds) {
		
		if(feeds.getData().isEmpty()) {
			return Optional.empty();
		}

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Feeds
		map(replyMarkup, feeds.getData());
		
		// Buttons
		map(replyMarkup, feeds.getPagination());
		
		return Optional.of(replyMarkup);
	}

	/**
	 * Maps the feed list to an InlineKeyboardMarkup.
	 * @param replyMarkup an InlineKeyboardMarkup to add the feed data.
	 * @param feedData the feed data to add
	 */
	private void map(InlineKeyboardMarkup replyMarkup, List<Feed> feedData) {
		
		for(Feed feed : feedData) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(feed.getTitle())
											.callbackData("/feeds/"+feed.getId()+"?action=show");
			
			replyMarkup.addRow(button);
			
		}
	}
	
	/**
	 * Maps the pagination to an InlineKeyboardMarkup.
	 * @param replyMarkup an InlineKeyboardMarkup to add pagination data.
	 * @param pagination the pagination to add.
	 */
	private void map(InlineKeyboardMarkup replyMarkup, Pagination pagination) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(!pagination.isFirst()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton("<<")
													.callbackData("/feeds?page="+pagination.getPreviousPage().get()+"&size="+pagination.getSize());
			
			paginationButtons.add(button);
		}
		
		if(!pagination.isLast()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(">>")
					.callbackData("/feeds?page="+pagination.getNextPage().get()+"&size="+pagination.getSize());
			
			paginationButtons.add(button);
		}
		
		InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
		paginationButtons.toArray(paginationButtonsArray);
		
		replyMarkup.addRow(paginationButtonsArray);
	}
}
