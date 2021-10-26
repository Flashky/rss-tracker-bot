package com.flashk.bots.rsstracker.controllers;

import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Manages bot actions.
 * @author Flashk
 *
 */
public interface ActionController {

	void showFeeds(MessageContext ctx);
	void addFeed(BaseAbilityBot bot, Update update);
}
