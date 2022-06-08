package 	com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.controllers.constants.CommonConstants;
import com.flashk.bots.rsstracker.repositories.utils.PageBuilder;
import com.flashk.bots.rsstracker.services.LocalizedMessageServiceImpl;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class ItemsReplyMarkupMapper {

    @Value("${bot.feeds.page-size}")
    private int pageSize;
    
    @Autowired
    private LocalizedMessageServiceImpl messageService;
    
	@Autowired
	private UrlBuilder urlBuilder;
	
	public InlineKeyboardMarkup map(User user, Feed feed, int itemPage, int size) {
		
		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Add items and pagination
		if(!feed.getItems().isEmpty()) {
			Page<Item> itemsPage = new PageBuilder<>(feed.getItems()).of(itemPage, size).build();
		
			addItems(replyMarkup, itemsPage);
			addPaginationButtons(replyMarkup, user, feed, itemsPage);
		}
		
		// Add back button
		addBackButton(replyMarkup, user);
		
		return replyMarkup;
	}

	private void addItems(InlineKeyboardMarkup replyMarkup, Page<Item> itemData) {
		
		for(Item item : itemData.getContent()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(item.getTitle())
											.url(item.getLink());
			
			replyMarkup.addRow(button);
			
		}
	}
	
	private void addPaginationButtons(InlineKeyboardMarkup replyMarkup, User user, Feed feed, Page<Item> items) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(items.hasPrevious()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(messageService.getText(MessageConstants.LABEL_PREVIOUS_PAGE, user.languageCode()))
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), items.previousPageable().getPageNumber(), items.getSize()));
			
			paginationButtons.add(button);
			
		}
		
		
		if(items.hasNext()) {
			
			InlineKeyboardButton button = new InlineKeyboardButton(messageService.getText(MessageConstants.LABEL_NEXT_PAGE, user.languageCode()))
					.callbackData(urlBuilder.getFeedItemsUri(feed.getId(), items.nextPageable().getPageNumber(), items.getSize()));		
			
			paginationButtons.add(button);
			
		}
		
		
		if(!paginationButtons.isEmpty()) {
			InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
			paginationButtons.toArray(paginationButtonsArray);
		
			replyMarkup.addRow(paginationButtonsArray);
		}
		
	}
	
	private void addBackButton(InlineKeyboardMarkup replyMarkup, User user) {
		
		InlineKeyboardButton backButton = new InlineKeyboardButton(messageService.getText(MessageConstants.LABEL_BACK_FEED_LIST, user.languageCode()))
				.callbackData(urlBuilder.getFeedsUri(CommonConstants.FIRST_PAGE, pageSize));
		
		replyMarkup.addRow(backButton);
	}
}
