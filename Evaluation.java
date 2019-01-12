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
		
		String datapath = a[0];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/data/tweets";
		String ixpath = a[1];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix";
		String tmpath = a[2];//"/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/Progetto19/ix_tmp";
		Indexer creator = new Indexer(datapath, ixpath, analyzer);
		IndexReader reader = creator.createIndex();
		IndexSearcher searcher = new IndexSearcher(reader);
		
		ArrayList<User> users = new ArrayList<User>();
		for (int i = 2; i <= 10; i++) {
			users.add(new User(i,7,i*5,searcher,categories, analyzer));
		}
		ArrayList<Puppet> puppets = new ArrayList<Puppet>();
		puppets.add(new Puppet(0,7,1,searcher,categories, analyzer));
		for (int i = 1; i < users.size(); i++) {
			puppets.add(new Puppet(i,7,i*3,searcher,categories, analyzer));
		}
		Recommender engine = new Recommender(reader, analyzer);
		ArrayList<Document> recommendations = new ArrayList<Document>();
		double[][] results = new double[users.size()][categories.size()+1]; 

		System.out.println("Scores: ");
		for (int i = 0; i < results.length; i++) {
			System.out.println(Arrays.toString(results[i]));
		}
		int rt = 10;
		for (int i = 0; i <= 8; i++) {
			int counc = 1;
			for (String c : categories.values()) {
				engine.recommend(puppets.get(i), c, rt);
				recommendations = engine.getResults();
				double hits = 0;
				for (Document d : recommendations) {
					if (d.get("Category").equals(c)) {
						hits++;
					}
				}
				results[i][counc] = hits/rt + results[i][counc];
				counc++;
			}
		}
		for (int t = 0; t<1000; t++) {
			int i;
			for (i = 0; i <= 8; i++) {
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
				double dcg = 0;
				double idcg = 0;
				double k = 1;
				for (Document d : recommendations) {
					//hits += ((double) up[inv_categories.get(d.get("Category"))])/(k*maxup);
					if (k>1) {
						dcg += (((double) up[inv_categories.get(d.get("Category"))])*Math.log(2))/(maxup*Math.log(k));
						idcg += Math.log(2)/Math.log(k);
					} else {
						dcg += ((double) up[inv_categories.get(d.get("Category"))])/maxup;
						idcg += 1;
					}
					k++;
				}
				results[i][0] = 0.001*dcg/idcg + results[i][0];
			}
			users = new ArrayList<User>();
			for (int p = 2; p <= 10; p++) {
				users.add(new User(p,7,p*5,searcher,categories, analyzer));
			}
		}
		System.out.println("Scores: ");
		for (int i = 0; i < results.length; i++) {
			System.out.println(Arrays.toString(results[i]));
		}
		

	}
}
