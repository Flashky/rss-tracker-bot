package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.controllers.constants.Constants;
import com.flashk.bots.rsstracker.repositories.utils.PageBuilder;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class ItemsReplyMarkupMapper {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
	@Autowired
	private UrlBuilder urlBuilder;
	
	public Optional<InlineKeyboardMarkup> map(Feed feed, int itemPage, int size) {
		
	
		if(feed.getItems().isEmpty()) {
			return Optional.empty();
		}
		
		// Paginate result
		Page<Item> itemsPage = new PageBuilder<>(feed.getItems()).of(itemPage, size).build();

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Items
		map(replyMarkup, itemsPage);
		
		// Buttons
		map(replyMarkup, feed, itemsPage);
		
		return Optional.of(replyMarkup);
	}

	private void map(InlineKeyboardMarkup replyMarkup, Page<Item> itemData) {
		
		for(Item item : itemData.getContent()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(item.getTitle())
											.url(item.getLink());
			
			replyMarkup.addRow(button);
			
		}
	}
	
	private void map(InlineKeyboardMarkup replyMarkup, Feed feed, Page<Item> items) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(items.hasPrevious()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.PREVIOUS_PAGE)
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), items.previousPageable().getPageNumber(), items.getSize()));
			
			paginationButtons.add(button);
			
		}
		
		InlineKeyboardButton backButton = new InlineKeyboardButton("Back to RSS list")
				.callbackData(urlBuilder.getFeedsUri(Constants.FIRST_PAGE, pageSize));
		
		paginationButtons.add(backButton);
		
		
		if(items.hasNext()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(Constants.NEXT_PAGE)
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), items.nextPageable().getPageNumber(), items.getSize()));		
			
			paginationButtons.add(button);
			
		}
		
		if(!paginationButtons.isEmpty()) {
			InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
			paginationButtons.toArray(paginationButtonsArray);
		
			replyMarkup.addRow(paginationButtonsArray);
		}
		
	}
}
