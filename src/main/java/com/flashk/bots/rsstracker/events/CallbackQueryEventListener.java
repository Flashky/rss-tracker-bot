package com.flashk.bots.rsstracker.events;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
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
 * @see https://reflectoring.io/spring-boot-application-events-explained/
 * @author Flashk
 */
@Component
public class CallbackQueryEventListener {

	@Autowired
	private RssTrackerBot bot;
	
	@Autowired
	private FeedService feedService;
	
	@Autowired
	private InlineKeyboardMarkupFactory replyMarkupFactory;
	
	@Autowired
	private InlineKeyboardButtonFactory buttonFactory;
	
	
    @EventListener(condition = "#event.action eq 'show_list'")
    public void onShowFeeds(CallbackQueryEvent event) {
       
    	System.out.println("show_list");
     	CallbackQuery callbackQuery = event.getCallbackQuery();
     	
        // Obtain feeds
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

	@EventListener(condition = "#event.action eq 'show'")
    public void onShowFeed(CallbackQueryEvent event) {
        
    	System.out.println("show feed " +event.getRssFeedId());
    	
		// TODO When trying to delete a non existing feed, throw an exception
		// TODO At the exception handler, use an AnswerCallbackQuery using show_alert == true
		// TODO More info: https://core.telegram.org/bots/api#answercallbackquery
    	
        CallbackQuery callbackQuery = event.getCallbackQuery();
        
        Optional<Feed> feed = feedService.getFeed(event.getRssFeedId());

     	// Send message text and markup back to the user
		sendEditMessageText(callbackQuery, feed.get().getTitle());
		sendEditMessageReplyMarkup(callbackQuery, 
				replyMarkupFactory.createShowFeedSettingsReplyMarkup(feed.get()));
		
    }

	@EventListener(condition = "#event.action eq 'delete'")
    public void onDeleteFeed(CallbackQueryEvent event) {
       
		System.out.println("delete feed " +event.getRssFeedId());
        
        // TODO When trying to delete a non existing feed, throw an exception
        // TODO At the exception handler, use an AnswerCallbackQuery using show_alert == true
        // TODO More info: https://core.telegram.org/bots/api#answercallbackquery
		
		// Remove the feed
        feedService.deleteFeed(event.getRssFeedId());
        
        // Send message text and markup back to the user
        sendEditMessageText(event.getCallbackQuery(), "The feed has been removed.");
        sendEditMessageReplyMarkup(event.getCallbackQuery(), 
        		replyMarkupFactory.createSingleButtonReplyMarkup(
        				buttonFactory.createShowFeedListButton("<<Back to RSS Feed List")));
    }

	@EventListener(condition = "#event.action eq 'confirm_delete'")
    public void onConfirmDeleteFeed(CallbackQueryEvent event) {
       
		System.out.println("confirm_delete feed " +event.getRssFeedId());
        
        // Send message text and markup back to the user
        sendEditMessageText(event.getCallbackQuery(), "Are you sure you want to delete the RSS feed?");
        sendEditMessageReplyMarkup(event.getCallbackQuery(), 
        		replyMarkupFactory.createDeleteFeedConfirmationReplyMarkup(event.getRssFeedId()));
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
