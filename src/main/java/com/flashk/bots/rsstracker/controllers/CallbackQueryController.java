package com.flashk.bots.rsstracker.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import com.flashk.bots.rsstracker.core.RssTrackerBot;
import com.flashk.bots.rsstracker.factories.InlineKeyboardButtonFactory;
import com.flashk.bots.rsstracker.factories.InlineKeyboardMarkupFactory;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;

/**
 * Handles requests originated via callback_data.
 * @author Flashk
 *
 */
@RestController
public class CallbackQueryController {

	private Logger logger = LoggerFactory.getLogger(CallbackQueryController.class);
	
	@Autowired
	private RssTrackerBot bot;
	
	@Autowired
	private FeedService feedService;
	
	@Autowired
	private InlineKeyboardMarkupFactory replyMarkupFactory;
	
	@Autowired
	private InlineKeyboardButtonFactory buttonFactory;
	
	@PostMapping("/feeds/{feedId}/settings")
	public void showFeedSettings(@RequestBody CallbackQuery callbackQuery, @PathVariable String feedId) {
		
		logger.info("Endpoint de settings invocado!");
		
		Optional<Feed> feed = feedService.getFeed(feedId);

		// Send message text and markup back to the user
		sendEditMessageText(callbackQuery, feed.get().getTitle());
		sendEditMessageReplyMarkup(callbackQuery, 
				replyMarkupFactory.createShowFeedSettingsReplyMarkup(feed.get()));

	}
	
	@PostMapping("/feeds")
	public void showFeeds(@RequestBody CallbackQuery callbackQuery, 
							@RequestParam(required = false) Integer page) {
		
		List<Feed> feeds = feedService.listFeeds();
		
     	// Send message text and markup back to the user
        if(feeds.isEmpty()) {
        	sendEditMessageText(callbackQuery, "You don't have any feeds.");
        } else {
    		sendEditMessageText(callbackQuery, "Your RSS feeds:");
    		sendEditMessageReplyMarkup(callbackQuery, 
    				replyMarkupFactory.createFeedListReplyMarkup(feeds));
        }
	}
	
	@DeleteMapping("/feeds/{feedId}")
	public void deleteFeed(@RequestBody CallbackQuery callbackQuery, @PathVariable String feedId) {
		
		feedService.deleteFeed(feedId);
	        
        // Send message text and markup back to the user
        sendEditMessageText(callbackQuery, "The feed has been removed.");
        sendEditMessageReplyMarkup(callbackQuery, 
        		replyMarkupFactory.createSingleButtonReplyMarkup(
        				buttonFactory.createShowFeedListButton("<< Back to RSS Feed List")));
	        
	}
	
	@PostMapping("/feeds/{feedId}/delete-confirmation")
	public void showDeleteFeedConfirmation(@RequestBody CallbackQuery callbackQuery, @PathVariable String feedId) {
	        
        // Send message text and markup back to the user
        sendEditMessageText(callbackQuery, "Are you sure you want to delete the RSS feed?");
        sendEditMessageReplyMarkup(callbackQuery, 
        		replyMarkupFactory.createDeleteFeedConfirmationReplyMarkup(feedId));
	        
	        
	}
    private void sendEditMessageText(CallbackQuery callbackQuery, String textMessage) {
		
    	EditMessageText editMessageText = EditMessageText.builder()
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.messageId(callbackQuery.getMessage().getMessageId())
				.text(textMessage)
				.build();
    	
    	bot.execute(callbackQuery.getMessage().getChatId(), editMessageText);
	}
    
    private void sendEditMessageReplyMarkup(CallbackQuery callbackQuery, InlineKeyboardMarkup replyMarkup) {
		
		EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
				.messageId(callbackQuery.getMessage().getMessageId())
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.replyMarkup(replyMarkup)
				.build();
		
		bot.execute(callbackQuery.getMessage().getChatId(), editMessage);
		
	}
}
