package com.flashk.bots.rsstracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.flashk.bots.rsstracker.constants.MessageCode;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.PagedResponse;
import com.flashk.bots.rsstracker.services.model.Pagination;
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
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
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
    
	@Override
	public String getToken() {
		return token;
	}
    
	@MessageRequest(value = "/myfeeds") 
	public SendMessage listFeeds(User user, Chat chat) {
		
		PagedResponse<Feed> feeds = feedService.listFeeds(user.id(), 0, 15);
		
		if(feeds.isEmpty()) {
    		return new SendMessage(chat.id(), "You don't have any feeds.");
    	} else {
    		
    		// TODO Inicio refactorización a mapper: PagedResponse<Feed> -> InlineKeyboardMarkup
    		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
    		
    		for(Feed feed : feeds.getData()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton(feed.getTitle())
    					.callbackData("/feeds/"+feed.getId()+"?action=show");
    			
    			replyMarkup.addRow(button);
    		}
    		
    		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
    		Pagination pagination = feeds.getPagination();
    		int size = pagination.getSize();
    		
    		if(pagination.getPreviousPage().isPresent()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton("<<")
    													.callbackData("/feeds?page="+pagination.getPreviousPage().get()+"&size="+size);
    			
    			paginationButtons.add(button);
    		}
    		
    		if(pagination.getNextPage().isPresent()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton(">>")
						.callbackData("/feeds?page="+pagination.getNextPage().get()+"&size="+size);
    			
    			paginationButtons.add(button);
    		}
    		
    		InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
    		paginationButtons.toArray(paginationButtonsArray);
    		replyMarkup.addRow(paginationButtonsArray);
    		
    		// TODO Fin refactorización a mapper
    		
    		SendMessage  feedsMessage = new SendMessage(chat.id(), "Your RSS feeds:")
    				.replyMarkup(replyMarkup);
  
    		return feedsMessage;
    	}
		
	
	}
	
    @CallbackQueryRequest(value = "/feeds?page={page}&size={size}")
    public EditMessageText listFeedsCallback(TelegramRequest request, @BotPathVariable("page") Integer page, @BotPathVariable("size") Integer size) {
    	
    	User user = request.getUser();
    	Chat chat = request.getChat();
    	
    	CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
    	int callbackQueryMessageId = callbackQuery.message().messageId();
    	
    	// Answer callback query
    	request.getTelegramBot().execute(new AnswerCallbackQuery(callbackQuery.id()));
    	
    	// Obtain feeds
    	PagedResponse<Feed> feeds = feedService.listFeeds(user.id(), page, size);
    	
    	if(feeds.isEmpty()) {
    		return new EditMessageText(chat.id(), callbackQueryMessageId, "You don't have any feeds.");
    	} else {
    		
    		// TODO Inicio refactorización a mapper: PagedResponse<Feed> -> InlineKeyboardMarkup
    		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
    		
    		for(Feed feed : feeds.getData()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton(feed.getTitle())
    					.callbackData("/feeds/"+feed.getId()+"?action=show");
    			
    			replyMarkup.addRow(button);
    		}
    		
    		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
    		Pagination pagination = feeds.getPagination();
    		
    		if(feeds.getPagination().getPreviousPage().isPresent()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton("<<")
    													.callbackData("/feeds?page="+pagination.getPreviousPage().get()+"&size="+size);
    			
    			paginationButtons.add(button);
    		}
    		
    		if(pagination.getNextPage().isPresent()) {
    			
    			InlineKeyboardButton button = new InlineKeyboardButton(">>")
						.callbackData("/feeds?page="+pagination.getNextPage().get()+"&size="+size);
    			
    			paginationButtons.add(button);
    		}
    		
    		InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
    		paginationButtons.toArray(paginationButtonsArray);
    		replyMarkup.addRow(paginationButtonsArray);
    		
    		// TODO Fin refactorización a mapper
    		
    		EditMessageText feedsMessage = new EditMessageText(chat.id(), callbackQueryMessageId, "Your RSS feeds:")
    				.replyMarkup(replyMarkup);
  
    		return feedsMessage;
    	}
    	
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
