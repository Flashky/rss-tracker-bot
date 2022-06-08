package com.flashk.bots.rsstracker.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.vdurmont.emoji.EmojiParser;

@Service
public class LocalizedMessageServiceImpl implements LocalizedMessageService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public String getText(String messageProperty, String languageCode) {	
		
		String text = messageSource.getMessage(messageProperty, 
						null, 
						Locale.forLanguageTag(languageCode));
		
		return EmojiParser.parseToUnicode(text); 
	}

	@Override
	public String getText(String messageProperty, String languageCode, Object... messageParameters) {
		
		String text = messageSource.getMessage(messageProperty, 
					messageParameters, 
					Locale.forLanguageTag(languageCode));
		
		return EmojiParser.parseToUnicode(text); 
	}

}
