package com.flashk.bots.rsstracker.factories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.flashk.bots.rsstracker.services.model.Feed;

@Component
public class InlineKeyboardMarkupFactoryImpl implements InlineKeyboardMarkupFactory {

	@Autowired
	private InlineKeyboardButtonFactory buttonFactory;


	@Override
	public InlineKeyboardMarkup createShowFeedSettingsReplyMarkup(Feed feed) {
		
		InlineKeyboardMarkupBuilder markupInlineBuilder = InlineKeyboardMarkup.builder();

		List<InlineKeyboardButton> row = new ArrayList<>();
		
		// View URL button
		InlineKeyboardButton keyboardButton = InlineKeyboardButton.builder()
				.text("View RSS feed")
				.url(feed.getUrl())
				.build();
		
		row.add(keyboardButton);
		
		// Delete button		
		row.add(buttonFactory.createDeleteFeedConfirmationButton("Delete RSS Feed", feed.getId()));
		markupInlineBuilder.keyboardRow(row);
		
		// Back to main menu
		row = new ArrayList<>();
		row.add(buttonFactory.createShowFeedListButton("<< Back to RSS Feed List"));
		
		markupInlineBuilder.keyboardRow(row);
		
		return markupInlineBuilder.build();
	}
	
	@Override
	public InlineKeyboardMarkup createDeleteFeedConfirmationReplyMarkup(String rssFeedId) {

        List<InlineKeyboardButton> optionsRow = new ArrayList<>();
        optionsRow.add(buttonFactory.createDeleteFeedButton("Yes", rssFeedId));
        optionsRow.add(buttonFactory.createShowFeedSettingsButton("No", rssFeedId));
        
        return InlineKeyboardMarkup.builder()
        		.keyboardRow(optionsRow)
        		.build();

	}
	
	@Override
	public InlineKeyboardMarkup createFeedListReplyMarkup(List<Feed> feeds) {

		InlineKeyboardMarkupBuilder markupInlineBuilder = InlineKeyboardMarkup.builder();
		
		List<InlineKeyboardButton> feedRow;
		
		for(Feed feed : feeds) {
			
			// Add the button to a new row
			feedRow = new ArrayList<>();
			feedRow.add(buttonFactory.createShowFeedSettingsButton(feed.getTitle(), feed.getId()));

			markupInlineBuilder.keyboardRow(feedRow);
		}
		
		feedRow = new ArrayList<>();
		feedRow.add(buttonFactory.createShowFeedListPageButton("<<", 1));
		feedRow.add(buttonFactory.createShowFeedListPageButton(">>", 3));
		
		markupInlineBuilder.keyboardRow(feedRow);
	
		return markupInlineBuilder.build();
	}

	@Override
	public InlineKeyboardMarkup createSingleButtonReplyMarkup(InlineKeyboardButton inlineKeyboardButton) {
		
		List<InlineKeyboardButton> optionsRow = new ArrayList<>();
        optionsRow.add(inlineKeyboardButton);
        
        return InlineKeyboardMarkup.builder()
        		.keyboardRow(optionsRow)
        		.build();
    
	}
	
}
