import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class User {
	private int id;
	private ArrayList<Document> user_profile = new ArrayList<Document>();
	
	public User(int id, int count, int init, IndexSearcher searcher, Map<Integer,String> categories) throws ParseException, IOException {
		this.id = id;
		int[] docCounts = generateDocCounts(count,init); 
		System.out.println(Arrays.toString(docCounts));
		
		QueryParser parser_category = new QueryParser("Category", new StandardAnalyzer());
		Iterator<Integer> cat_key_it = categories.keySet().iterator();
		Integer key;
		while (cat_key_it.hasNext()) {
			key = cat_key_it.next();
//			System.out.println(categories.get(key));
			Query q_cat = parser_category.parse(categories.get(key));
			TopDocs topdocs = searcher.search(q_cat, init);
			ScoreDoc[] resultsList = topdocs.scoreDocs;
			for(int i = 0; i<docCounts[key]; i++){
				user_profile.add(searcher.doc(resultsList[i].doc));
			}
		}
	}
	
	public int getId() {
		return id;
	}

	public ArrayList<Document> getUser_profile() {
		return user_profile;
	}

	public ArrayList<Document> getUser_profile(String cat) throws CategoryNotFoundException {
		if (cat.equals("anime") | cat.equals("tech") | cat.equals("cars") | cat.equals("archi") | 
				cat.equals("sport") | cat.equals("weather") | cat.equals("hiphop")) {
			ArrayList<Document> filtered_profile = new ArrayList<Document>();

			for (Document d : user_profile) {
				if (d.get("Category").equals(cat)) {
					filtered_profile.add(d);
				}
			}
			return filtered_profile;
		} else if (cat.equals("allin")) {
			return user_profile;
		} else {
			throw new CategoryNotFoundException();
		}
	}
	
	public void setUser_profile(ArrayList<Document> user_profile) {
		this.user_profile = user_profile;
	}
	
	private int[] generateDocCounts(int count, int init) {
		int sum = init;
		Random g = new Random();
		int vals[] = new int[count];
		sum -= count;
		for (int i = 0; i < count-1; ++i) {
            vals[i] = g.nextInt(sum);
        }
        vals[count-1] = sum;
        Arrays.sort(vals);
        for (int i = count-1; i > 0; --i) {
            vals[i] -= vals[i-1];
        }
        for (int i = 0; i < count; ++i) { 
        	++vals[i]; 
        }
        return vals;
	}
}
