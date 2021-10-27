package com.flashk.bots.rsstracker.core;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.flashk.bots.rsstracker.controllers.ActionController;
import com.flashk.bots.rsstracker.events.CallbackQueryEventPublisher;

@Service
public class RssTrackerBot extends AbilityCallbackBot {
	
	@Autowired
	private ActionController actionController;
	
	@Autowired 
	private CallbackQueryEventPublisher eventPublisher;
	
	@Value("${bot.creatorId}")
	private Long creatorId;
	
	protected RssTrackerBot(@Value("${bot.token}")String botToken, @Value("${bot.username}") String botUsername) {
		super(botToken, botUsername);	
	}
	
	public Ability showFeeds() {
	    return Ability
	              .builder()
	              .name("showfeeds")
	              .info("Show your RSS feeds")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(actionController::showFeeds)
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
	              .action(ctx -> silent.forceReply("What RSS feed do you want to add?", ctx.chatId()) )
	              .reply(actionController::addFeed,
	            	// Conditions to trigger the action on the reply:
	            	// The update is a reply to the specified text message from the bot.
	            	Flag.MESSAGE,
	            	Flag.REPLY,
	            	isReplyToBot(),
	            	isReplyToMessage("What RSS feed do you want to add?"))
	              .build();
	}
	
	@SuppressWarnings("unchecked")
	public Ability handleCallbackQueries() {
	    return Ability
	              .builder()
	              .name(DEFAULT)
	              .flag(Flag.CALLBACK_QUERY)
	              .info("Handle callback query")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(ctx -> eventPublisher.publishCallbackQueryEvent(ctx.update().getCallbackQuery()))
	              .post(actionController::answerCallbackQuery)
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

	@Override
	public long creatorId() {
		return creatorId;
	}


	

	


}
