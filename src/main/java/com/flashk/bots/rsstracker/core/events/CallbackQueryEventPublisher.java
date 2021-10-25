package com.flashk.bots.rsstracker.core.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryEventPublisher {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
    public void publishCallbackQueryEvent(final CallbackQuery callbackQuery) {
        
		String regex = "(show|edit|delete)[\\/]([a-zA-Z0-9]*)|(show_list)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(callbackQuery.getData());
		
		if(matcher.matches()) {

			String action;
			String id = "";
			CallbackQueryEvent event;
			if(matcher.group(3) != null) {
				action = matcher.group(3);
				event = new CallbackQueryEvent(action, callbackQuery);
			} else {
				action = matcher.group(1);
				id = matcher.group(2);
				event = new CallbackQueryEvent(action, callbackQuery, id);
			}	

	    	System.out.println("Publishing custom event. ");  	
	        applicationEventPublisher.publishEvent(event);
	        
		}

    }
}
