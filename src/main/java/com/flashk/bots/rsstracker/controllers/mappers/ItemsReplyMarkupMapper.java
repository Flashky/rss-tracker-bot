package 	com.flashk.bots.rsstracker.controllers.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.constants.MessageConstants;
import com.flashk.bots.rsstracker.repositories.utils.PageBuilder;
import com.flashk.bots.rsstracker.services.model.Feed;
import com.flashk.bots.rsstracker.services.model.Item;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

@Component
public class ItemsReplyMarkupMapper {

	@Autowired 
	private InlineKeyboardButtonFactory buttonFactory;
	
    @Value("${bot.feeds.page-size}")
    private int pageSize;
	
	public InlineKeyboardMarkup map(User user, Feed feed, int itemPage, int size) {
		
		InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
		
		// Add items and pagination
		if(!feed.getItems().isEmpty()) {
			Page<Item> itemsPage = new PageBuilder<>(feed.getItems()).of(itemPage, size).build();
		
			addItems(replyMarkup, itemsPage);
			addPaginationButtons(replyMarkup, user, feed, itemsPage);
		}
		
		// Add back button
		addOptionButtons(replyMarkup, user, feed);
		
		return replyMarkup;
	}

	private void addItems(InlineKeyboardMarkup replyMarkup, Page<Item> itemData) {
		
		for(Item item : itemData.getContent()) {
			replyMarkup.addRow(new InlineKeyboardButton(item.getTitle()).url(item.getLink()));
		}
	}
	
	private void addPaginationButtons(InlineKeyboardMarkup replyMarkup, User user, Feed feed, Page<Item> items) {
		
		List<InlineKeyboardButton> paginationButtons = new ArrayList<>();
		
		if(items.hasPrevious()) {
			paginationButtons.add(buttonFactory.createPreviousItemPageButton(user, feed, items));
		}
		
		
		if(items.hasNext()) {
			paginationButtons.add(buttonFactory.createNextItemPageButton(user, feed, items));
		}
		
		
		if(!paginationButtons.isEmpty()) {
			InlineKeyboardButton[] paginationButtonsArray = new InlineKeyboardButton[paginationButtons.size()];
			paginationButtons.toArray(paginationButtonsArray);
		
			replyMarkup.addRow(paginationButtonsArray);
		}
		
	}
	
	private void addOptionButtons(InlineKeyboardMarkup replyMarkup, User user, Feed feed) {

    	InlineKeyboardButton[] optionButtons = {
    			buttonFactory.createFirstFeedPageButton(MessageConstants.LABEL_BACK_FEED_LIST, user),
    			buttonFactory.createOpenDialogDeleteFeedButton(user, feed)
    	};
    	
		replyMarkup.addRow(optionButtons);
	}
}
