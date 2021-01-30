package search;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import utility.FileUtil;

public class LuceneSearcher implements Searcher {
	public static final String FIELD_NAME_CONTENT = "content";
	public static final String FIElD_NAME_PATH = "path";
	public static final String FIELD_NAME_FILENAME = "filename";

	@Override
	public Object indexDocuments(String url, int indexStorageType) {
		Directory indexDirectory = null;

		try {
			switch (indexStorageType) {

			case SearchConstants.INDEX_LOCATION_TYPE_FILE: {
				File file = new File(
						SearchConstants.INDEX_DIRECTORY);
				FileUtil.emptyDirectory(file);
				indexDirectory = FSDirectory.open(file);
				break;
			}
			case SearchConstants.INDEX_LOCATION_TYPE_MEMORY: {
				indexDirectory = new RAMDirectory();
				break;
			}
			default: {
				indexDirectory = new RAMDirectory();
				break;
			}
			}

			/*
			 * Create instance of analyzer, which will be used to tokenize the
			 * input data
			 */
			Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_46);
			// Create a new index
			// boolean create = true;
			// Create the instance of deletion policy
			// IndexDeletionPolicy deletionPolicy = new
			// KeepOnlyLastCommitDeletionPolicy();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46,
					standardAnalyzer);
			IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

			//Find all the text files under the specified URL, and index them
			List<File> fileList = new ArrayList<File>();
			FileUtil.findAllTextFiles(new File(url), fileList);
			for(File file : fileList){
				addFileToIndex(indexWriter, file);			
			}
			
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return indexDirectory;
	}

	@Override
	public List<model.Document> searchTerms(String[] keywords, Object indexer) {
		Directory indexDirectory = (Directory)indexer;
		List<model.Document> result = new ArrayList<model.Document>();
		
		try {
			IndexReader reader = DirectoryReader.open(indexDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(SearchConstants.SEARCH_RESULT_NUM, true);
			
			StringBuffer buffer = new StringBuffer();
			
			int i = 1;
			for(String item : keywords){
				buffer.append(item);
				if(i < keywords.length)
					buffer.append(" ");
				i++;
			}
			String queryStr = buffer.toString();
			
			Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_46);
			Query query = new QueryParser(Version.LUCENE_46, FIELD_NAME_CONTENT, standardAnalyzer).parse(queryStr);
	        searcher.search(query, collector);
	        ScoreDoc[] hitArray = collector.topDocs().scoreDocs;
	        
	        for(ScoreDoc hit : hitArray){
	        	int docId = hit.doc;
	            Document doc = searcher.doc(docId);
	            result.add(convertToEventDocument(doc));
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Convert the specified Lucene Document to an Event Document defined in the model package
	 * @param doc
	 * @return
	 */
	public model.Document convertToEventDocument(Document doc){
		if(doc == null)
			return null;
		
		String content = doc.get(FIELD_NAME_CONTENT);
		String title = doc.get(FIELD_NAME_FILENAME);
		String path = doc.get(FIElD_NAME_PATH);
		
		title = title.substring(0, title.lastIndexOf(".txt"));
//		System.out.println("[Title]: " + title);
//		System.out.println("[Content]: " + content);
		
		return new model.Document(title, content, path);
	}

	/**
	 * Create a Lucene document from the specified file, and add it to the specified IndexWriter
	 * @param file
	 * @return
	 */
	private boolean addFileToIndex(IndexWriter indexWriter, File file) {
//		FileReader fr = null;
		Document doc = new Document();
		try {

			String content = FileUtil.readTextFromFile(file);
//			fr = new FileReader(file);
//			doc.add(new TextField(FIELD_NAME_CONTENT, fr));
			doc.add(new TextField(FIELD_NAME_CONTENT, content, Field.Store.YES));
			doc.add(new StringField(FIElD_NAME_PATH, file.getAbsolutePath(), Field.Store.YES));
			doc.add(new StringField(FIELD_NAME_FILENAME, file.getName(), Field.Store.YES));
			
			
			indexWriter.addDocument(doc);
//			System.out.println("File [" + file.getAbsolutePath() + "] indexed!");
			
//			fr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not index: " + file.getAbsolutePath());
			return false;
		}

		return true;
	}
	
	public static void main (String args[]){
		LuceneSearcher searcher = new LuceneSearcher();
		Object indexer = searcher.indexDocuments("D:\\ResearchProjects\\EventExtraction\\Paragraphs", SearchConstants.INDEX_LOCATION_TYPE_FILE);
		String[] array = {"jobs", "steve", "apple", "dies", "world", "changed", "death", "legacy", "icon", "tech"};
		searcher.searchTerms(array, indexer);
		
	}

}
