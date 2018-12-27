import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	private String datapath;
	private String indexpath;
	private ArrayList<Document> docs = new ArrayList<Document>();
	
	public Indexer(String datapath, String indexpath) {
		this.datapath = datapath;
		this.indexpath = indexpath;
	}
	
	public String getDatapath() {
		return datapath;
	}
	
	public void setDatapath(String datapath) {
		this.datapath = datapath;
	}
	
	public String getIndexpath() {
		return indexpath;
	}
	
	public void setIndexpath(String indexpath) {
		this.indexpath = indexpath;
	}
	
	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}
	
	public IndexReader createIndex() throws IOException, IndexAlreadyPresentException {
		String os = System.getProperty("os.name").toLowerCase().split(" ")[0];
		if (Files.walk(Paths.get(this.indexpath)).filter(Files::isRegularFile).iterator().hasNext()) {
			throw new IndexAlreadyPresentException("Delete previous index");
//			FileUtils.cleanDirectory(new File(this.indexpath));
		} else {
			System.out.println("creating index...");
		}
		FieldType ft = new FieldType();
		ft.setTokenized(true);
		ft.setStored(true);
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		ft.setStoreTermVectors(true);
		
		Iterator<Path> files = Files.walk(Paths.get(this.datapath)).filter(Files::isRegularFile).iterator();
		while(files.hasNext()) {
			Path current = files.next();
//			System.out.println(current.toString());
			File fXmlFile = new File(current.toString());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
//				System.out.println("==============================");
//				System.out.println(current.toString());
//				System.out.println("Date: "+doc.getElementsByTagName("data").item(0).getTextContent());
//				System.out.println("Category: "+doc.getElementsByTagName("categoria").item(0).getTextContent());
//				System.out.println("Tweet: "+doc.getElementsByTagName("tweet").item(0).getTextContent());
//				System.out.println("Article: "+doc.getElementsByTagName("articolo").item(0).getTextContent());
				
				Document doc1 = new Document();
				String[] splitted_path;
				if (os.equals("windows")) {
					splitted_path = current.toString().split("\\\\");
				} else {
					splitted_path = current.toString().split("/");
				}
				String filename = splitted_path[splitted_path.length-1];
				String[] splitted_filename = filename.split("\\.");
				
				doc1.add(new StringField("Id",splitted_filename[0],Field.Store.YES));
				doc1.add(new StringField("Date",doc.getElementsByTagName("data").item(0).getTextContent(),Field.Store.YES));
				doc1.add(new StringField("Category",doc.getElementsByTagName("categoria").item(0).getTextContent(),Field.Store.YES));
				doc1.add(new Field("Tweet",doc.getElementsByTagName("tweet").item(0).getTextContent(),ft));
				doc1.add(new Field("Article",doc.getElementsByTagName("articolo").item(0).getTextContent(),ft));
				this.docs.add(doc1);
			} catch(Exception e) {
				System.out.println(current.toString());
				System.out.println(e.getMessage());
			}
		}
		
		Analyzer analyzer = CustomAnalyzer.builder()
	            .addCharFilter("patternreplace","pattern","\\p{Punct}","replacement"," ")
				//.addCharFilter("patternreplace", "pattern","((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)","replacement"," ")
				//.addCharFilter("patternreplace", "pattern","[^a-zA-Z ]","replacement"," ")
	            .withTokenizer("whitespace")
	            .addTokenFilter("lowercase")
	            .addTokenFilter("stop", "ignoreCase", "false", "words", "stoplist.txt", "format", "wordset")
	            .build();
		
		Path ipath = Paths.get(this.indexpath);
		Directory directory = FSDirectory.open(ipath);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setSimilarity(new ClassicSimilarity());
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		for (Document d: docs) {
			iwriter.addDocument(d);
		}
		iwriter.close();
		
		IndexReader reader = DirectoryReader.open(directory);
		System.out.println(reader.numDocs()==this.docs.size());
		return reader;
	}
}
