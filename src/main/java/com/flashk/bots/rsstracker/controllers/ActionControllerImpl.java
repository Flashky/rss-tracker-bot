package com.flashk.bots.rsstracker.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage.SendMessageBuilder;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.flashk.bots.rsstracker.factories.InlineKeyboardMarkupFactory;
import com.flashk.bots.rsstracker.services.FeedService;
import com.flashk.bots.rsstracker.services.model.Feed;

@Component
public class ActionControllerImpl implements ActionController {

	private Logger logger = LoggerFactory.getLogger(ActionControllerImpl.class);
	
	@Autowired
	private FeedService feedService;
	
	@Autowired
	private InlineKeyboardMarkupFactory replyMarkupFactory;
	
	@Override
	public void showFeeds(MessageContext ctx) {

		// Obtain feeds
		List<Feed> feeds = feedService.listFeeds();
		
		// Prepare and send response
		SendMessage message = prepareShowRssFeedsResponse(ctx, feeds);
		ctx.bot().silent().execute(message);

	}

	@Override
	public void addFeed(BaseAbilityBot bot, Update update) {
		
		Feed feed = new Feed();
		feed.setUrl(update.getMessage().getText());
		
		// Create the feed
		try {
			Feed createdFeed = feedService.createFeed(feed);
			bot.silent().send("New RSS feed added: " + createdFeed.getTitle(), update.getMessage().getChatId());
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			bot.silent().send("That RSS feed is not valid", update.getMessage().getChatId());
		}
	}
	
	private SendMessage prepareShowRssFeedsResponse(MessageContext ctx, List<Feed> feeds) {
		
		SendMessageBuilder sendMessageBuilder = SendMessage.builder()
				.chatId(String.valueOf(ctx.chatId()));
		
		if(feeds.isEmpty()) {
			sendMessageBuilder.text("You don't have any feeds.");
		} else {
			sendMessageBuilder.text("Your RSS feeds:")
				.replyMarkup(replyMarkupFactory.createFeedListReplyMarkup(feeds));
		}

		return sendMessageBuilder.build();
	}

	public void requestCallbackQuery(MessageContext ctx) {
		
		CallbackQuery callbackQuery = ctx.update().getCallbackQuery();
		
		logger.debug("Preparing callback query request: " + callbackQuery.getData());
    	
		String[] parameters = callbackQuery.getData().split(" ");
		String verb = parameters[0];
		String endpoint = parameters[1];
		
		RestTemplate restTemplate = new RestTemplate();
		
		String url = "http://localhost:8080" + endpoint;
		HttpEntity<CallbackQuery> body = new HttpEntity<CallbackQuery>(callbackQuery);

		restTemplate.exchange(url, HttpMethod.resolve(verb), body, String.class);
	}
	
	
	@Override
	public void answerCallbackQuery(MessageContext ctx) {

		AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
				.callbackQueryId(ctx.update().getCallbackQuery().getId())
   			  	.build();

		ctx.bot().silent().execute(answerCallbackQuery);
		
	}
	
}