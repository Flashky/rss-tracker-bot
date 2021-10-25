package com.flashk.bots.rsstracker.core;

import java.io.Serializable;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbilityCallbackBot extends AbilityBot {

	protected AbilityCallbackBot(String botToken, String botUsername) {
		super(botToken, botUsername);
	}

	public <T extends Serializable, Method extends BotApiMethod<T>> void execute(Long chatId, Method method) {
		try {
			this.execute(method);
		} catch (TelegramApiException e) {
			e.printStackTrace();
			silent.send("Oops! something wrong happened!", chatId);
		}
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		
		// Handle abilities
		super.onUpdateReceived(update);
		
		// Handle callback queries
		if(update.getCallbackQuery() != null) {
			onCallbackQuery(update.getCallbackQuery());
			answerCallbackQuery(update.getCallbackQuery());
		}
	
    }
	
	private void answerCallbackQuery(CallbackQuery callbackQuery) {
		
		// Prepare answer callback query
		AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
		answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
		
		// Send answer to Telegram API
		execute(callbackQuery.getMessage().getChatId(), answerCallbackQuery);

	}
	
	abstract void onCallbackQuery(CallbackQuery callbackQuery);

	

	

}
