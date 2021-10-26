package com.flashk.bots.rsstracker.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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
	
	public Ability showFeeds() {
	    return Ability
	              .builder()
	              .name("showfeeds")
	              .info("Show your RSS feeds")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(this::showFeeds)
	              .build();
	}
	
	// https://github.com/rubenlagus/TelegramBots/wiki/Using-Replies
	public Ability addFeed() {
	    return Ability
	              .builder()
	              .name("addfeed")
	              .info("Add a new RSS feed")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(ctx -> silent.forceReply("What RSS feed do you want to add?", ctx.chatId()))
	              .reply(this::addRssFeed,
	            	// Conditions to trigger the action on the reply:
	            	// The update is a reply to the specified text message from the bot.
	            	Flag.MESSAGE,
	            	Flag.REPLY,
	            	isReplyToBot(),
	            	isReplyToMessage("What RSS feed do you want to add?"))
	              .build();
	}
	
    private Predicate<Update> isReplyToBot() {
    	
    	return upd -> {
    		System.out.println("Checking reply to bot");
    		return upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    	};
    }
    
	private Predicate<Update> isReplyToMessage(String message) {
		return upd -> {
			System.out.println("Checking reply to message");
	        Message reply = upd.getMessage().getReplyToMessage();
	        boolean isReplyToMessage= reply.hasText() && reply.getText().equalsIgnoreCase(message); 
	        return isReplyToMessage;
		};
    }
    
	private void showFeeds(MessageContext ctx) {
		
		// Obtain feeds
		List<Feed> feeds = feedService.listFeeds();
		
		// Prepare and send response
		SendMessage message = prepareShowRssFeedsResponse(ctx, feeds);
		
		execute(ctx.chatId(), message);
				
	}

	private void addRssFeed(BaseAbilityBot bot, Update update) {
		
		System.out.println("reply!!");;
  	  	silent.send("Your new tracker is: " + update.getMessage().getText(), update.getMessage().getChatId());
			
	}

	@Override
	public long creatorId() {
		return creatorId;
	}


	private SendMessage prepareShowRssFeedsResponse(MessageContext ctx, List<Feed> feeds) {
		
		SendMessageBuilder sendMessageBuilder = SendMessage.builder()
				.chatId(String.valueOf(ctx.chatId()));
		
		if(feeds.isEmpty()) {
			sendMessageBuilder.text("You don't have any feeds.");
		} else {
			sendMessageBuilder.text("Your RSS feeds:")
				.replyMarkup(createRssFeedListReplyMarkup(feeds));
		}

		return sendMessageBuilder.build();
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
