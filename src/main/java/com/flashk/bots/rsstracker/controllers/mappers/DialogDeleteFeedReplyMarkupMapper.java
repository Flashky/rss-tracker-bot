package com.flashk.bots.rsstracker.controllers.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class DialogDeleteFeedReplyMarkupMapper {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
    @Autowired
    private LocalizedMessageService messageService;
    
    @Autowired
    private UrlBuilder urlBuilder;
    
    public InlineKeyboardMarkup map(User user, Feed feed) {
    	
    	InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
    	
    	InlineKeyboardButton[] dialog = {
    			createYesButton(user, feed),
    			createNoButton(user, feed)
    	};
    	
    	return replyMarkup.addRow(dialog);
    }

	private InlineKeyboardButton createYesButton(User user, Feed feed) {
    	
		String text = messageService.getText(MessageConstants.LABEL_BUTTON_YES, user.languageCode());
		
    	String deleteUri = UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ACTION_DELETE)
				.buildAndExpand(feed.getId())
				.toString();
		
    	return new InlineKeyboardButton(text).callbackData(deleteUri);
    }
	
    
    private InlineKeyboardButton createNoButton(User user, Feed feed) {
    	
    	String text = messageService.getText(MessageConstants.LABEL_BUTTON_NO, user.languageCode());
    	String feedUri = urlBuilder.getFeedItemsUri(feed.getId(), CommonConstants.FIRST_PAGE, pageSize);
    	
		return new InlineKeyboardButton(text).callbackData(feedUri);
	}
}
