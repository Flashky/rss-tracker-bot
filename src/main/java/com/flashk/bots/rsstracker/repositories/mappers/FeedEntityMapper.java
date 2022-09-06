package com.flashk.bots.rsstracker.repositories.mappers;

import org.jsoup.Jsoup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.flashk.bots.rsstracker.repositories.entities.FeedEntity;
import com.flashk.bots.rsstracker.repositories.entities.ItemEntity;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

@Mapper(componentModel = "spring")
public abstract class FeedEntityMapper {

	@Mapping(source = "userId", target = "telegram.userId")
	@Mapping(source = "chatId", target = "telegram.chatId")
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "syndFeed.title", target = "title", qualifiedByName = "cleanHtml")
	@Mapping(target = "isEnabled", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(source = "syndFeed.entries", target = "notifiedItems")
	public abstract FeedEntity map(Long userId, Long chatId, String sourceLink, SyndFeed syndFeed);
	
	@Mapping(source = "title", target = "title", qualifiedByName = "cleanHtml")
	public abstract ItemEntity map(SyndEntry syndEntry);
	
	@Named("cleanHtml")
	public String cleanHtml(String html) {
		return (html == null) ? null : Jsoup.parse(html).text();
	}
}
