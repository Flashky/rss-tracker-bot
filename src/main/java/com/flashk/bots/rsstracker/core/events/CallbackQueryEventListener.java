package com.flashk.bots.rsstracker.core.events;

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
import com.flashk.bots.rsstracker.core.services.FeedService;
import com.flashk.bots.rsstracker.core.services.model.Feed;

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
	
    @EventListener(condition = "#event.action eq 'show_list'")
    public void handleShowRssFeeds(CallbackQueryEvent event) {
       
    	System.out.println("show_list");
     	CallbackQuery callbackQuery = event.getCallbackQuery();
     	
        // Obtain feeds
     	List<Feed> feeds = feedService.listFeeds();
        
     	// Prepare reply text and keyboard markup
        String textMessage = "Your RSS feeds:";
        InlineKeyboardMarkup replyMarkup = createRssFeedListReplyMarkup(feeds);
        
     	// Update the text message to display the RSS feed title
		EditMessageText editMessageText = createEditMessageText(textMessage, callbackQuery);
		bot.execute(callbackQuery.getMessage().getChatId(), editMessageText);
		
		// Update the reply markup keyboard to show the RSS feed options
		EditMessageReplyMarkup editMessage = createEditMessageReplyMarkup(callbackQuery, replyMarkup);
		bot.execute(callbackQuery.getMessage().getChatId(), editMessage);
		
    }
    
    @EventListener(condition = "#event.action eq 'show'")
    public void handleShowFeedItem(CallbackQueryEvent event) {
        
    	System.out.println("show feed " +event.getRssFeedId());
        CallbackQuery callbackQuery = event.getCallbackQuery();
        
        Optional<Feed> feed = feedService.getFeed(event.getRssFeedId());
		
        // Prepare reply text and keyboard markup
        String textMessage = feed.get().getTitle();
        InlineKeyboardMarkup replyMarkup = createRssFeedItemReplyMarkup(feed.get());

		// Update the text message to display the RSS feed title
		EditMessageText editMessageText = createEditMessageText(textMessage, callbackQuery);
		bot.execute(callbackQuery.getMessage().getChatId(), editMessageText);
		
		// Update the reply markup keyboard to show the RSS feed options
		EditMessageReplyMarkup editMessage = createEditMessageReplyMarkup(callbackQuery, replyMarkup);
		bot.execute(callbackQuery.getMessage().getChatId(), editMessage);
		
    }
    
    @EventListener(condition = "#event.action eq 'delete'")
    public void handleDeleteFeedItem(CallbackQueryEvent event) {
        System.out.println("delete feed " +event.getRssFeedId());
    }
    

	/**
	 * @param textMessage
	 * @param callbackQuery
	 * @return
	 */
	private EditMessageText createEditMessageText(String textMessage, CallbackQuery callbackQuery) {
		
		EditMessageText editMessageText = EditMessageText.builder()
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.messageId(callbackQuery.getMessage().getMessageId())
				.text(textMessage)
				.build();
		
		return editMessageText;
	}
	
	/**
	 * @param callbackQuery
	 * @param replyMarkup
	 * @return
	 */
	private EditMessageReplyMarkup createEditMessageReplyMarkup(CallbackQuery callbackQuery,
			InlineKeyboardMarkup replyMarkup) {
		
		EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
				.messageId(callbackQuery.getMessage().getMessageId())
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.replyMarkup(replyMarkup)
				.build();
		
		return editMessage;
	}
	
	private InlineKeyboardMarkup createRssFeedListReplyMarkup(List<Feed> feeds) {
		
		// Pre: feeds is not empty
		
		InlineKeyboardMarkupBuilder markupInlineBuilder = InlineKeyboardMarkup.builder();
		
		List<List<InlineKeyboardButton>> feedRows = new ArrayList<>();
		
		for(Feed feed : feeds) {
			
			InlineKeyboardButton feedButton = InlineKeyboardButton.builder()
					.text(feed.getTitle())
					.callbackData("show/"+feed.getId())
					.build();
			
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
		keyboardButton = InlineKeyboardButton.builder()
				.text("Delete RSS feed")
				.callbackData("delete/"+feed.getId())
				.build();
		
		row.add(keyboardButton);
		markupInlineBuilder.keyboardRow(row);
		
		// Back to main menu
		row = new ArrayList<>();
		keyboardButton = InlineKeyboardButton.builder()
				.text("<< Return to feed list")
				.callbackData("show_list")
				.build();
		
		row.add(keyboardButton);
		markupInlineBuilder.keyboardRow(row);
		
		return markupInlineBuilder.build();
	}
    
}
