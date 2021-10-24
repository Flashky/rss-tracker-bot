package com.flashk.bots.rsstracker.core;

import java.util.ArrayList;
import java.util.List;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.flashk.bots.rsstracker.config.properties.BotConfig;

@Service
public class RssTrackerBot extends AbilityBot {

	@Autowired
	private BotConfig config;
	
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
		
		// TODO Call service and retrieve feeds
		
		System.out.println("Show the feeds!");
		
		SendMessage message = new SendMessage();
		message.setChatId(String.valueOf(ctx.chatId()));
		message.setText("Show the feeds!");
		
		
		InlineKeyboardMarkup markupInline = createRssFeedListReplyMarkup();
		message.setReplyMarkup(markupInline);
		
		execute(ctx, message);
				
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
		
		// TODO do something with callbackQuery.getData()
		// String data = callbackQuery.getData();
		// TODO validate data value to be a valid value
		// data = "show/<rssFeedId>
		
		// Reply Answercallback query
		answerCallbackQuery(callbackQuery);
		
		try {
		// Reply final message
		EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
		editMessage.setMessageId(callbackQuery.getMessage().getMessageId());
		editMessage.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
		editMessage.setReplyMarkup(createRssFeedItemReplyMarkup("id"));
		
		
		
			this.execute(editMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			silent.send("Oops! something wrong happened!",  callbackQuery.getMessage().getChatId());
		}
		// Reply final message
		System.out.println(callbackQuery);
	}

	private void answerCallbackQuery(CallbackQuery callbackQuery) {
		
		try {
			
			// Prepare answer callback query
			AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
			answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
			
			// Send answer to Telegram API
			this.execute(answerCallbackQuery);
			
		} catch (TelegramApiException e) {
			e.printStackTrace();
			silent.send("Oops! something wrong happened!", callbackQuery.getMessage().getChatId());
		}
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
	
	private void execute(MessageContext ctx, SendMessage message) {
		try {
			this.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			silent.send("Oops! something wrong happened!", ctx.chatId());
		}
	}
	
	/* TEMPORAL MOCKS */
	private InlineKeyboardMarkup createRssFeedListReplyMarkup() {
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
		List<InlineKeyboardButton> secondRowInline = new ArrayList<>();

		//add your buttons to both rows
		InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
		keyboardButton.setText("HD-Olimpo: Notification - Invasión");
		keyboardButton.setCallbackData("show/61746f2c9095ec51f15994e3");
		firstRowInline.add(keyboardButton);
		
		keyboardButton = new InlineKeyboardButton();
		keyboardButton.setText("Fundación");
		keyboardButton.setCallbackData("show_list");
		secondRowInline.add(keyboardButton);

		rowsInline.add(firstRowInline);
		rowsInline.add(secondRowInline);

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}
	
	private InlineKeyboardMarkup createRssFeedItemReplyMarkup(String rssFeedId) {
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
		List<InlineKeyboardButton> secondRowInline = new ArrayList<>();

		//add your buttons to both rows
		InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
		keyboardButton.setText("Delete");
		keyboardButton.setCallbackData("delete/"+rssFeedId);
		firstRowInline.add(keyboardButton);
		
		keyboardButton = new InlineKeyboardButton();
		keyboardButton.setText("Return");
		keyboardButton.setCallbackData("show_all/"+rssFeedId);
		secondRowInline.add(keyboardButton);

		rowsInline.add(firstRowInline);
		rowsInline.add(secondRowInline);

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}

}
