package com.flashk.bots.rsstracker.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.controllers.mappers.FeedsReplyMarkupMapper;
import com.flashk.bots.rsstracker.controllers.mappers.ItemsReplyMarkupMapper;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
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
    private ItemsReplyMarkupMapper itemsReplyMarkupMapper;
    
    @Autowired
    private FeedsReplyMarkupMapper feedsReplyMarkupMapper;
    
	@Override
	public String getToken() {
		return token;
	}
    
	@MessageRequest("/myfeeds") 
	public SendMessage listFeeds(User user, Chat chat) {
		
		// Obtain feeds
		Page<Feed> feeds = feedService.listFeeds(user.id(), CommonConstants.FIRST_PAGE, pageSize);
		
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
    	Page<Feed> feeds = feedService.listFeeds(user.id(), page, size);
    	
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
    
    @CallbackQueryRequest(PathConstants.URI_FEED_ITEMS)
    public EditMessageText listFeedItems(TelegramRequest request, 
    										@BotPathVariable(PathConstants.FEED_ID) String feedId, 
											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
    	Chat chat = request.getChat();
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
    	
    	// Obtain feed
    	Optional<Feed> feed = feedService.getFeed(feedId);
    	
    	if(feed.isEmpty()) {
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), "Sorry, I couldn't find that feed.");
    	}

    	// Answer callback query
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));
    	
    	// Prepare response
    	Optional<InlineKeyboardMarkup> replyMarkup = itemsReplyMarkupMapper.map(feed.get(), page, size);
    	
    	if(replyMarkup.isEmpty()) {
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), "There are no items on this feed yet");
    	} else {
    		return new EditMessageText(chat.id(), callbackQuery.message().messageId(), feed.get().getTitle())
    				.replyMarkup(replyMarkup.get());
    	}
    	
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

    	// Prepare response
    	Optional<InlineKeyboardMarkup> replyMarkup = itemsReplyMarkupMapper.map(feed, CommonConstants.FIRST_PAGE, pageSize);
    	
		SendMessage feedCreatedMessage = new SendMessage(request.getChat().id(), 
															messageService.getText(MessageConstants.RSS_FEED_ADDED, request.getUser().languageCode(), feed.getTitle()))
				.replyMarkup(replyMarkup.get());

		
		request.getTelegramBot().execute(feedCreatedMessage);
	}
}
