package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.repositories.utils.PageBuilder;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class ReplyMarkupFactory {

	@Autowired 
	private InlineKeyboardButtonFactory buttonFactory;
	
	public Optional<InlineKeyboardMarkup> createFeedPage(User user, Page<Feed> feedPage) {
		
		if(feedPage.isEmpty()) {
			return Optional.empty();
		}

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Feeds
		feedPage.getContent().stream().map(buttonFactory::createFirstItemPageButton).forEach(replyMarkup::addRow);
		
		// Pagination buttons
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		buttonFactory.createPreviousFeedPageButton(user, feedPage).ifPresent(paginationButtons::add);
		buttonFactory.createNextFeedPageButton(user, feedPage).ifPresent(paginationButtons::add);
		
		InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
		replyMarkup.addRow(paginationButtons.toArray(paginationButtonsArray));
		
		return Optional.of(replyMarkup);
		
	}
	
	public InlineKeyboardMarkup createItemPage(User user, Feed feed, int page, int size) {
		
		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();

		if(!feed.getItems().isEmpty()) {
			Page<Item> itemPage = new PageBuilder<>(feed.getItems()).of(page, size).build();
		
			// Items
			itemPage.getContent().stream().map(buttonFactory::createItemUrlButton).forEach(replyMarkup::addRow);
			
			// Pagination
			List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
			
			buttonFactory.createPreviousItemPageButton(user, feed, itemPage).ifPresent(paginationButtons::add);
			buttonFactory.createNextItemPageButton(user, feed, itemPage).ifPresent(paginationButtons::add);
			
			if(!paginationButtons.isEmpty()) {
				InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
				paginationButtons.toArray(paginationButtonsArray);
			
				replyMarkup.addRow(paginationButtonsArray);
			}
		}
		
		// Options
    	InlineKeyboardButton[] optionButtons = {
    			buttonFactory.createFirstFeedPageButton(MessageConstants.LABEL_BACK_FEED_LIST, user),
    			buttonFactory.createOpenDialogDeleteFeedButton(user, feed)
    	};
    	
		replyMarkup.addRow(optionButtons);
		
		return replyMarkup;
	}
	
    public InlineKeyboardMarkup createDialogDeleteFeed(User user, Feed feed) {
    	
    	return new InlineKeyboardMarkup(
    			buttonFactory.createDeleteFeedButton(user, feed),
    			buttonFactory.createFirstItemPageButton(MessageConstants.LABEL_BUTTON_NO, user, feed));
    	
    }
}
