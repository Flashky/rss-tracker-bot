package com.flashk.bots.rsstracker.services.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feed {
	private String id;
	private String sourceLink;
	private String title;
	private String link;
	private List<Item> items;
}
