import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IOException, IndexAlreadyPresentException, ParseException, CategoryNotFoundException {
		Map<Integer,String> categories = new HashMap<Integer, String>();
		categories.put(0, "anime");
		categories.put(1, "archi");
		categories.put(2, "cars");
		categories.put(3, "hiphop");
		categories.put(4, "sport");
		categories.put(5, "tech");
		categories.put(6, "weather");
		Analyzer analyzer = CustomAnalyzer.builder()
				//.addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
				.addCharFilter("patternreplace", "pattern","((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)","replacement"," ")
				.addCharFilter("patternreplace", "pattern","[^a-zA-Z ]","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		String datapath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/data/tweets";
		String ixpath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix";
		String tmpath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix_tmp";
		Indexer creator = new Indexer(datapath, ixpath, analyzer);
		IndexReader reader = creator.createIndex();
		IndexSearcher searcher = new IndexSearcher(reader);
		System.out.println(searcher.doc(0).getField("Id"));
		System.out.println(creator.getDocs().size());
		
		TermsEnum tv = reader.getTermVector(0, "Tweet").iterator();
		BytesRef term = null;
		while ((term = tv.next()) != null) {
			System.out.println(term.utf8ToString());
		}
 
		User u0 = new User(0,7,30,searcher,categories, analyzer);
		User u1 = new User(1,7,50,searcher,categories, analyzer);
		User u2 = new User(2,7,30,searcher,categories, analyzer);
		User u3 = new User(3,7,30,searcher,categories, analyzer);
		User u4 = new User(4,7,30,searcher,categories, analyzer);
		User u5 = new User(5,7,30,searcher,categories, analyzer);
		User u6 = new User(6,7,30,searcher,categories, analyzer);
		User u7 = new User(7,7,30,searcher,categories, analyzer);
		User u8 = new User(8,7,30,searcher,categories, analyzer);
		User u9 = new User(9,7,30,searcher,categories, analyzer);
		ArrayList<Document> up1 = u1.getUser_profile("allin");
		System.out.println(up1.size());
		for (Document d : up1) {
			System.out.print(d.get("Category") + " ");
			System.out.println(d.get("Id"));
		}
		
		System.out.println("##################################");
		System.out.println("#        Recommendations         #");
		System.out.println("##################################");
		
		Recommender engine = new Recommender(reader, analyzer);
		engine.recommend(u1, "hiphop;anime");
		ArrayList<Document> recommendations = engine.getResults();
		ArrayList<Float> scores = engine.getScores();
		
		int count = 0;
		for (Document d : recommendations) {
			System.out.println(d.get("Category") + " " + d.get("Id") + " " + scores.get(count));
			count++;
		}
		
		System.out.println("##################################");
		System.out.println("#        Recommendations         #");
		System.out.println("##################################");
		
		engine.recommend(u1, "allin");
		recommendations = engine.getResults();
		scores = engine.getScores();
		
		count = 0;
		for (Document d : recommendations) {
			System.out.println(d.get("Category") + " " + d.get("Id") + " " + scores.get(count) + " " + d.get("Tweet"));
			count++;
		}
		
		System.out.println(u1.getBOW(ixpath, tmpath, 20).toString());
		int e = 8/0;
		
		DataInputStream is;
		DataOutputStream os;
		System.out.println("Waiting for you...");
		ServerSocket socket = new ServerSocket(22399);
		while (true) {
			Socket socket2 = socket.accept();
			// String string =
			// "{\"id\":1,\"method\":\"object.deleteAll\",\"params\":[\"subscriber\"]}";
			is = new DataInputStream(socket2.getInputStream());
			os = new DataOutputStream(socket2.getOutputStream());
			PrintWriter pw = new PrintWriter(os);
			// pw.println(string);
			// pw.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			JSONObject json;

			json = new JSONObject(in.readLine());
			System.out.println(json.toString());
			
			JSONObject obj = new JSONObject().put("ack",1).put("lista", new String[] {"a","b","c","d"});
			JSONObject obj2 = new JSONObject().put("ack",1).put("lista", new String[] {"b","c","d","e"});
			if (json.getString("action").equals("prova")) {
				System.out.println(json.get("category"));
				System.out.println(obj.toString());
				pw.println(obj);
			} else if (json.getString("action").equals("update")) {
				System.out.println(json.get("category"));
				System.out.println(obj2.toString());
				pw.println(obj2);
			} else {
				System.out.println("vattelappesca");
			}

//			if (!json.has("action") || !json.has("id")) {
//				System.out.println("serve action e id");
//				continue;
//			}
//			if (json.getString("action").equals("recommend")) {
//				if (!json.has("category") || !json.has("start_date"))
//					continue;
//				Recommender rec = new Recommender(json.getInt("id"), json.getString("category"),
//						json.getString("start_date"));
//				ArrayList<Event> results = rec.raccomanda();
//				// for (Event r : results) {
//				// System.out.print(r.category + " " + r.fb_id + " ");
//				// System.out.println(r.name);
//				// }
//				// System.out.println("");
//				// System.out.println(rec.utente.profilo.toString());
//				// System.out.println(rec.utente.profilo_fb_id.toString());
//
//				pw.println(new JSONArray(results));
//			} else if (json.getString("action").equals("user_info")) {
//				Recommender rec = new Recommender(json.getInt("id"), "", "");
//				Utente u = rec.utente;
//				pw.println(new JSONObject().put("bags", u.bags).put("docs", u.profilo));
//			}

			pw.flush();
			socket2.close();
		}

	}

}