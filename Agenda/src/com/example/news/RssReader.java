package com.example.news;

import java.util.List;


/**
 * Class reads RSS data.
 */
public class RssReader {
	
	private String rssUrl;

	/**
	 * Constructor
	 * 
	 * @param rssUrl
	 */
	public RssReader(String rssUrl) {
		this.rssUrl = rssUrl;
	}

	/**
	 * Get RSS items.
	 * 
	 * @return
	 */
	public List<RssItem> getItems() throws Exception {
		RssParseHandler handler = new RssParseHandler(rssUrl);
		handler.parse();
		
		RssFeed feed = handler.getFeed();

		return feed.getItems();
		
	}
}
