package feedsucker.feedreader.rome;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import feedsucker.data.entity.Feed;
import feedsucker.feedreader.FeedEntry;
import feedsucker.feedreader.IFeedReader;
import feedsucker.util.HttpUtils;

/**
 * Feed reader for Atom and RSS feed based on Rome framework 
 */
public class RomeFeedReader implements IFeedReader {

    private String userAgent;
    private URLConnection urlConn;
    
    public RomeFeedReader() {}
    public RomeFeedReader(String userAgent) { this.userAgent = userAgent; }
    
    @Override
    public List<FeedEntry> getFeedEntries(String feedUrl)        
            throws FeedException, IOException {        
        SyndFeed feed = createFeed(feedUrl);
        List entries = feed.getEntries();
        List<FeedEntry> result = new ArrayList<>(entries.size());
        for (Object o : entries) {
            SyndEntry e = (SyndEntry)o;
            e.setLink(HttpUtils.cleanFeedUrl(e.getLink()));
            result.add(syndEntryToFeedEntry(e));
        }        
        closeUrlConnection();
        return result;
    } 
    
    // setup url connection, create and return readable feed for url
    public SyndFeed createFeed(String feedUrl) throws IOException, FeedException {        
        SyndFeedInput input = new SyndFeedInput();       
        SyndFeed feed = null; urlConn = null;
        if (userAgent == null) { // use default user agend
            feed = input.build(new XmlReader(new URL(feedUrl)));
        } 
        else { // setup user agend explicitly
            urlConn = new URL(feedUrl).openConnection();            
            urlConn.setRequestProperty("User-Agent", userAgent);        
            feed = input.build(new XmlReader(urlConn));
        }        
        return feed;
    }

    private void closeUrlConnection() {
        if (urlConn == null) return;
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection)urlConn;
            httpConn.disconnect();                  
        }
        urlConn = null;
    }    
    
    private static FeedEntry syndEntryToFeedEntry(SyndEntry e) {
        FeedEntry entry = new FeedEntry();
        entry.setUrl(e.getLink());
        entry.setDate(e.getPublishedDate());
        entry.setTitle(e.getTitle());
        entry.setDescription(e.getDescription().getValue());
        entry.setAuthor(e.getAuthor());
        return entry;
    }
    
    // parse feed url, read data and write it to entity file
    public void readFeedData(Feed efeed) throws FeedException, IOException {
        SyndFeed feed = createFeed(efeed.getUrl());        
        efeed.setTitle(feed.getTitle());
        efeed.setDescription(feed.getDescription());        
    }

    }
