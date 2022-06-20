package com.flashk.bots.rsstracker.controllers.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class DialogDeleteFeedReplyMarkupMapper {

	@Autowired 
	private InlineKeyboardButtonFactory buttonFactory;
	
    public InlineKeyboardMarkup map(User user, Feed feed) {
    	
    	return new InlineKeyboardMarkup(
    			buttonFactory.createDeleteFeedButton(user, feed),
    			buttonFactory.createFirstFeedPageButton(MessageConstants.LABEL_BUTTON_NO, user));
    }

}
