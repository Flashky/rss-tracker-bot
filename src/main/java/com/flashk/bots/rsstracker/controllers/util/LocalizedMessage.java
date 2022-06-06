package com.flashk.bots.rsstracker.controllers.util;

import java.util.Locale;

import org.springframework.context.MessageSource;

public enum LocalizedMessage {

	RSS_FEED_ADD("rss.feed.add"),
	RSS_FEED_ADDED("rss.feed.added"),
	RSS_FEED_LIST_TITLE("rss.feed.list.title"),
	RSS_FEED_LIST_EMPTY("rss.feed.list.empty");
	
	private String property;	
    private static MessageSource messageSource;

	private LocalizedMessage(String property) {
		this.property = property;
	}
	
	
	public static void setMessageSource(MessageSource messageSourceOther) {
		messageSource = messageSourceOther;
	}

	
	public String getText(String languageCode) {
		 return messageSource.getMessage(property, 
										null, 
										Locale.forLanguageTag(languageCode));
	}
	
	public String getText(String languageCode, String... arguments) {
		return messageSource.getMessage(property, 
										arguments, 
										Locale.forLanguageTag(languageCode));
	}
	
}
