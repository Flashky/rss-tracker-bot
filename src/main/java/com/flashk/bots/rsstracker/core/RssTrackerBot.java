package com.flashk.bots.rsstracker.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.flashk.bots.rsstracker.events.CallbackQueryEventPublisher;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;

@Service
public class RssTrackerBot extends AbilityCallbackBot {
	
	@Autowired
	private FeedService feedService;
	
	@Autowired 
	private CallbackQueryEventPublisher eventPublisher;
	
	@Value("${bot.creatorId}")
	private Long creatorId;
	
	protected RssTrackerBot(@Value("${bot.token}")String botToken, @Value("${bot.username}") String botUsername) {
		super(botToken, botUsername);	
	}
	
	@Override
	void onCallbackQuery(CallbackQuery callbackQuery) {
		eventPublisher.publishCallbackQueryEvent(callbackQuery);
	}
	
	public Ability showfeeds() {
	    return Ability
	              .builder()
	              .name("showfeeds")
	              .info("Show your RSS feeds")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(this::showRssFeeds)
	              .build();
	}
	
	public Ability addfeed() {
	    return Ability
	              .builder()
	              .name("addfeed")
	              .info("Add a new RSS feed")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(this::addRssFeed)
	              .build();
	}
	
	private void showRssFeeds(MessageContext ctx) {
		
		// Obtain feeds
		List<Feed> feeds = feedService.listFeeds();
		
		// Prepare and send response
		SendMessage message = prepareShowRssFeedsResponse(ctx, feeds);
		
		execute(ctx.chatId(), message);
				
	}

	private void addRssFeed(MessageContext ctx) {
		
		// Prepare and send response
		SendMessage message = SendMessage.builder()
				.chatId(String.valueOf(ctx.chatId()))
				.text("What RSS feed do you want to add?")
				.build();
		
		execute(ctx.chatId(), message);
				
	}

	@Override
	public long creatorId() {
		return creatorId;
	}


	private SendMessage prepareShowRssFeedsResponse(MessageContext ctx, List<Feed> feeds) {
		
		SendMessage message = new SendMessage();
		message.setChatId(String.valueOf(ctx.chatId()));
		
		if(feeds.isEmpty()) {
			message.setText("You don't have any feeds.");
		} else {
			message.setText("Your RSS feeds:");
			InlineKeyboardMarkup rssFeedListReplyMarkup = createRssFeedListReplyMarkup(feeds);
			message.setReplyMarkup(rssFeedListReplyMarkup);
		}

		return message;
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


	


}
