import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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

	private String removeUrl(String input_str)     {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input_str);
        int i = 0;
        while (m.find()) {
            input_str = input_str.replaceAll(m.group(i),"").trim();
            i++;
        }
        return input_str;
    }
	
	public void/*ArrayList<Document>*/ recommend(User u, String cat) throws IOException, ParseException, CategoryNotFoundException {
		results.clear();
		scores.clear();
		Analyzer analyzer = CustomAnalyzer.builder()
	            .addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		QueryParser parser_title = new QueryParser("Tweet", analyzer);
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
  
        
        Query qt = parser_title.parse(str);
        TopDocs topdocs = searcher.search(qt, 50);
        ScoreDoc[] resultsList = topdocs.scoreDocs;
        
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
        	
//        return results;		
	}
	
}