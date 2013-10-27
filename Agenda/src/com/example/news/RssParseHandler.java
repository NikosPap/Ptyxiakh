package com.example.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * SAX tag handler, responsibly for parsing the news from UOA site
 */

public class RssParseHandler extends DefaultHandler 
{   
    private String        urlString; 
    private RssFeed       rssFeed; 
    private StringBuilder text; 
    private RssItem          item; 
    private boolean       imgStatus;
    
    public RssParseHandler(String url) 
    { 
        this.urlString = url; 
        this.text = new StringBuilder(); 
    }
    
    public void parse() 
    { 
        InputStream urlInputStream = null; 
        SAXParserFactory spf = null; 
        SAXParser sp = null; 
        
        try 
        { 
            URL url = new URL(this.urlString); 
            _setProxy(); // Set the proxy if needed 
            urlInputStream = url.openConnection().getInputStream();            
            spf = SAXParserFactory.newInstance(); 
            if (spf != null) 
            { 
                sp = spf.newSAXParser(); 
                sp.parse(urlInputStream, this); 
            } 
        }
        /* 
         * Exceptions need to be handled 
         * MalformedURLException 
         * ParserConfigurationException 
         * IOException 
         * SAXException 
         */ 
        
        catch (Exception e) 
        { 
            System.out.println("Exception: " + e); 
            e.printStackTrace(); 
        } 
        finally 
        { 
            try 
            { 
                if (urlInputStream != null) urlInputStream.close(); 
            } 
            catch (Exception e) {} 
        } 
    }
    public RssFeed getFeed() 
    { 
        return (this.rssFeed); 
    } 
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) 
    { 
        if (qName.equalsIgnoreCase("channel")) 
            this.rssFeed = new RssFeed(); 
        else if (qName.equalsIgnoreCase("item") && (this.rssFeed != null)) 
        { 
            this.item = new RssItem(); 
            this.rssFeed.addItem(this.item); 
        } 
        else if (qName.equalsIgnoreCase("image") && (this.rssFeed != null)) 
            this.imgStatus = true; 
    } 
    
    public void endElement(String uri, String localName, String qName) 
    { 
        if (this.rssFeed == null) 
            return; 
        
        if (qName.equalsIgnoreCase("item")) 
            this.item = null; 
        
        else if (qName.equalsIgnoreCase("image")) 
            this.imgStatus = false; 
        
        else if (qName.equalsIgnoreCase("title")) 
        { 
            if (this.item != null) 
            	this.item.setTitle(this.text.toString().trim()); 
            else if (this.imgStatus) 
            	this.rssFeed.imageTitle = this.text.toString().trim(); 
            else 
            	this.rssFeed.title = this.text.toString().trim(); 
        }        
        
        else if (qName.equalsIgnoreCase("link")) 
        { 
            if (this.item != null) 
            	this.item.setLink(this.text.toString().trim()); 
            else if (this.imgStatus) 
            	this.rssFeed.imageLink = this.text.toString().trim(); 
            else 
            	this.rssFeed.link = this.text.toString().trim(); 
        }        
        
        else if (qName.equalsIgnoreCase("description")) 
        { 
            if (this.item != null) 
            	this.item.setDescription(this.text.toString().trim()); 
            else 
            	this.rssFeed.description = this.text.toString().trim(); 
        } 
        
        else if (qName.equalsIgnoreCase("url") && this.imgStatus) 
            this.rssFeed.imageUrl = this.text.toString().trim(); 
        
        else if (qName.equalsIgnoreCase("language")) 
            this.rssFeed.language = this.text.toString().trim(); 
        
        else if (qName.equalsIgnoreCase("generator")) 
            this.rssFeed.generator = this.text.toString().trim(); 
        
        else if (qName.equalsIgnoreCase("copyright")) 
            this.rssFeed.copyright = this.text.toString().trim(); 
        
        else if (qName.equalsIgnoreCase("pubDate") && (this.item != null)) 
            this.item.setPabDate(this.text.toString().trim()); 
        
        else if (qName.equalsIgnoreCase("category") && (this.item != null)) 
            this.rssFeed.addItem(this.text.toString().trim(), this.item); 
        
        this.text.setLength(0); 
    } 
    
    public void characters(char[] ch, int start, int length) 
    { 
        this.text.append(ch, start, length); 
    } 
    
    public static void _setProxy() throws IOException 
    { 
        Properties sysProperties = System.getProperties(); 
        sysProperties.put("proxyHost", "<Proxy IP Address>"); 
        sysProperties.put("proxyPort", "<Proxy Port Number>"); 
        System.setProperties(sysProperties); 
    }
}