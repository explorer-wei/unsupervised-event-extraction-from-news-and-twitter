package event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.GeneralConstants;
import utility.TermMetricComparator;
import utility.TermMetricUtil;
import ner.NERConstants;
import ner.NERUtil;
import model.Event;
import model.TermMetric;
import model.Topic;
import model.Document;

/**
 * @author Xuan Zhang
 * @author Wei Huang
 */

public class EventExtractor {
	/**
	 * Create an event based on the specified topic and related documents
	 * @param topic
	 * @param docList
	 * @return
	 */
	public static Event extract(Topic topic, List<Document> docList){
		String who = null;
		String when = null;
		String where = null;
		
		String NamedEntityFile = GeneralConstants.WORKING_DIRECTORY + File.separator + "NamedEntity.txt";
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(NamedEntityFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		
		Map<String, Integer> whoMap = new HashMap<String, Integer>();
		Map<String, Integer> whenMap = new HashMap<String, Integer>(); 
		Map<String, Integer> whereMap = new HashMap<String, Integer>();
		
		//Extract named entities from every relevant document (paragraph), and put them into global maps
		for(Document doc: docList){
			String inputFile = doc.getPath();
			Map<String, Map<String, Integer>> entityMap = null;
			try {
				entityMap = NERUtil.extractNamedEntities(inputFile, pw);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			Map<String, Integer> fileWhoMap = entityMap.get(NERConstants.TYPE_WHO);
			Map<String, Integer> fileWhenMap = entityMap.get(NERConstants.TYPE_WHEN);
			Map<String, Integer> fileWhereMap = entityMap.get(NERConstants.TYPE_WHERE);
			
			//Put the named entities extracted from current document into the global maps
			mergeMaps(whoMap, fileWhoMap);
			mergeMaps(whenMap, fileWhenMap);
			mergeMaps(whereMap, fileWhereMap);
		}
		
		try {
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Convert maps to arrays in order to do sorting
		TermMetric[] whoArray = TermMetricUtil.mapToTermMetricArray(whoMap);
		TermMetric[] whenArray = TermMetricUtil.mapToTermMetricArray(whenMap);
		TermMetric[] whereArray = TermMetricUtil.mapToTermMetricArray(whereMap);
		
		//Sort all the named entities by term frequency
		Arrays.sort(whoArray, new TermMetricComparator());
		Arrays.sort(whenArray, new TermMetricComparator());
		Arrays.sort(whereArray, new TermMetricComparator());
		
		who = (whoArray.length > 0) ? whoArray[whoArray.length - 1].getTerm() : null;
		when = (whenArray.length > 0) ? whenArray[whenArray.length - 1].getTerm() : null;
		where = (whereArray.length > 0) ? whereArray[whereArray.length - 1].getTerm() : null;

		//Print the event [Topic + Named entities]
		DecimalFormat df = new DecimalFormat("0.0000");
		System.out.println("\nTopic--[Probability: " + df.format(topic.getProbability()) + "]--" + topic.getWordSet());

		//Print the categorized named entities(ranked by term frequency) 
		printTopNamedEntities("WHO", whoArray, 5);
		printTopNamedEntities("WHEN", whenArray, 5);
		printTopNamedEntities("WHERE", whereArray, 5);
		
		List<List<String>> combinations = 
				NERUtil.getFrequentNamedEntityCombination(NamedEntityFile, whoMap, whenMap, whereMap);
		printTopNamedEntityCombination(combinations);
		
		SimpleDateFormat formatter = new SimpleDateFormat("MMM d , yyyy");
		Date start = new Date();
		Date end = new Date();
		if(combinations.size() != 0){
			try {
				//put start 1 day earlier, to avoid news latency
				start = formatter.parse(combinations.get(0).get(0));
				Calendar cal = new GregorianCalendar();
		        cal.setTime(start);
		        cal.add(Calendar.DATE, -1);
		        start=cal.getTime();
				
				end = formatter.parse(combinations.get(combinations.size()-1).get(0));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		return new Event(topic, who, when, where, start, end);
	}
	
	private static void printTopNamedEntities(String type, TermMetric[] array, int top){
		System.out.print(type + ": ");
		for(int i=0;i<top;i++){
			int index = array.length-1-i;
			
			if(index >= 0){
				TermMetric tm = array[index];
				System.out.print(tm.getTerm() + "-[" + tm.getMetric() + "]\t");
			}else
				break;
		}
		System.out.println();
	}
	
	private static void printTopNamedEntityCombination(List<List<String>> combinations){
//		final int LIMIT = 10;		
//		int count = 1;		
		
		for(List<String> combination : combinations){
			System.out.print("Combination: [");
			int i=0;
			for(String term : combination){
				System.out.print(term);
				if(i<combination.size()-1)
					System.out.print("; ");
				i++;
			}
			System.out.println("]");
//			if(count >= LIMIT)
//				break;
//			count++;
		}
	}
	
	private static void mergeMaps(Map<String, Integer> dest, Map<String, Integer> added){
		if(dest == null || added == null)
			return;
		
		for(String key : added.keySet()){
			int iAdded = added.get(key);
			Integer destInt = dest.get(key);
			
			if(destInt == null){//If the key not found in the destination map, add the key/value pair to it directly
				dest.put(key, new Integer(iAdded));
				continue;
			}else{//Otherwise, add the value from the added map to that in the dest map 
				destInt = new Integer(destInt.intValue() + iAdded);
				dest.put(key, destInt);
			}
		}
	}

}
