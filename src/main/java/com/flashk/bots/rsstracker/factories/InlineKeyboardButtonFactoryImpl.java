package com.flashk.bots.rsstracker.factories;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class InlineKeyboardButtonFactoryImpl implements InlineKeyboardButtonFactory {

	@Override
	public InlineKeyboardButton createShowFeedSettingsButton(String buttonText, String rssFeedId) {
		
		return InlineKeyboardButton.builder()
				.text(buttonText)
				.callbackData("show/"+rssFeedId)
				.build();
		
	}

	@Override
	public InlineKeyboardButton createDeleteFeedButton(String buttonText, String rssFeedId) {
		
		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("delete/"+rssFeedId)
        		.build();
		
	}

	@Override
	public InlineKeyboardButton createDeleteFeedConfirmationButton(String buttonText, String rssFeedId) {

		return InlineKeyboardButton.builder()
				.text(buttonText)
				.callbackData("confirm_delete/"+rssFeedId)
				.build();
	}
	
	@Override
	public InlineKeyboardButton createShowFeedListButton(String buttonText) {

		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("show_list")
        		.build();

	}

	@Override
	public InlineKeyboardButton createShowFeedListPageButton(String buttonText, Integer pageNumber) {

		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("show_list?page="+String.valueOf(pageNumber))
        		.build();

	}
	
}
