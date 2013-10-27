package com.example.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This code encapsulates RSS item data.
 * Our application needs title and link data.
 */
public class RssItem {
	
	// item title
	private String title;
	// item link
	private String link;
	private String description;
	
	private String pabDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(String desc)
	{
		Document doc=Jsoup.parse(desc);
		this.description = doc.text();
		//System.out.println(description);
	}
	
	public String getPabDate() {
		return pabDate;
	}

	public void setPabDate(String pabDate) {
		this.pabDate = pabDate;
	}
	

	@Override
	public String toString() {
		return title;
	}
	
}


