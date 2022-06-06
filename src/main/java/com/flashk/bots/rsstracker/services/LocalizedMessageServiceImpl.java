package com.flashk.bots.rsstracker.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class LocalizedMessageServiceImpl implements LocalizedMessageService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getText(String messageProperty, String languageCode) {
		 return messageSource.getMessage(messageProperty, 
					null, 
					Locale.forLanguageTag(languageCode));
	}

	@Override
	public String getText(String messageProperty, String languageCode, String... messageParameters) {
		return messageSource.getMessage(messageProperty, 
					messageParameters, 
					Locale.forLanguageTag(languageCode));
	}

}
