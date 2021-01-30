package search;

import java.util.List;

import model.Document;

public interface Searcher {
	List<Document> searchTerms(String[] keywords, Object indexer);
	Object indexDocuments(String url, int indexStorageType);

}
