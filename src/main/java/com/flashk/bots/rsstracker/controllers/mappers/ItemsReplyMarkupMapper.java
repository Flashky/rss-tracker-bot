package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.controllers.constants.Constants;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class ItemsReplyMarkupMapper {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
	@Autowired
	private UrlBuilder urlBuilder;
	
	public Optional<InlineKeyboardMarkup> map(Feed feed, PagedResponse<Item> items) {
		
		if(items.getData().isEmpty()) {
			return Optional.empty();
		}

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Feeds
		map(replyMarkup, items.getData());
		
		// Buttons
		map(replyMarkup, items.getPagination(), feed);
		
		return Optional.of(replyMarkup);
	}

	private void map(InlineKeyboardMarkup replyMarkup, List<Item> itemData) {
		
		for(Item item : itemData) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(item.getTitle())
											.url(item.getLink());
			
			replyMarkup.addRow(button);
			
		}
	}
	
	private void map(InlineKeyboardMarkup replyMarkup, Pagination pagination, Feed feed) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(!pagination.isFirst()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.PREVIOUS_PAGE)
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), pagination.getPreviousPage().get(), pagination.getSize()));
			
			paginationButtons.add(button);
			
		}
		
		InlineKeyboardButton backButton = new InlineKeyboardButton("Back to RSS list")
				.callbackData(urlBuilder.getFeedsUri(Constants.FIRST_PAGE, pageSize));
		
		paginationButtons.add(backButton);
		
		
		if(!pagination.isLast()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.NEXT_PAGE)
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), pagination.getNextPage().get(), pagination.getSize()));		
			paginationButtons.add(button);
			
		}
		
		if(!paginationButtons.isEmpty()) {
			InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
			paginationButtons.toArray(paginationButtonsArray);
		
			replyMarkup.addRow(paginationButtonsArray);
		}
		
	}
}
