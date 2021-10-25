package com.flashk.bots.rsstracker.core.events;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import lombok.Getter;

@Getter
public class CallbackQueryEvent {

	private String action;
	private CallbackQuery callbackQuery;
	private String rssFeedId;
	
	public CallbackQueryEvent(String action, CallbackQuery callbackQuery) {
		this.action = action;
		this.callbackQuery = callbackQuery;
	}
	
	public CallbackQueryEvent(String action, CallbackQuery callbackQuery, String rssFeedId) {
		this.action = action;
		this.callbackQuery = callbackQuery;
		this.rssFeedId = rssFeedId;
	}
}
