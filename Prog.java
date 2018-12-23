import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.PrefixCodedTerms.TermIterator;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class Prog {
	public static void main(String[] a) throws IOException, ParseException {
		String path = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/xml";
		String ix = "/home/ld/daniele/UniMiB/Magistrale/IR/2019/lab/progix";
		
		File[] files = new File(path).listFiles();
		ArrayList<Document> docs = new ArrayList<Document>();
		
		FieldType ft = new FieldType();
		ft.setTokenized(true);
		ft.setStored(true);
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		ft.setStoreTermVectors(true);
		
		for (File file : files) {
			File fXmlFile = new File(path+"/"+file.getName());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				System.out.println("==============================");
				System.out.println(file.getName());
				System.out.println("Title: "+doc.getElementsByTagName("title").item(0).getTextContent());
				System.out.println("Author: "+doc.getElementsByTagName("author").item(0).getTextContent());
				System.out.println("ISBN: "+doc.getElementsByTagName("isbn").item(0).getTextContent());
				System.out.println("Review: "+doc.getElementsByTagName("editorialReview").item(0).getTextContent());
				
				Document doc1 = new Document();
				doc1.add(new StringField("ISBN",doc.getElementsByTagName("isbn").item(0).getTextContent(),Field.Store.YES));
				doc1.add(new TextField("Title",doc.getElementsByTagName("title").item(0).getTextContent(),Field.Store.YES));
				doc1.add(new TextField("Author",doc.getElementsByTagName("author").item(0).getTextContent(),Field.Store.YES));
				doc1.add(new Field("Review",doc.getElementsByTagName("editorialReview").item(0).getTextContent(),ft));
				docs.add(doc1);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		Analyzer analyzer = CustomAnalyzer.builder()
	            .addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		Path ipath = Paths.get(ix);
		Directory directory = FSDirectory.open(ipath);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setSimilarity(new ClassicSimilarity());
		IndexWriter iwriter = new IndexWriter(directory, config);
		System.out.println(config.getSimilarity());
		
		for (Document d: docs) {
			iwriter.addDocument(d);
		}
		iwriter.close();
		
		System.out.println("##########################################################################################################");
		System.out.println("##########################################################################################################");
		System.out.println("##########################################################################################################");
		System.out.println("##########################################################################################################");
		
		IndexReader reader = DirectoryReader.open(directory);
		for (int docid = 0; docid < reader.maxDoc(); docid++) {
			System.out.println(docid);
		}
		
		TermsEnum tv = reader.getTermVector(0, "Review").iterator();
		BytesRef term = null;
		while ((term = tv.next()) != null) {
			System.out.println(term.utf8ToString());
		}
		
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new ClassicSimilarity());
		QueryParser parser_title = new QueryParser("Review", analyzer);
		Query qt = parser_title.parse("germany dreadnought history");
		QueryParser parser_author = new QueryParser("Author", analyzer);
		Query qa = parser_author.parse("Isaac");
		
		BooleanQuery finalQuery = new BooleanQuery.Builder().add(qt, Occur.MUST).add(qa, Occur.MUST).build();
		TopDocs topdocs = searcher.search(qt, 100);
		ScoreDoc[] resultsList = topdocs.scoreDocs;
		System.out.println(resultsList.length);
		for(int i = 0; i<resultsList.length; i++){
			Document book = searcher.doc(resultsList[i].doc);
			
			String author= book.getField("Author").stringValue();
			System.out.println(author);
			
			String title= book.getField("Title").stringValue();
			System.out.println(title);
			
			String review= book.getField("Review").stringValue();
			System.out.println(review);
			System.out.println("==============================");
		}
		
		FileUtils.cleanDirectory(new File(ix)); 
	}
}