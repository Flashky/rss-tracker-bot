package com.flashk.bots.rsstracker.factories;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface InlineKeyboardButtonFactory {

	/**
	 * Creates a button that on click must go to the RSS feed settings.
	 * @param buttonText the button text
	 * @param rssFeedId the rss feed to modify the settings.
	 * @return InlineKeyboardButton
	 */
	InlineKeyboardButton createShowFeedSettingsButton(String buttonText, String rssFeedId);
	
	/**
	 * Creates a button that on click must delete the specified RSS feed.
	 * @param buttonText the button text
	 * @param rssFeedId the rss feed to delete
	 * @return InlineKeyboardButton
	 */
	InlineKeyboardButton createDeleteFeedButton(String buttonText, String rssFeedId);
	
	/**
	 * Creates a button that on click must ask confirmation on deleting the specified RSS feed.
	 * @param buttonText the button text
	 * @param rssFeedId the rss feed to confirmationdeletion
	 * @return InlineKeyboardButton
	 */
	InlineKeyboardButton createDeleteFeedConfirmationButton(String buttonText, String rssFeedId);
	
	/**
	 * Creates a button that on click must go to the RSS feed list.
	 * @param buttonText the button text
	 * @return InlineKeyboardButton
	 */
	InlineKeyboardButton createShowFeedListButton(String buttonText);

	InlineKeyboardButton createShowFeedListPageButton(String buttonText, Integer pageNumber);
	
}
