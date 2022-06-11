package com.flashk.bots.rsstracker;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing // Support for @CreatedDate and @LastModifiedDate on Documents
public class RssTrackerBotApplication {

	public static void main(String[] args) {
		
		Locale.setDefault(Locale.ENGLISH);
		SpringApplication.run(RssTrackerBotApplication.class, args);
		
	}
	
	@Bean
    public MessageSource messageSource () {
		
		ResourceBundleMessageSource  messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("language/messages");
        
        return messageSource;
        
	}

}
