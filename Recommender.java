import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class Recommender {
	private IndexReader reader;
    private ArrayList<Document> results = new ArrayList<Document>();
    private ArrayList<Float> scores = new ArrayList<Float>(); 
	
	public ArrayList<Document> getResults() {
		return results;
	}

	public void setResults(ArrayList<Document> results) {
		this.results = results;
	}

	public ArrayList<Float> getScores() {
		return scores;
	}

	public void setScores(ArrayList<Float> scores) {
		this.scores = scores;
	}

	public IndexReader getReader() {
		return reader;
	}

	public Recommender(IndexReader reader) {
		super();
		this.reader = reader;
	}

	public void setReader(IndexReader reader) {
		this.reader = reader;
	}

//	private String removeUrl(String input_str) {
//        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(input_str);
//        int i = 0;
//        while (m.find()) {
//            input_str = input_str.replaceAll(m.group(i),"").trim();
//            i++;
//        }
//        return input_str;
//    }
	
	public void/*ArrayList<Document>*/ recommend(User u, String cat) throws IOException, ParseException, CategoryNotFoundException {
		results.clear();
		scores.clear();
		Analyzer analyzer = CustomAnalyzer.builder()
				//.addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
				.addCharFilter("patternreplace", "pattern","((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)","replacement"," ")
				.addCharFilter("patternreplace", "pattern","[^a-zA-Z ]","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		QueryParser parser_tweet = new QueryParser("Tweet", analyzer);
		QueryParser parser_id = new QueryParser("Id", new StandardAnalyzer());
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new ClassicSimilarity());
		ArrayList<Document> documents = u.getUser_profile(cat);
		
		String str = ""; 
		  
	        // ArrayList to Array Conversion 
        for (int j = 0; j < documents.size(); j++) {
            // Assign each value to String array 
            str = str + " " + documents.get(j).get("Tweet");
        } 
        
        str = str.replaceAll("((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", " ");
        str = str.replaceAll("[^a-zA-Z ]", " ");
  
        Iterator<Document> up_documents = documents.iterator();
        Builder finalQueryBuilder = new BooleanQuery.Builder();
        while (up_documents.hasNext()) {
        	Document current_doc = up_documents.next();
        	Query current_q = parser_id.parse(current_doc.get("Id"));
        	finalQueryBuilder.add(current_q,Occur.MUST_NOT);
        }
        
        Query qt = parser_tweet.parse(str);
        finalQueryBuilder.add(qt,Occur.SHOULD);
        BooleanQuery finalQuery = finalQueryBuilder.build();
        TopDocs topdocs = searcher.search(finalQuery, 10);
        ScoreDoc[] resultsList = topdocs.scoreDocs;
        
        
        for(int i = 0; i<resultsList.length; i++){
        	results.add(searcher.doc(resultsList[i].doc));
    		scores.add(resultsList[i].score);
        }
/*        
        for(int i = 0; i<resultsList.length; i++){
        	String current = searcher.doc(resultsList[i].doc).get("Id");
        	Iterator<Document> up_documents = documents.iterator();
        	boolean flag = false;
        	while (up_documents.hasNext() && !flag) {
        		Document upd = up_documents.next();
        		if (current.equals(upd.get("Id"))) {
        			flag = true;
        		}
        	}
        	if (!flag) {
        		results.add(searcher.doc(resultsList[i].doc));
        		scores.add(resultsList[i].score);
        	}
        }
*/        	
//        return results;		
	}	
}