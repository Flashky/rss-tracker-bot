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
import com.pengrad.telegrambot.model.request.ParseMode;
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
		Optional<InlineKeyboardMarkup> replyMarkup = feedsReplyMarkupMapper.map(user, feeds);
		
		if(replyMarkup.isEmpty()) {
		
    		return new SendMessage(chat.id(), messageService.getText(MessageConstants.RSS_FEED_LIST_EMPTY, user.languageCode()));
    		
    	} else {
    		
    		return new SendMessage(chat.id(), messageService.getText(MessageConstants.RSS_FEED_LIST_TITLE, user.languageCode(), feeds.getTotalElements()))
    						.replyMarkup(replyMarkup.get())
    						.parseMode(ParseMode.Markdown);
    	}
		
	
	}
	
    @CallbackQueryRequest(PathConstants.URI_FEEDS)
    public void listFeedsCallback(TelegramRequest request, 
    											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
    											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
    	User user = request.getUser();
    	Chat chat = request.getChat();
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();

    	// Obtain feeds
    	Page<Feed> feeds = feedService.listFeeds(user.id(), page, size);
    	
    	// Prepare response
    	Optional<InlineKeyboardMarkup> replyMarkup = feedsReplyMarkupMapper.map(user, feeds);
    	
    	EditMessageText message;
    	if(replyMarkup.isEmpty()) {
    		
    		message = new EditMessageText(chat.id(), callbackQuery.message().messageId(), 
    									messageService.getText(MessageConstants.RSS_FEED_LIST_EMPTY, user.languageCode()));
    		
    	} else {
    		
    		message = new EditMessageText(chat.id(), callbackQuery.message().messageId(), 
    									messageService.getText(MessageConstants.RSS_FEED_LIST_TITLE, user.languageCode(), feeds.getTotalElements()))
    				.replyMarkup(replyMarkup.get())
    				.parseMode(ParseMode.Markdown);
 
    	}
    	
    	// Reply with message
    	request.getTelegramBot().execute(message);
    	
    	// Answer callback query
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));
    	
    }
    
    @CallbackQueryRequest(PathConstants.URI_FEED_ITEMS)
    public void listFeedItems(TelegramRequest request, 
    										@BotPathVariable(PathConstants.FEED_ID) String feedId, 
											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
    	Chat chat = request.getChat();
    	User user = request.getUser();
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
    	
    	// Obtain feed
    	Optional<Feed> feed = feedService.getFeed(feedId);
    	
      	// Prepare response
    	EditMessageText message;
    	if(feed.isEmpty()) {
    		message = new EditMessageText(chat.id(), callbackQuery.message().messageId(), "Sorry, I couldn't find that feed.");
    	} else {
    		
    		InlineKeyboardMarkup replyMarkup = itemsReplyMarkupMapper.map(request.getUser(), feed.get(), page, size);
        	
        	String text = messageService.getText(MessageConstants.RSS_FEED_ITEM_LIST_TITLE, user.languageCode(), feed.get().getTitle());
        	message = new EditMessageText(chat.id(), callbackQuery.message().messageId(), text)
        				.replyMarkup(replyMarkup)
        				.parseMode(ParseMode.Markdown);
    	}

    	// Reply with message
    	request.getTelegramBot().execute(message);
    	
    	// Answer callback query
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));
    	
  
    	
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
		
		// Create feed
		Feed feed = feedService.createFeed(request.getUser().id(), 
											request.getChat().id(), 
											request.getMessage().text());

		// Bot response
    	InlineKeyboardMarkup replyMarkup = itemsReplyMarkupMapper.map(request.getUser(), feed, CommonConstants.FIRST_PAGE, pageSize);
    	
    	SendMessage response = new SendMessage(request.getChat().id(), messageService.getText(MessageConstants.RSS_FEED_ADD_SUCCESS, request.getUser().languageCode(), feed.getTitle()))
    				.replyMarkup(replyMarkup)
    				.parseMode(ParseMode.Markdown);
		
		request.getTelegramBot().execute(response);
	}
}
