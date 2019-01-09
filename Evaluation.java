import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;

public class Evaluation {
	public static void main(String a[]) throws IOException, IndexAlreadyPresentException, ParseException, CategoryNotFoundException {
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
		
		String datapath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/data/tweets";
		String ixpath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix";
		String tmpath = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix_tmp";
		Indexer creator = new Indexer(datapath, ixpath, analyzer);
		IndexReader reader = creator.createIndex();
		IndexSearcher searcher = new IndexSearcher(reader);
		
		ArrayList<User> users = new ArrayList<User>();
		for (int i = 2; i <= 10; i++) {
			users.add(new User(i,7,i*5,searcher,categories, analyzer));
		}
		Recommender engine = new Recommender(reader, analyzer);
		ArrayList<Document> recommendations = new ArrayList<Document>();
		double[][] results = new double[users.size()][categories.size()+1]; 
		int rt = 10;
		for (int i = 0; i <= 8; i++) {
			int counc = 1;
			for (String c : categories.values()) {
				engine.recommend(users.get(i), c, rt);
				recommendations = engine.getResults();
				double hits = 0;
				for (Document d : recommendations) {
					if (d.get("Category").equals(c)) {
						hits++;
					}
					results[i][counc] = hits/rt;
				}
				counc++;
			}
		}
		for (int i = 0; i <= 8; i++) {
			engine.recommend(users.get(i), "allin", 10);
			int[] up = users.get(i).getDocCounts();
			int maxup = 0;
			for (int j : up) {
				if (j > maxup) {
					maxup = j;
				}
			}
			int upsize = users.get(i).getUser_profile().size();
			recommendations = engine.getResults();
			double hits = 0;
			double k = 1;
			for (Document d : recommendations) {
				hits += ((double) up[inv_categories.get(d.get("Category"))])/(k*maxup);
				k++;
			}
			results[i][0] = hits;
		}
		System.out.println("Scores: ");
		for (int i = 0; i < results.length; i++) {
//		    for (int j = 0; j < results[i].length; j++) {
//		        System.out.print(results[i][j] + " ");
//		    }
			System.out.println(Arrays.toString(results[i]));
		}
	}
}
