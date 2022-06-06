package com.flashk.bots.rsstracker.services;

public interface LocalizedMessageService {

	String getText(String messageProperty, String languageCode);
	String getText(String messageProperty, String languageCode, String... messageParameters);
	
}
