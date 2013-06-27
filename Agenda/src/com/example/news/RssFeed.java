package com.example.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RssFeed 
    { 
        public  String title; 
        public  String description; 
        public  String link; 
        public  String language; 
        public  String generator; 
        public  String copyright; 
        public  String imageUrl; 
        public  String imageTitle; 
        public  String imageLink; 
        
        private ArrayList <RssItem> items; 
        private HashMap <String, ArrayList <RssItem>> category;
        
        public List<RssItem> getItems()
        {
        	return items;
        }
        
        public void addItem(RssItem item) 
        { 
            if (this.items == null) 
                this.items = new ArrayList<RssItem>(); 
            this.items.add(item); 
        } 
        
        public void addItem(String category, RssItem item) 
        { 
            if (this.category == null) 
                this.category = new HashMap<String, ArrayList<RssItem>>(); 
            if (!this.category.containsKey(category)) 
                this.category.put(category, new ArrayList<RssItem>()); 
            this.category.get(category).add(item); 
        } 
    }