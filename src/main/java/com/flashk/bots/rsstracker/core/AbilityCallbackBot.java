package com.flashk.bots.rsstracker.core;

import java.io.Serializable;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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
	
}
