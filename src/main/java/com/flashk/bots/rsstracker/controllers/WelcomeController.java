package com.flashk.bots.rsstracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

@BotController
public class WelcomeController implements TelegramMvcController {

    @Autowired
    private LocalizedMessageService messageService;
    
    @Value("${bot.token}")
    private String token;
    
	@Override
	public String getToken() {
		return token;
	}
	
	@MessageRequest("/help") 
	public SendMessage help(User user, Chat chat) {
		return sendHelpMessage(user, chat);
	}
	
	@MessageRequest("/start") 
	public SendMessage start(User user, Chat chat) {
		return sendHelpMessage(user, chat);
	}

	private SendMessage sendHelpMessage(User user, Chat chat) {
		return new SendMessage(chat.id(), messageService.getText(MessageConstants.HELP_MESSAGE, user.languageCode()))
				.parseMode(ParseMode.Markdown);
	}
}
