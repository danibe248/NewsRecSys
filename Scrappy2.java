import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Scrappy2 {
	public static void main(String[] args) throws TwitterException, IOException{

		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer("4AN4RwecyFqXyAYdF6aB1wsZ7", "agrWdgv8ObHLi106znh4vCr448P4akHTd4KrOY6uTR2AebmArx");
		twitter.setOAuthAccessToken(new AccessToken("702181802253881344-GjAtl4ZkdHZbDLtdsyXVjDvmwuawxNr","lLGiQZONBEVsHfSV3zom1CWSMFbybuhSxwCPKVpjifJN9"));

		Query bbcsport = new Query("from:BBCSport -filter:retweets since:2018-12-21");
		Query techradar = new Query("from:techradar -filter:retweets since:2018-12-21");
		Query autocar = new Query("from:autocar -filter:retweets since:2018-12-21");
		Query theb1m = new Query("from:TheB1M -filter:retweets since:2018-12-21");
		Query myanimelist = new Query("from:myanimelist -filter:retweets since:2018-12-21");
		Query vizmedia = new Query("from:VIZMedia -filter:retweets since:2018-12-21");
		QueryResult bbcsport_result = twitter.search(bbcsport);
		QueryResult techradar_result = twitter.search(techradar);
		QueryResult autocar_result = twitter.search(autocar);
		QueryResult theb1m_result = twitter.search(theb1m);
		QueryResult myanimelist_result = twitter.search(myanimelist);
		QueryResult vizmedia_result = twitter.search(vizmedia);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		for (Status status : bbcsport_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/sport/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>sport</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
		for (Status status : techradar_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/tech/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>tech</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
		for (Status status : autocar_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/cars/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>cars</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
		for (Status status : theb1m_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/archi/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>archi</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
		for (Status status : myanimelist_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/anime/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>anime</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
		for (Status status : vizmedia_result.getTweets()) {
			String fmt = "@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText();
			System.out.println(fmt);
			File current = new File("/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/tweets/anime/" + status.getId() + ".xml");
			PrintWriter out = new PrintWriter(new FileOutputStream(current,false));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<oggetto>\n");
			out.println("<data>" + dateFormat.format(status.getCreatedAt()) + "</data>");
			out.println("<categoria>anime</categoria>");
			out.println("<tweet>" + status.getText() + "</tweet>");
			out.println("<articolo>null</articolo>");
			out.println("</oggetto>\n");
			out.close();
		}
	}
}