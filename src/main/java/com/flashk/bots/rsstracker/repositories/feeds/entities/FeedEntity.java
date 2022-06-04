package com.flashk.bots.rsstracker.repositories.feeds.entities;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "rssFeeds", collation = "{ 'locale' :  'es' }")
public class FeedEntity {

	@Id
	private String id;
	private TelegramEntity telegram;
	private String title;
	private String description;
	private String link;
	
	private String sourceLink;
	private Boolean isEnabled;
	
	// Auditing fields
	
	@CreatedDate
	@JsonFormat(timezone = "GMT+02:00")
	private Date createdDate;
	
	@LastModifiedDate
	@JsonFormat(timezone = "GMT+02:00")
	private Date lastModifiedDate;
	

	
}
