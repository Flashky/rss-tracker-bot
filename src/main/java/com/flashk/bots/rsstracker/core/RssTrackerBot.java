package com.flashk.bots.rsstracker.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.flashk.bots.rsstracker.config.properties.BotConfig;
import com.flashk.bots.rsstracker.core.services.FeedService;
import com.flashk.bots.rsstracker.core.services.model.Feed;

@Service
public class RssTrackerBot extends AbilityBot {

	@Autowired
	private BotConfig config;

	@Autowired
	private FeedService feedService;
	
	protected RssTrackerBot(@Value("${bot.token}")String botToken, @Value("${bot.username}") String botUsername) {
		super(botToken, botUsername);	
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		
		// Handle abilities
		super.onUpdateReceived(update);
		
		// Handle callback queries
		if(update.getCallbackQuery() != null) {
			handle(update.getCallbackQuery());
		}
	   
    }
	   
	public Ability show() {
	    return Ability
	              .builder()
	              .name("show")
	              .info("Show your RSS feeds")
	              .input(0)
	              .locality(Locality.USER)
	              .privacy(Privacy.PUBLIC)
	              .action(this::showRssFeeds)
	              .build();
	}
	
	private void showRssFeeds(MessageContext ctx) {
		
		// Obtain feeds
		List<Feed> feeds = feedService.listFeeds();
		
		// Prepare and send response
		SendMessage message = prepareShowRssFeedsResponse(ctx, feeds);
		
		execute(ctx.chatId(), message);
				
	}
	
	private void handle(CallbackQuery callbackQuery) {
		
		System.out.println("Callback query: " +callbackQuery.getData());
		
		// Regex:
		// ((show|edit|delete|)[\/]+?([a-zA-Z0-9]*))|(show_list)
		// Captura "show/id", "edit/id", "delete/id" o "show_list" para determinar que acción hay que realizar.
		
		String regex = "(show|edit|delete)[\\/]([a-zA-Z0-9]*)|(show_list)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(callbackQuery.getData());
		
		if(matcher.matches()) {

			if(matcher.group(3) != null) {
				System.out.println("Action = " + matcher.group(3));
			} else {
				System.out.println("Action = " + matcher.group(1));
				System.out.println("Id = " + matcher.group(2));
			}

			
		}
		
		// Parameters: (action,id)
		// Returns: <T extends Serializable, Method extends BotApiMethod<T>> -> Method
		
		// TODO do something with callbackQuery.getData()
		// String data = callbackQuery.getData();
		// TODO validate data value to be a valid value
		// data = "show/<rssFeedId>
		
		// Reply Answercallback query
		answerCallbackQuery(callbackQuery);
		
		Optional<Feed> feed = feedService.getFeed("61746f2c9095ec51f15994e3");
		

		// Reply final message - View RSS feed
		
		// Update the text message to display the RSS feed title
		EditMessageText editMessageText = EditMessageText.builder()
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.messageId(callbackQuery.getMessage().getMessageId())
				.text(feed.get().getTitle())
				.build();
		
		execute(callbackQuery.getMessage().getChatId(), editMessageText);
		
		// Update the reply markup keyboard to show the RSS feed options
		EditMessageReplyMarkup editMessage = EditMessageReplyMarkup.builder()
				.messageId(callbackQuery.getMessage().getMessageId())
				.chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
				.replyMarkup(createRssFeedItemReplyMarkup(feed.get()))
				.build();
				
		execute(callbackQuery.getMessage().getChatId(), editMessage);
	

		System.out.println(callbackQuery);
	}

	private void answerCallbackQuery(CallbackQuery callbackQuery) {
		
		// Prepare answer callback query
		AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
		answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
		
		// Send answer to Telegram API
		execute(callbackQuery.getMessage().getChatId(), answerCallbackQuery);

	}

	@Override
	public String getBotUsername() {
		return config.getUsername();
	}

	@Override
	public String getBotToken() {
		return config.getToken();
	}

	@Override
	public long creatorId() {
		// TODO Auto-generated method stub
		return 0; 
	}
	
	private <T extends Serializable, Method extends BotApiMethod<T>> void execute(Long chatId, Method method) {
		try {
			this.execute(method);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			silent.send("Oops! something wrong happened!", chatId);
		}
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
				.callbackData("show_all/")
				.build();
		
		row.add(keyboardButton);
		markupInlineBuilder.keyboardRow(row);
		
		return markupInlineBuilder.build();
	}

}
