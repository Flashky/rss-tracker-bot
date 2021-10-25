package com.flashk.bots.rsstracker.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryEventPublisher {

	// Examples of accepted patterns: "word", "word/another_word" 
	private final static String CALLBACK_DATA_PATTERN = "(\\w*)[\\/]?(\\w*)";
	private final Pattern pattern = Pattern.compile(CALLBACK_DATA_PATTERN);
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
    public void publishCallbackQueryEvent(final CallbackQuery callbackQuery) {
        
		Matcher matcher = pattern.matcher(callbackQuery.getData());
		
		if(matcher.matches()) {

			String action = matcher.group(1);
			String id = matcher.group(2);
			
			CallbackQueryEvent event = new CallbackQueryEvent(action, callbackQuery, id);

	    	System.out.println("Publishing custom event. ");  	
	        applicationEventPublisher.publishEvent(event);
	        
		}

    }
}
