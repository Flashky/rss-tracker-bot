package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class FeedsReplyMarkupMapper {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
    @Autowired
    private LocalizedMessageService messageService;
    
	@Autowired
	private UrlBuilder urlBuilder;
	
	public Optional<InlineKeyboardMarkup> map(User user, Page<Feed> feeds) {
		
		if(feeds.isEmpty()) {
			return Optional.empty();
		}

		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Feeds
		map(replyMarkup, feeds.getContent());
		
		// Buttons
		map(replyMarkup, user, feeds);
		
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
											.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), CommonConstants.FIRST_PAGE, pageSize));
			
			replyMarkup.addRow(button);
			
		}
	}
	
	/**
	 * Maps the pagination to an InlineKeyboardMarkup.
	 * @param replyMarkup an InlineKeyboardMarkup to add pagination data.
	 * @param pagination the pagination to add.
	 */
	private void map(InlineKeyboardMarkup replyMarkup, User user, Page<Feed> feedPage) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(feedPage.hasPrevious()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(messageService.getText(MessageConstants.LABEL_PREVIOUS_PAGE, user.languageCode()))
					.callbackData(urlBuilder.getFeedsUri(feedPage.previousPageable().getPageNumber(), feedPage.getSize()));
			
			paginationButtons.add(button);
			
		}
		
		if(feedPage.hasNext()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(messageService.getText(MessageConstants.LABEL_NEXT_PAGE, user.languageCode()))
					.callbackData(urlBuilder.getFeedsUri(feedPage.nextPageable().getPageNumber(), feedPage.getSize()));		
			
			paginationButtons.add(button);
			
		}
		
		InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
		paginationButtons.toArray(paginationButtonsArray);
		
		replyMarkup.addRow(paginationButtonsArray);
	}
	
}
