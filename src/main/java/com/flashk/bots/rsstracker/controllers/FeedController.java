package com.flashk.bots.rsstracker.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.Constants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.controllers.mappers.FeedsReplyMarkupMapper;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
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
    
    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
    @Autowired
    private FeedService feedService;
    
    @Autowired
    private LocalizedMessageService messageService;
    
    @Autowired
    private FeedsReplyMarkupMapper feedsReplyMarkupMapper;
    
	@Override
	public String getToken() {
		return token;
	}
    
	@MessageRequest("/myfeeds") 
	public SendMessage listFeeds(User user, Chat chat) {
		
		// Obtain feeds
		PagedResponse<Feed> feeds = feedService.listFeeds(user.id(), Constants.FIRST_PAGE, pageSize);
		
		// Prepare response
		Optional<InlineKeyboardMarkup> replyMarkup = feedsReplyMarkupMapper.map(feeds);
		
		if(replyMarkup.isEmpty()) {
		
    		return new SendMessage(chat.id(), messageService.getText(MessageConstants.RSS_FEED_LIST_EMPTY, user.languageCode()));
    		
    	} else {
    		
    		return new SendMessage(chat.id(), messageService.getText(MessageConstants.RSS_FEED_LIST_TITLE, user.languageCode()))
    						.replyMarkup(replyMarkup.get());
    	}
		
	
	}
	
    @CallbackQueryRequest(PathConstants.URI_FEEDS)
    public EditMessageText listFeedsCallback(TelegramRequest request, 
    											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
    											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
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
    		
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), 
    									messageService.getText(MessageConstants.RSS_FEED_LIST_EMPTY, user.languageCode()));
    		
    	} else {
  
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), 
    									messageService.getText(MessageConstants.RSS_FEED_LIST_TITLE, user.languageCode()))
    				.replyMarkup(replyMarkup.get());
 
    	}
    	
    }
    
    @CallbackQueryRequest(PathConstants.URI_FEED_SHOW)
    public EditMessageText getFeed(TelegramRequest request, @BotPathVariable(PathConstants.FEED_ID) String feedId) {
    	
    	User user = request.getUser();
    	Chat chat = request.getChat();
    	
    	// Answer callback query
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));
    	
    	// Obtain feed
    	Optional<Feed> feed = feedService.getFeed(feedId);
    	
    	// Prepare response
    	
    	if(feed.isEmpty()) {
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), "Sorry, I couldn't find that feed.");
    	} else {
    		
    		
    	}
    	
    	return null;
    	
    }
	
    @MessageRequest("/newfeed" )
    public SendMessage addFeed(User user, Chat chat) {
    	
    	ForceReply forceReply = new ForceReply().inputFieldPlaceholder("https://your-rss-feed-url");
    	
    	return new SendMessage(chat.id(), messageService.getText(MessageConstants.RSS_FEED_ADD, user.languageCode()))
    			.replyMarkup(forceReply);
    	
    }
    
    @MessageRequest
    public void reply(TelegramRequest request) {
    	
    	Message replyToMessage = request.getMessage().replyToMessage();
    	
    	if(replyToMessage == null) {
    		return;
    	}
    	
		if(messageService.getText(MessageConstants.RSS_FEED_ADD, request.getUser().languageCode())
				.equals(replyToMessage.text())) {
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

		SendMessage feedCreatedMessage = new SendMessage(request.getChat().id(), 
															messageService.getText(MessageConstants.RSS_FEED_ADDED, request.getUser().languageCode(), feed.getTitle())); 
		
		request.getTelegramBot().execute(feedCreatedMessage);
	}
}
