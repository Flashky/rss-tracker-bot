package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.github.kshashov.telegram.api.TelegramRequest;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

@Component
public class MessageFactory {

    
    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
	@Autowired
	private ReplyMarkupFactory replyMarkupFactory;
	
    @Autowired
    private LocalizedMessageService messageService;
    
    // Callback queries - EditMessageText
    
	public EditMessageText createFeedListEditMessageText(TelegramRequest request, Page<Feed> feeds) {
		
		User user = request.getUser();
		Chat chat = request.getChat();
		CallbackQuery callbackQuery = request.getUpdate().callbackQuery();
		
		Optional<InlineKeyboardMarkup> replyMarkup = replyMarkupFactory.createFeedPage(user, feeds);
    	
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
		return message;
	}
	
	public EditMessageText createRssNotFoundEditMessageText(TelegramRequest request) {
		String text = messageService.getText(MessageConstants.RSS_FEED_NOT_FOUND, request.getUser().languageCode());
		return new EditMessageText(request.getChat().id(), request.getUpdate().callbackQuery().message().messageId(), text);
	}
	
	public EditMessageText createFeedItemItemsEditMessageText(TelegramRequest request, Integer page, Integer size, Feed feed) {

		User user = request.getUser();
		InlineKeyboardMarkup replyMarkup = replyMarkupFactory.createItemPage(user, feed, page, size);
		String text = messageService.getText(MessageConstants.RSS_FEED_ITEM_LIST_TITLE, user.languageCode(), feed.getTitle());
		return getEditMessageText(request, text, replyMarkup);
		
	}
	
	public EditMessageText createDeleteFeedEditMessageText(TelegramRequest request, Feed feed) {

		User user = request.getUser();
		InlineKeyboardMarkup replyMarkup = replyMarkupFactory.createDialogDeleteFeed(user, feed);
		String text = messageService.getText(MessageConstants.DIALOG_TITLE_DELETE_FEED, user.languageCode(), feed.getTitle());
		return getEditMessageText(request, text, replyMarkup);

	}
	
	public EditMessageText createDeletedFeedEditMessageText(TelegramRequest request, Feed feed) {

		String text = messageService.getText(MessageConstants.MESSAGE_FEED_DELETED, request.getUser().languageCode(), feed.getTitle(), feed.getSourceLink());
		
		return new EditMessageText(request.getChat().id(), request.getUpdate().callbackQuery().message().messageId(), text)
				.parseMode(ParseMode.Markdown);

	}
	
	
	// Simple messages - SendMessage

	public SendMessage createFeedListSendMessage(User user, Chat chat, Page<Feed> feedPage) {
		
		Optional<InlineKeyboardMarkup> replyMarkup = replyMarkupFactory.createFeedPage(user, feedPage);
		
		if(replyMarkup.isEmpty()) {
			String text = messageService.getText(MessageConstants.RSS_FEED_LIST_EMPTY, user.languageCode());
			return new SendMessage(chat.id(), text);
    	} else {
    		String text = messageService.getText(MessageConstants.RSS_FEED_LIST_TITLE, user.languageCode(), feedPage.getTotalElements());
    		return new SendMessage(chat.id(), text)
    						.replyMarkup(replyMarkup.get())
    						.parseMode(ParseMode.Markdown);
    	}
		
	}
	
	public SendMessage createAddFeedSendMessage(User user, Chat chat) {
		
	   	ForceReply forceReply = new ForceReply().inputFieldPlaceholder("https://your-rss-feed-url");
    	String text =  messageService.getText(MessageConstants.RSS_FEED_ADD, user.languageCode());
    	
		return new SendMessage(chat.id(), text).replyMarkup(forceReply);
	}

	public SendMessage createAddedFeedSendMessage(TelegramRequest request, Feed feed) {
		
		InlineKeyboardMarkup replyMarkup = replyMarkupFactory.createItemPage(request.getUser(), feed, CommonConstants.FIRST_PAGE, pageSize);
    	String text = messageService.getText(MessageConstants.RSS_FEED_ADD_SUCCESS, request.getUser().languageCode(), feed.getTitle());
    	
    	return new SendMessage(request.getChat().id(), text)
    				.replyMarkup(replyMarkup)
    				.parseMode(ParseMode.Markdown);
    	
	}
	
	
	private EditMessageText getEditMessageText(TelegramRequest request, String text, InlineKeyboardMarkup replyMarkup) {

		return new EditMessageText(request.getChat().id(), request.getUpdate().callbackQuery().message().messageId(), text)
					.replyMarkup(replyMarkup)
					.parseMode(ParseMode.Markdown);

	}

}
