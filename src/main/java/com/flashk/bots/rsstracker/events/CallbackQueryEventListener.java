package com.flashk.bots.rsstracker.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.flashk.bots.rsstracker.core.RssTrackerBot;
import com.flashk.bots.rsstracker.factories.InlineKeyboardButtonFactory;
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
    		sendEditMessageReplyMarkup(callbackQuery, createRssFeedListReplyMarkup(feeds));
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
		
        // Prepare reply text and keyboard markup
        InlineKeyboardMarkup replyMarkup = createRssFeedItemReplyMarkup(feed.get());

     	// Send message text and markup back to the user
		sendEditMessageText(callbackQuery, feed.get().getTitle());
		sendEditMessageReplyMarkup(callbackQuery, replyMarkup);
		
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
        sendEditMessageReplyMarkup(event.getCallbackQuery(), menuBackToFeedList());
    }

	@EventListener(condition = "#event.action eq 'confirm_delete'")
    public void onConfirmDeleteFeed(CallbackQueryEvent event) {
       
		System.out.println("confirm_delete feed " +event.getRssFeedId());
        
        // Send message text and markup back to the user
        sendEditMessageText(event.getCallbackQuery(), "Are you sure you want to delete the RSS feed?");
        sendEditMessageReplyMarkup(event.getCallbackQuery(), menuConfirmDeleteFeed(event.getRssFeedId()));
    }
	
	private InlineKeyboardMarkup menuBackToFeedList() {
		    
        List<InlineKeyboardButton> optionsRow = new ArrayList<>();
        optionsRow.add(buttonFactory.createShowFeedListButton("<< Back to RSS Feed List"));
        
        return InlineKeyboardMarkup.builder()
        		.keyboardRow(optionsRow)
        		.build();
	}
	
	private InlineKeyboardMarkup menuConfirmDeleteFeed(String rssFeedId) {
		
        List<InlineKeyboardButton> optionsRow = new ArrayList<>();
        optionsRow.add(buttonFactory.createDeleteFeedButton("Yes", rssFeedId));
        optionsRow.add(buttonFactory.createShowFeedSettingsButton("No", rssFeedId));
        
        return InlineKeyboardMarkup.builder()
        		.keyboardRow(optionsRow)
        		.build();
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
	
	private InlineKeyboardMarkup createRssFeedListReplyMarkup(List<Feed> feeds) {
		
		// Pre: feeds is not empty
		
		InlineKeyboardMarkupBuilder markupInlineBuilder = InlineKeyboardMarkup.builder();
		
		List<List<InlineKeyboardButton>> feedRows = new ArrayList<>();
		
		for(Feed feed : feeds) {
			
			InlineKeyboardButton feedButton = buttonFactory.createShowFeedSettingsButton(feed.getTitle(), feed.getId());
			
			// Add the button to a new row
			List<InlineKeyboardButton> feedRow = new ArrayList<>();
			feedRow.add(feedButton);
			feedRows.add(feedRow);
			
			markupInlineBuilder.keyboardRow(feedRow);
		}
		
		
		return markupInlineBuilder.build();
	}
	
	private InlineKeyboardMarkup createRssFeedItemReplyMarkup(Feed feed) {
		
		InlineKeyboardMarkupBuilder markupInlineBuilder = InlineKeyboardMarkup.builder();

		List<InlineKeyboardButton> row = new ArrayList<>();
		
		// View URL button
		InlineKeyboardButton keyboardButton = InlineKeyboardButton.builder()
				.text("View RSS feed")
				.url(feed.getUrl())
				.build();
		
		row.add(keyboardButton);
		
		// Delete button		
		row.add(buttonFactory.createDeleteFeedConfirmationButton("Delete RSS Feed", feed.getId()));
		markupInlineBuilder.keyboardRow(row);
		
		// Back to main menu
		row = new ArrayList<>();
		row.add(buttonFactory.createShowFeedListButton("<< Back to RSS Feed List"));
		
		markupInlineBuilder.keyboardRow(row);
		
		return markupInlineBuilder.build();
	}
    
}
