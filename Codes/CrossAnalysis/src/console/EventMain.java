package console;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.GeneralConstants;
import event.EventExtractor;
import LDA.TopicModel;
import model.Document;
import model.Event;
import model.Topic;
import model.TopicSet;
import search.LuceneSearcher;
import search.SearchConstants;
import utility.DateUtil;
import utility.FileUtil;
import word2vec.Word2VecUtil;

/**
 * @author Xuan Zhang
 * @author Wei Huang
 */

public class EventMain {
	public static final Map<String,String> PROPERTY_MAP = FileUtil.readKeyValueList("config.properties");
	

	public static void main(String[] args) {
		Date start = new Date();
		
		//final String newsDir = "D:\\Projects\\EventExtraction\\NewsPortals\\Reuters\\MH370";
		final String newsDir = PROPERTY_MAP.get("NEWS_DIR");
		final String paragraphDir = GeneralConstants.WORKING_DIRECTORY + File.separator + "paragraphs";
		final String ldaFile = GeneralConstants.WORKING_DIRECTORY + File.separator + "LDA.txt";
		
		final int topicNumber = Integer.parseInt(PROPERTY_MAP.get("TOPIC_NUMBER"));
		final int wordPerTopic = Integer.parseInt(PROPERTY_MAP.get("WORD_PER_TOPIC"));
		
		try {
			//Produce the LDA target file
			FileUtil.writeFileNamesToFile(newsDir, ldaFile);
//			FileUtil.writeRelevantParagraphsToFile(newsDir, ldaFile, 0);
			
			//Apply LDA, fetch topics
			TopicSet topicSet = TopicModel.extractTopics(ldaFile, topicNumber, wordPerTopic);
			
			System.out.println("\nSearch the paragraphs relevant with topics...\n");
			
			//Split news into paragraphs, skip the 1st line of each news
			FileUtil.emptyDirectory(new File(paragraphDir));
			FileUtil.splitDocumentsIntoParagraphs(newsDir, paragraphDir, false, 1);
			
			//Index all the paragraphs
			LuceneSearcher searcher = new LuceneSearcher();
			Object indexer = searcher.indexDocuments(paragraphDir, SearchConstants.INDEX_LOCATION_TYPE_FILE);
			
			//Extract named entities from the paragraphs relevant with topics, then create event
			Iterator<Topic> topicIterator = topicSet.getTopicIterator();
			List<Event> eventSet = new LinkedList<Event>();
			while(topicIterator.hasNext()){
				Topic topic = (Topic)topicIterator.next();
				Set<String> words = topic.getWordSet();
				
				String[] array = new String[words.size()];
				array = words.toArray(array);
				//Search topic key words among all the paragraphs, find the most relevant paragraphs
				List<Document> docList = searcher.searchTerms(array, indexer);
				//Extract events from the relevant paragraphs
				Event event = EventExtractor.extract(topic, docList);
				if(event != null)
					eventSet.add(event);
			}
			
			//Event Ranking						
			for(int i = eventSet.size()- 1; i > 0; --i){
				for (int j = 0; j < i; ++j) { 			
	            	if(eventSet.get(j+1).getStartDate().before(eventSet.get(j).getStartDate())){  
	                    Event temp = eventSet.get(j);  
	                    eventSet.set(j, eventSet.get(j+1));  
	                    eventSet.set(j+1, temp);  
	                }
	            }				
			}
			
			// Print Sorted Event
			System.out.println();
			System.out.println("Sorted Events");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			for(Event e : eventSet){
				System.out.println(formatter.format(e.getStartDate()) 
						+ " - " + formatter.format(e.getEndDate())
						+ "; Topic: " + e.getTopic().getWordSet());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Date end = new Date();
		System.err.println("Time used: " + DateUtil.calculatePeriod(start, end));

	}
	
	/**
	 * Compare the first half of topics with the second half of topics by their Word Vectors
	 * @param topicSet
	 */
	public static void compareTopicsByWordVector(TopicSet topicSet){
		
		List<Set<String>> topicList = topicSet.getTopicList();
		List<Set<String>> subList1 = topicList.subList(0, topicList.size()/2);
		List<Set<String>> subList2 = topicList.subList(topicList.size()/2, topicList.size());
		
		//For each of the first half of topics in the topic set, find the most relevant topic from the second half of topic set
		List<Set<String>> relevantList = Word2VecUtil.getRelevantWordSet(subList1, subList2);
		for(int i=0;i<subList1.size();i++){
			Set<String> set1 = subList1.get(i);
			System.out.println("Topic [" + i +"]:\t" + set1);
			Set<String> set3 = subList2.get(i);
			System.out.println("Topic [" + i +"]:\t" + set3);
			Set<String> set2 = relevantList.get(i);
			System.out.println("Relevant topic:\t" + set2 + "\n");
		}
	}

}
