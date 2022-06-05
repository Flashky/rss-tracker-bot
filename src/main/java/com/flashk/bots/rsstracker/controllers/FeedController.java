package com.flashk.bots.rsstracker.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.flashk.bots.rsstracker.constants.MessageCode;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.TelegramRequest;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;

@BotController
public class FeedController implements TelegramMvcController {

    @Value("${bot.token}")
    private String token;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private FeedService feedService;
    
	@Override
	public String getToken() {
		return token;
	}
    
    @MessageRequest(value = "/newfeed" )
    public SendMessage addFeed(User user, Chat chat) {
    	
    	ForceReply forceReply = new ForceReply().inputFieldPlaceholder("https://your-rss-feed-url");
    	
    	String addFeedText = messageSource.getMessage(MessageCode.RSS_FEED_ADD, 
    													null, 
    													Locale.forLanguageTag(user.languageCode()));
    	
    	return new SendMessage(chat.id(), addFeedText).replyMarkup(forceReply);
    	
    }
    
    @MessageRequest
    public void reply(TelegramRequest request) {
    	
    	Message replyToMessage = request.getMessage().replyToMessage();
    	
    	if(replyToMessage == null) {
    		return;
    	}
    	
		if(messageSource.getMessage(MessageCode.RSS_FEED_ADD, 
									null, 
									Locale.forLanguageTag(request.getUser().languageCode())).equals(replyToMessage.text())) {
			createFeed(request);
		}

    }

    /**
     * Creates a feed using the request data and sends a confirmation to the user.
     * @param request The request containing all the user, chat and message information.
     */
	private void createFeed(TelegramRequest request) {
		
		Feed feed = feedService.createFeed(request.getUser().id(), 
											request.getChat().id(), 
											request.getMessage().text());
		
		String feedCreatedMessage = messageSource.getMessage(MessageCode.RSS_FEED_ADDED, 
																new Object[] { feed.getTitle() }, 
																Locale.forLanguageTag(request.getUser().languageCode()));
	
		request.getTelegramBot().execute(new SendMessage(request.getChat().id(), feedCreatedMessage));
	}
}
