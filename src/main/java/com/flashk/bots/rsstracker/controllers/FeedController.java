package com.flashk.bots.rsstracker.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.controllers.mappers.MessageFactory;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.TelegramRequest;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.request.CallbackQueryRequest;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
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
    private MessageFactory messageFactory;
    
	@Override
	public String getToken() {
		return token;
	}
    
	@MessageRequest("/myfeeds") 
	public SendMessage listFeeds(User user, Chat chat) {
		
		// Obtain feeds
		Page<Feed> feedPage = feedService.listFeeds(user.id(), CommonConstants.FIRST_PAGE, pageSize);
		
		// Prepare response
		return messageFactory.createFeedListSendMessage(user, chat, feedPage);
	
	}
	
    @CallbackQueryRequest(PathConstants.URI_FEEDS)
    public void listFeedsCallback(TelegramRequest request, 
    											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
    											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
    	// Obtain feeds
    	Page<Feed> feeds = feedService.listFeeds(request.getUser().id(), page, size);
    	
    	// Prepare response
    	EditMessageText message = messageFactory.createFeedListEditMessageText(request, feeds);
    	
    	replyCallBackQueryWithEditMessageText(request, message);
    	
    }


    
    @CallbackQueryRequest(PathConstants.URI_FEED_ITEMS)
    public void listFeedItems(TelegramRequest request, 
    										@BotPathVariable(PathConstants.FEED_ID) String feedId, 
											@BotPathVariable(PathConstants.QUERY_PAGE) Integer page, 
											@BotPathVariable(PathConstants.QUERY_SIZE) Integer size) {
    	
    	// Obtain feed  	
    	Optional<Feed> feed = feedService.getFeed(feedId);
    	
      	// Prepare response
    	EditMessageText message;
    	if(feed.isEmpty()) {
    		message = messageFactory.createRssNotFoundEditMessageText(request);
    	} else {
    		message = messageFactory.createFeedItemItemsEditMessageText(request, page, size, feed.get());
    	}

    	replyCallBackQueryWithEditMessageText(request, message);
    	
    }
	
    @MessageRequest("/newfeed" )
    public SendMessage addFeed(User user, Chat chat) {
    	
    	return messageFactory.createAddFeedSendMessage(user, chat);
    	
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

    @CallbackQueryRequest(PathConstants.URI_FEED_ACTION_DIALOG_DELETE)
    public void showDeleteFeedDialog(TelegramRequest request, @BotPathVariable(PathConstants.FEED_ID) String feedId) {
    	
    	// Obtain feed
    	Optional<Feed> feed = feedService.getFeed(feedId);
    	
    	// Prepare response
    	EditMessageText message;
    	if(feed.isEmpty()) {
    		message = messageFactory.createRssNotFoundEditMessageText(request);
    	} else {
    		message = messageFactory.createDeleteFeedEditMessageText(request, feed.get());
    	}
    	
    	replyCallBackQueryWithEditMessageText(request, message);
    	
    }

  
    @CallbackQueryRequest(PathConstants.URI_FEED_ACTION_DELETE)
    public void deleteFeed(TelegramRequest request, @BotPathVariable(PathConstants.FEED_ID) String feedId) {
    	
    	// Delete feed
    	Optional<Feed> feed = feedService.deleteFeed(feedId);
    	
    	// Prepare response
    	EditMessageText message;
    	if(feed.isEmpty()) {
    		message = messageFactory.createRssNotFoundEditMessageText(request);
    	} else {
        	message = messageFactory.createDeletedFeedEditMessageText(request, feed.get());
    	}
    	
    	replyCallBackQueryWithEditMessageText(request, message);
    	
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
    	SendMessage response = messageFactory.createAddedFeedSendMessage(request, feed);
		
		request.getTelegramBot().execute(response);
	}
	


	
	private void replyCallBackQueryWithEditMessageText(TelegramRequest request, EditMessageText message) {
		
		// Reply with message
    	request.getTelegramBot().execute(message);

    	// Answer callback query
    	request.getTelegramBot().execute(new AnswerCallbackQuery(request.getUpdate().callbackQuery().id()));
    	
	}
}
