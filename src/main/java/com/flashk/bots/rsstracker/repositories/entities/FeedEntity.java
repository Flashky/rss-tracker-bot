package com.flashk.bots.rsstracker.repositories.entities;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "rssFeeds", collation = "{ 'locale' :  'es' }")
public class FeedEntity {

	/**
	 * Unique feed identifier.
	 */
	@Id
	private String id;
	
	/**
	 * Telegram feed owner.
	 */
	private TelegramEntity telegram;
	
	/** 
	 * The base link where the feed is located.
	 */
	private String sourceLink;
	
	/**
	 * RSS feed title field
	 */
	private String title;
	
	/**
	 * RSS feed description field
	 */
	private String description;
	
	/**
	 * RSS feed link field.
	 */
	private String link;
	

	/**
	 * Defines if the RSS feed has Telegram notifications enabled.
	 */
	private Boolean isEnabled;
	
	// Auditing fields
	
	@CreatedDate
	@JsonFormat(timezone = "GMT+02:00")
	private Date createdDate;
	
	@LastModifiedDate
	@JsonFormat(timezone = "GMT+02:00")
	private Date lastModifiedDate;
	

	
}
