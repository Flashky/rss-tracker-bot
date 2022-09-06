package com.flashk.bots.rsstracker.controllers.mappers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.controllers.constants.PathConstants;
import com.flashk.bots.rsstracker.services.LocalizedMessageService;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

@Component
public class InlineKeyboardButtonFactory {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
    @Autowired
    private LocalizedMessageService messageService;
    
    // Feeds - /feeds
    
    public InlineKeyboardButton createFirstFeedPageButton(Feed feed) {
       	
    	String text = feed.getTitle();
    	String callbackData = callbackDataFeedPage(CommonConstants.FIRST_PAGE);
    	
    	return new InlineKeyboardButton(text).callbackData(callbackData);
    }
    
    public InlineKeyboardButton createFirstFeedPageButton(String label, User user) {
    	
    	String text = messageService.getText(label, user.languageCode());
    	String callbackData = callbackDataFeedPage(CommonConstants.FIRST_PAGE);
    	
    	return new InlineKeyboardButton(text).callbackData(callbackData);
    }

    public Optional<InlineKeyboardButton> createPreviousFeedPageButton(User user, Page<Feed> feedPage) {
     	
    	if(!feedPage.hasPrevious()) {
    		return Optional.empty();
    	}
    	
    	String text = messageService.getText(MessageConstants.LABEL_PREVIOUS_PAGE, user.languageCode());
    	String callbackData = callbackDataFeedPage(feedPage.previousPageable().getPageNumber());
     	
    	return Optional.of(new InlineKeyboardButton(text).callbackData(callbackData));
    }
    
    public Optional<InlineKeyboardButton> createNextFeedPageButton(User user, Page<Feed> feedPage) {
     	
    	if(!feedPage.hasNext()) {
    		return Optional.empty();
    	}
    	
    	String text = messageService.getText(MessageConstants.LABEL_NEXT_PAGE, user.languageCode());
    	String callbackData = callbackDataFeedPage(feedPage.nextPageable().getPageNumber());
     	
    	return Optional.of(new InlineKeyboardButton(text).callbackData(callbackData));
    }
    
    public InlineKeyboardButton createOpenDialogDeleteFeedButton(User user, Feed feed) {
    
    	String text = messageService.getText(MessageConstants.LABEL_BUTTON_DELETE_FEED, user.languageCode());
    	String callbackData = UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ACTION_DIALOG_DELETE)
    			.buildAndExpand(feed.getId())
    			.toString();
    	
    	return new InlineKeyboardButton(text).callbackData(callbackData);
    }
    
    // Feed Items- /feeds/{feedId}/items
    
    public InlineKeyboardButton createItemUrlButton(Item item) {
    	return new InlineKeyboardButton(item.getTitle()).url(item.getLink());
    }
    
    public InlineKeyboardButton createFirstItemPageButton(Feed feed) {
    	
    	String text = feed.getTitle();
    	String callbackData = callbackDataItemPage(feed.getId(), CommonConstants.FIRST_PAGE);
    	
    	return new InlineKeyboardButton(text).callbackData(callbackData);
    }
   
    public InlineKeyboardButton createFirstItemPageButton(String label, User user, Feed feed) {
    	
    	String text = messageService.getText(label, user.languageCode());
    	String callbackData = callbackDataItemPage(feed.getId(), CommonConstants.FIRST_PAGE);
    	
    	return new InlineKeyboardButton(text).callbackData(callbackData);
    }
    
    public Optional<InlineKeyboardButton> createPreviousItemPageButton(User user, Feed feed, Page<Item> itemPage) {
    	
    	if(!itemPage.hasPrevious()) {
    		return Optional.empty();
    	}
    	
    	String text = messageService.getText(MessageConstants.LABEL_PREVIOUS_PAGE, user.languageCode());
    	String callbackData = callbackDataItemPage(feed.getId(), itemPage.previousPageable().getPageNumber());
    	
    	return Optional.of(new InlineKeyboardButton(text).callbackData(callbackData));
    }
    
    public Optional<InlineKeyboardButton> createNextItemPageButton(User user, Feed feed, Page<Item> itemPage) {
    	
    	if(!itemPage.hasNext()) {
    		return Optional.empty();
    	}
    	
    	String text = messageService.getText(MessageConstants.LABEL_NEXT_PAGE, user.languageCode());
    	String callbackData = callbackDataItemPage(feed.getId(), itemPage.nextPageable().getPageNumber());
    	
    	return Optional.of(new InlineKeyboardButton(text).callbackData(callbackData));
    }
    
	public InlineKeyboardButton createDeleteFeedButton(User user, Feed feed) {
    	
		String text = messageService.getText(MessageConstants.LABEL_BUTTON_YES, user.languageCode());
		
    	String deleteUri = UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ACTION_DELETE)
				.buildAndExpand(feed.getId())
				.toString();
		
    	return new InlineKeyboardButton(text).callbackData(deleteUri);
    }
    
    // Private auxiliar methods
    
	private String callbackDataFeedPage(int page) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEEDS)
				.buildAndExpand(page, pageSize)
				.toString();
	}
	
	private String callbackDataItemPage(String feedId, int page) {
		return UriComponentsBuilder.fromPath(PathConstants.URI_FEED_ITEMS)
				.buildAndExpand(feedId, page, pageSize)
				.toString();
	}
}
