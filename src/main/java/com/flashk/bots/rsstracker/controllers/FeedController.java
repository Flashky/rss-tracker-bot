package com.flashk.bots.rsstracker.controllers;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.mappers.FeedsReplyMarkupMapper;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.TelegramRequest;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.request.CallbackQueryRequest;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

@BotController
public class FeedController implements TelegramMvcController {

    @Value("${bot.token}")
    private String token;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private FeedService feedService;
    
    @Autowired
    private FeedsReplyMarkupMapper feedsReplyMarkupMapper;
    
	@Override
	public String getToken() {
		return token;
	}
    
	@MessageRequest(value = "/myfeeds") 
	public SendMessage listFeeds(User user, Chat chat) {
		
		// Obtain feeds
		PagedResponse<Feed> feeds = feedService.listFeeds(user.id(), 0, 15);
		
		// Prepare response
		Optional<InlineKeyboardMarkup> replyMarkup = feedsReplyMarkupMapper.map(feeds);
		
		if(replyMarkup.isEmpty()) {
			
			String noFeedsText = messageSource.getMessage(MessageConstants.RSS_FEED_LIST_EMPTY, 
					null, 
					Locale.forLanguageTag(user.languageCode()));
			
    		return new SendMessage(chat.id(), noFeedsText);
    		
    	} else {
    		
    		String feedListTitleText = messageSource.getMessage(MessageConstants.RSS_FEED_LIST_TITLE, 
					null, 
					Locale.forLanguageTag(user.languageCode()));
    		
    		SendMessage feedsMessage = new SendMessage(chat.id(), feedListTitleText)
    									.replyMarkup(replyMarkup.get());
  
    		return feedsMessage;
    	}
		
	
	}
	
    @CallbackQueryRequest(value = "/feeds?page={page}&size={size}")
    public EditMessageText listFeedsCallback(TelegramRequest request, @BotPathVariable("page") Integer page, @BotPathVariable("size") Integer size) {
    	
    	User user = request.getUser();
    	Chat chat = request.getChat();
    	
    	// Answer callback query
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));

    	
    	// Obtain feeds
    	PagedResponse<Feed> feeds = feedService.listFeeds(user.id(), page, size);
    	
    	// Prepare response
    	Optional<InlineKeyboardMarkup> replyMarkup = feedsReplyMarkupMapper.map(feeds);
    	
    	if(replyMarkup.isEmpty()) {
    		
    		String noFeedsText = messageSource.getMessage(MessageConstants.RSS_FEED_LIST_EMPTY, 
					null, 
					Locale.forLanguageTag(user.languageCode()));
    		
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), noFeedsText);
    		
    	} else {
    		
    		String feedListTitleText = messageSource.getMessage(MessageConstants.RSS_FEED_LIST_TITLE, 
					null, 
					Locale.forLanguageTag(user.languageCode()));
    		
    		EditMessageText feedsMessage = new EditMessageText(chat.id(),  callbackQuery.message().messageId(), feedListTitleText)
    				.replyMarkup(replyMarkup.get());
  
    		return feedsMessage;
    	}
    	
    }
	
	
    @MessageRequest(value = "/newfeed" )
    public SendMessage addFeed(User user, Chat chat) {
    	
    	ForceReply forceReply = new ForceReply().inputFieldPlaceholder("https://your-rss-feed-url");
    	
    	String addFeedText = messageSource.getMessage(MessageConstants.RSS_FEED_ADD, 
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
    	
		if(messageSource.getMessage(MessageConstants.RSS_FEED_ADD, 
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
		
		String feedCreatedMessage = messageSource.getMessage(MessageConstants.RSS_FEED_ADDED, 
																new Object[] { feed.getTitle() }, 
																Locale.forLanguageTag(request.getUser().languageCode()));
	
		request.getTelegramBot().execute(new SendMessage(request.getChat().id(), feedCreatedMessage));
	}
}
