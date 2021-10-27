package com.flashk.bots.rsstracker.factories;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class InlineKeyboardButtonFactoryImpl implements InlineKeyboardButtonFactory {

	@Override
	public InlineKeyboardButton createShowFeedSettingsButton(String buttonText, String rssFeedId) {
		
		return InlineKeyboardButton.builder()
				.text(buttonText)
				.callbackData("POST /feeds/"+rssFeedId+"/settings")
				.build();
		
	}

	@Override
	public InlineKeyboardButton createDeleteFeedButton(String buttonText, String rssFeedId) {
		
		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("DELETE /feeds/"+rssFeedId)
        		.build();
		
	}

	@Override
	public InlineKeyboardButton createDeleteFeedConfirmationButton(String buttonText, String rssFeedId) {

		return InlineKeyboardButton.builder()
				.text(buttonText)
				.callbackData("POST /feeds/"+rssFeedId+"/delete-confirmation")
				.build();
	}
	
	@Override
	public InlineKeyboardButton createShowFeedListButton(String buttonText) {

		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("POST /feeds")
        		.build();

	}

	@Override
	public InlineKeyboardButton createShowFeedListPageButton(String buttonText, Integer pageNumber) {

		return InlineKeyboardButton.builder()
        		.text(buttonText)
        		.callbackData("POST /feeds?page="+String.valueOf(pageNumber))
        		.build();

	}
	
}
