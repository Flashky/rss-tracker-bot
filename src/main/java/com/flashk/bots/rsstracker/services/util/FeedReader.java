package com.flashk.bots.rsstracker.services.util;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.flashk.bots.rsstracker.services.exceptions.InvalidRssException;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Component
public class FeedReader {
	
	public SyndFeed read(String feedUrl) {
		
		try {
			
			URL url = new URL(feedUrl);
			SyndFeedInput input = new SyndFeedInput();	
			return input.build(new XmlReader(url));
		
		} catch (FeedException | IllegalArgumentException | IOException e) {
			throw new InvalidRssException(e);
		}
		
	}
}
