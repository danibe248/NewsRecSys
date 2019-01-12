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
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.stream.Collectors;

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

public class Main2 {
	public static void main(String[] args) throws ClassNotFoundException, IOException, IndexAlreadyPresentException, ParseException, CategoryNotFoundException {
		Map<Integer,String> categories = new HashMap<Integer, String>();
		categories.put(0, "anime");
		categories.put(1, "archi");
		categories.put(2, "cars");
		categories.put(3, "hiphop");
		categories.put(4, "sport");
		categories.put(5, "tech");
		categories.put(6, "weather");
		Map<String,Integer> inv_categories = categories.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
		Analyzer analyzer = CustomAnalyzer.builder()
				//.addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
				.addCharFilter("patternreplace", "pattern","((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)","replacement"," ")
				.addCharFilter("patternreplace", "pattern","[^a-zA-Z ]","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		
		String datapath = args[0];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/data/tweets";
		String ixpath = args[1];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix";
		String tmpath = args[2];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix_tmp";
		Indexer creator = new Indexer(datapath, ixpath, analyzer);
		IndexReader reader = creator.createIndex();
		IndexSearcher searcher = new IndexSearcher(reader);
		
		ArrayList<User> users = new ArrayList<User>();
		for (int i = 0; i < 10; i++) {
			users.add(new User(i,7,15,searcher,categories, analyzer));
		}
		Recommender engine = new Recommender(reader, analyzer);
		
		DataInputStream is;
		DataOutputStream os;
		System.out.println("Waiting for you...");
		ServerSocket socket = new ServerSocket(21111);
		ArrayList<Document> recommendations = new ArrayList<Document>();
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
			
			if (json.getString("action").equals("recommend") | json.getString("action").equals("catupdate")) {
				int req_id = json.getInt("id");
				String req_cat = json.getString("category");
				
				engine.recommend(users.get(req_id), req_cat, 10);
				recommendations = engine.getResults();
//				String[] recvec = new String[10];
				int count = 0;
//				for (Document d : recommendations) {
//					recvec[count] = "(" + d.get("Category") + ")  " + d.get("Tweet");
//					count++;
//				}
				JSONObject[] recvec = new JSONObject[10];
				for (Document d : recommendations) {
					JSONObject current_obj = new JSONObject();
					current_obj.put("category", d.get("Category"));
					current_obj.put("tweet", d.get("Tweet"));
					current_obj.put("itemid", count);
					recvec[count] = current_obj;
					count++;
				}
				System.out.println(recvec.toString());
				JSONObject obj = new JSONObject().put("ack",1).put("lista", recvec);
				pw.println(obj);
			} else if (json.getString("action").equals("itemupdate")) {
				int req_id = json.getInt("id");
				int req_item = json.getInt("itemid");
				String req_cat = json.getString("category");
				users.get(req_id).upUpdate(recommendations.get(req_item),inv_categories);
				engine.recommend(users.get(req_id), req_cat, 10);
				recommendations = engine.getResults();
				int count = 0;
				JSONObject[] recvec = new JSONObject[10];
				for (Document d : recommendations) {
					JSONObject current_obj = new JSONObject();
					current_obj.put("category", d.get("Category"));
					current_obj.put("tweet", d.get("Tweet"));
					current_obj.put("itemid", count);
					recvec[count] = current_obj;
					count++;
				}
				System.out.println(recvec.toString());
				JSONObject obj = new JSONObject().put("ack",1).put("lista", recvec);
				pw.println(obj);
			} else {
				int req_id = json.getInt("id");
				int[] cat_counts_int = users.get(req_id).getDocCounts();
				HashMap<String, SortedSet<String>> bows = users.get(req_id).getBOW(ixpath, tmpath, 10);
				
				JSONObject[] data = new JSONObject[7];
				for (int i = 0; i < 7; i++) {
					JSONObject current_obj = new JSONObject();
					current_obj.put("label", categories.get(i));
					current_obj.put("value", cat_counts_int[i]);
					current_obj.put("bow", bows.get(categories.get(i)).toString());
					data[i] = current_obj;
				}
				JSONObject obj = new JSONObject().put("ack",1).put("cat_counts",data);
//				JSONObject cat_counts = new JSONObject();
//				for (int i = 0; i < 7; i++) {
//					cat_counts.put(categories.get(i), cat_counts_int[i]);
//				}
//				System.out.println(cat_counts.toString());
//				JSONObject obj = new JSONObject().put("ack",1).put("cat_counts", cat_counts).put("categories", new String[] {"anime","archi","cars","hiphop","sport","tech","weather"});
				pw.println(obj);
			}
			
			pw.flush();
			socket2.close();
		}
	}

}
