import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class Puppet {
	private int id;
	private int[] docCounts;
	private ArrayList<Document> user_profile = new ArrayList<Document>();
	private Analyzer analyzer;
	
	public Puppet(int id, int count, int init, IndexSearcher searcher, Map<Integer,String> categories, Analyzer ana) throws ParseException, IOException {
		this.id = id;
		this.analyzer = ana;
		docCounts = new int[count];
		for (int i = 0; i< count; i++) {
			docCounts[i] = init;
		}
		System.out.println(Arrays.toString(docCounts));
		
		QueryParser parser_category = new QueryParser("Category", new StandardAnalyzer());
		Iterator<Integer> cat_key_it = categories.keySet().iterator();
		Integer key;
		while (cat_key_it.hasNext()) {
			key = cat_key_it.next();
			Query q_cat = parser_category.parse(categories.get(key));
			TopDocs topdocs = searcher.search(q_cat, init);
			ScoreDoc[] resultsList = topdocs.scoreDocs;
			for(int i = 0; i<docCounts[key]; i++){
				user_profile.add(searcher.doc(resultsList[i].doc));
			}
		}
	}
	
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public int[] getDocCounts() {
		return docCounts;
	}

	public void setDocCounts(int[] docCounts) {
		this.docCounts = docCounts;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Document> getUser_profile() {
		return user_profile;
	}

	public ArrayList<Document> getUser_profile(String cat) throws CategoryNotFoundException {
		String[] cats = cat.split(";");
		if (cats.length == 1) {
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
		} else if (cats.length > 1) {
			ArrayList<Document> filtered_profile = new ArrayList<Document>();
			for (String c : cats) {
				if (c.equals("anime") | c.equals("tech") | c.equals("cars") | c.equals("archi") | 
						c.equals("sport") | c.equals("weather") | c.equals("hiphop")) {
		
					for (Document d : user_profile) {
						if (d.get("Category").equals(c)) {
							filtered_profile.add(d);
						}
					}
				} else {
					throw new CategoryNotFoundException();
				}
			}
			return filtered_profile;
		} else {
			throw new CategoryNotFoundException();
		}
	}
	
	public void upUpdate(Document d, Map<String,Integer> map) {
		this.user_profile.add(d);		
		this.docCounts[map.get(d.get("Category"))] = this.docCounts[map.get(d.get("Category"))]+1;
	}
	
	public void setUser_profile(ArrayList<Document> user_profile) {
		this.user_profile = user_profile;
	}
	
	public HashMap<String, SortedSet<String>> getBOW(String ixpath, String tmpath, int nterms) throws IOException {
		FileUtils.cleanDirectory(new File(tmpath));
		
		Path ipath = Paths.get(tmpath);
		Directory directory = FSDirectory.open(ipath);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		TFIDFSimilarity sim = new ClassicSimilarity();
		config.setSimilarity(sim);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		for (Document d: user_profile) {
			iwriter.addDocument(d);
		}
		iwriter.close();
		
		IndexReader reader = DirectoryReader.open(directory);
		IndexReader reader_collection = DirectoryReader.open(FSDirectory.open(Paths.get(ixpath)));
		HashMap<String, SortedSet<String>> bows = new HashMap<String, SortedSet<String>>();
		Map<String,TopTerms> categories = new HashMap<String, TopTerms>();
		categories.put("anime", new TopTerms(nterms));
		categories.put("archi",new TopTerms(nterms));
		categories.put("cars", new TopTerms(nterms));
		categories.put("hiphop", new TopTerms(nterms));
		categories.put("sport", new TopTerms(nterms));
		categories.put("tech", new TopTerms(nterms));
		categories.put("weather", new TopTerms(nterms));
		
		for (int id_doc = 0; id_doc < reader.maxDoc(); id_doc++) {
			String cat = reader.document(id_doc).get("Category");
			
//			TopTerms tt = new TopTerms(20);
			try {
				TermsEnum it = reader.getTermVector(id_doc, "Tweet").iterator();
//				System.out.println(cat);
				BytesRef term = null;
				while ((term = it.next()) != null) {
					float idf = sim.idf(reader_collection.docFreq(new Term("Tweet", term.utf8ToString())),reader_collection.numDocs());
					float tf = sim.tf(it.totalTermFreq());
					TermTFIDF newterm = new TermTFIDF(tf * idf, term.utf8ToString());
					categories.get(cat).add(newterm);
//					System.out.println(newterm.term + ":" + newterm.tfidf);
				}
			} catch(Exception e) {
				System.out.println("Eccezione sulla creazione bow");
			}
		}
		Iterator<String> catit = categories.keySet().iterator();
		String currentcat;
		while (catit.hasNext()) {
			currentcat = catit.next();
			TreeSet<String> tmpSet = new TreeSet<String>();
			for (TermTFIDF ttf : categories.get(currentcat).top) {
				tmpSet.add(ttf.term);
			}
			bows.put(currentcat, tmpSet);
		}
		return bows;
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
