import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class Scrappy {
	public static void main(String[] args) throws TwitterException, IOException{
		
//		ConfigurationBuilder cb = new ConfigurationBuilder();
//		cb.setDebugEnabled(true)
//		  .setOAuthConsumerKey("*************************")
//		  .setOAuthConsumerSecret("**************************************************")
//		  .setOAuthAccessToken("*************************-*************************")
//		  .setOAuthAccessTokenSecret("**************************************************");

	    Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer("*************************", "**************************************************");
	    twitter.setOAuthAccessToken(new AccessToken("*************************-*************************","**************************************************"));
	    
	    ArrayList<String> links = new ArrayList<String>();
	    
	    Query query = new Query("from:myanimelist -filter:retweets -filter:video since:2018-12-17");
	    QueryResult result = twitter.search(query);
	    for (Status status : result.getTweets()) {
	    	for(URLEntity urle : status.getURLEntities()) {
	    		//System.out.println(urle.toString());
	    		links.add(urle.getExpandedURL());
	    	}
	        String fmt = "@" + status.getUser().getScreenName() + " - " + status.getText();
	        System.out.println(fmt);
	    }
	    
	    System.out.println(links.get(0));
	    
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;

	    try {
	        url = new URL(links.get(0));
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
//	            System.out.println(line);
	        }
	    } catch (MalformedURLException mue) {
	        System.out.println("MalformedURLException");
	    } catch (IOException ioe) {
	    	System.out.println("IOException");
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	}
}
