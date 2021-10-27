package com.flashk.bots.rsstracker.factories;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.flashk.bots.rsstracker.services.model.Feed;

public interface InlineKeyboardMarkupFactory {

	InlineKeyboardMarkup createShowFeedSettingsReplyMarkup(Feed feed);
	InlineKeyboardMarkup createDeleteFeedConfirmationReplyMarkup(String rssFeedId);
	
	/**
	 * Creates an inline keyboard reply markup that displays a list of RSS feeds.
	 * @param feeds the feeds to be displayed
	 * @return InlineKeyboardMarkup
	 */
	InlineKeyboardMarkup createFeedListReplyMarkup(List<Feed> feeds);
	
	/**
	 * Creates a simple inline keyboard reply markup using just an InlineKeyboardButton.
	 * @param inlineKeyboardButton the button to be used
	 * @return InlineKeyboardMarkup
	 */
	InlineKeyboardMarkup createSingleButtonReplyMarkup(InlineKeyboardButton inlineKeyboardButton);
	
	
	


}
