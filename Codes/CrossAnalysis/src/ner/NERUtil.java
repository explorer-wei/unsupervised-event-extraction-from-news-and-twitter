package ner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import fpmin.FPComparator;
import fpmin.FpGrowth;
import fpmin.FpNode;
import fpmin.FrequentPattern;
import utility.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is a demo of calling CRFClassifier programmatically.
 * <p>
 * Usage:
 * <code> java -mx400m -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]]</code>
 * <p>
 * If arguments aren't specified, they default to
 * ner-eng-ie.crf-3-all2006.ser.gz and some hardcoded sample text.
 * <p>
 * To use CRFClassifier from the command line: java -mx400m
 * edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -textFile
 * [file] Or if the file is already tokenized and one word per line, perhaps in
 * a tab-separated value format with extra columns for part-of-speech tag, etc.,
 * use the version below (note the 's' instead of the 'x'): java -mx400m
 * edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -testFile
 * [file]
 * 
 * NER classification mapping to 3Ws: Who: Organization, Person Where: Location
 * When: Date, Time
 * 
 * @author Jenny Finkel
 * @author Christopher Manning
 * @author Xuan Zhang (Adaptor)
 * @author Wei Huang(Adaptor)
 */

public class NERUtil {
	public static final String serializedClassifier = "classifiers/english.muc.7class.distsim.crf.ser.gz";
	
	public static final String[] STOP_ENTITIES = {"Reuters"};
	
	private static AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier
			.getClassifierNoExceptions(serializedClassifier);

	public static Map<String, Map<String, Integer>> extractNamedEntities(String inputFile, PrintWriter fileWriter)
			throws IOException {

		Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();

		Map<String, Integer> whoMap = new HashMap<String, Integer>();
		Map<String, Integer> whenMap = new HashMap<String, Integer>();
		Map<String, Integer> whereMap = new HashMap<String, Integer>();
		
		Set<String> whoSet = new HashSet<String>();
		Set<String> whenSet = new HashSet<String>();
		Set<String> whereSet = new HashSet<String>();		

		String fileContents = IOUtils.slurpFile(inputFile);
		
		// Annotate every word in the file with the 7 named entity types
		List<List<CoreLabel>> out = classifier.classify(fileContents);
		//List<List<String>> entitiesInSentences = new LinkedList<List<String>>();
		List<String> entitiesInDoc = new LinkedList<String>();
		
		/* Modified List<List<CoreLabel>> out, combine continuous words which belong to same entity type*/
		// For every sentence
		for (int i = 0; i < out.size(); i++) {
			
			
			String comWord = out.get(i).get(0).word();// word after combination
			String tag = out.get(i).get(0).get(CoreAnnotations.AnswerAnnotation.class); // indicate type of previous word
			
			//for each word in this sentence
			for (int j = 1; j < out.get(i).size(); j++) {
				String word = out.get(i).get(j).word();
				String category = out.get(i).get(j).get(CoreAnnotations.AnswerAnnotation.class);
				// System.out.println(tag + " : " + category + " : " + sentence);
				
				// combine continuous same-type words
				if(category.equals(tag)) {
					comWord = comWord + " " + word;
					out.get(i).remove(j);
					j = j - 1;
					out.get(i).get(j).setWord(comWord);
				}
				else
					comWord = word;
				
				tag = category;
			}
			
			//Put the identified named entities in the corresponding sets
			for (CoreLabel cl : out.get(i)) {
				String word = cl.word();
				String category = cl.get(CoreAnnotations.AnswerAnnotation.class);
				
				
				// System.out.println(word + " : " + category);
				
				if(category.equals("PERSON") || category.equals("ORGANIZATION")) {
					//Rule out the meaningless entities such as "Reuters"
					if(!Arrays.asList(STOP_ENTITIES).contains(word)){
						entitiesInDoc.add(word);
						whoSet.add(word);
					}
				}
				else if (category.equals("LOCATION")) {
					entitiesInDoc.add(word);
					whereSet.add(word);
				}					
				else if (category.equals("DATE") || category.equals("TIME")) {
					entitiesInDoc.add(word);
					whenSet.add(word);
				}
			}
			
			//entitiesInSentences.add(entitiesPerSentence);
		}

		//File file = new File(inputFile);
		//produceNamedEntityFile(entitiesInSentences, file.getParent() + File.separator + "NamedEntities.txt");
		produceNamedEntityFile(entitiesInDoc,fileWriter);
		
		for(String term : whoSet){
			int num = countTermFrequencyInList(term, out);
			whoMap.put(term, new Integer(num));
		}
		
		for(String term : whereSet){
			int num = countTermFrequencyInList(term, out);
			whereMap.put(term, new Integer(num));
		}
		
		for(String term : whenSet){
			int num = countTermFrequencyInList(term, out);
			whenMap.put(term, new Integer(num));
		}
		
		//Save the 3 maps in the final result
		result.put(NERConstants.TYPE_WHO, whoMap);
		result.put(NERConstants.TYPE_WHEN, whenMap);
		result.put(NERConstants.TYPE_WHERE, whereMap);
		
//		System.out.println(whoMap.size() + " WHO entities: " + whoMap);
//		System.out.println(whereMap.size() + " WHERE entities: " + whereMap);
//		System.out.println(whenMap.size() + " WHEN entities: "  + whenMap);

		return result;
	}
	
	/**
	 * Get the frequent named entity combinations from the specified file
	 * Incomplete combinations will be filtered out according to the specified entity map
	 *  
	 * @param namedEntityFile
	 * @param whoMap
	 * @param whenMap
	 * @param whereMap
	 * @return
	 */
	public static List<List<String>> getFrequentNamedEntityCombination(String namedEntityFile, 
			Map<String, Integer> whoMap, 
			Map<String, Integer> whenMap, 
			Map<String, Integer> whereMap){
		List<List<String>> result = new LinkedList<List<String>>();
		List<List<String>> combine = new LinkedList<List<String>>();
		
		FrequentPattern[] patterns = FpGrowth.getFrequentPatterns(namedEntityFile);
/*		for(FrequentPattern item : patterns){
			Set<FpNode> set = item.getNodeSet();
			long value = item.getFrequency();
			
			// updated the weighted value of importance of the patterns
			value = (long)(value * 0.7 + set.size() * 0.3);
			item.setFrequency(value);
		}
*/		
		//Rank all the frequent patterns (named entity combinations) by "frequency + term set size"
//		Arrays.sort(patterns, new FPComparator());
		
		Set<String> whoSet = whoMap.keySet();
		Set<String> whenSet = whenMap.keySet();
		Set<String> whereSet = whereMap.keySet();
		HashMap<String, Integer> newWhen = new HashMap<String, Integer>();
		int count = 0;
		int mostWhen = 0; 

		//Filter incomplete named entity combinations, which are lack of at least one type of entity
		for(FrequentPattern item : patterns){
			
			int hasWho = 0;
			int hasWhen = 0;
			int hasWhere = 0;
			
			List<String> termList = new LinkedList<String>();
			Set<FpNode> set = item.getNodeSet();
			
			int countTime = 0;
			for(FpNode node : set){
				String term = node.getIdName();
				if(whenSet.contains(term)) {
					hasWhen = 1;
					termList.add(0, term);
					countTime++;
				}
				else {
					if(whoSet.contains(term))
						hasWho = 1;
					if(whereSet.contains(term))
						hasWhere = 1;
					termList.add(term);
				}				
			}
			
			// Consider Time as a must-have
			if(hasWhen == 1 && (hasWho+hasWhere)>0){
				// Combine keywords for same time stamp
				combine.add(termList);
				for(int i = 0; i < countTime; i++){
					if(newWhen.containsKey(termList.get(i))){  
			            int oldValue = newWhen.get(termList.get(i));  
			            newWhen.put(termList.get(i), oldValue+1);
			            mostWhen = mostWhen>oldValue ? mostWhen : oldValue+1;
			        } else {  
			        	newWhen.put(termList.get(i), 1);
			        	mostWhen = mostWhen>0 ? mostWhen : 1;
			        }
				}
				
				// Print out original combinations
//				System.out.println("Freq: " + item.getFrequency() + ", Set: " + termList);

				count++;
			}
		}		

		// format and sort newWhen, as well as filter low-frequency timestamps
		List<String> sortedWhen = DateUtil.sortDateString(newWhen, mostWhen/2);
		System.out.println("Only keep the dates which have appeared at least " + mostWhen/2 + " times");
		
//		System.out.println("Total " + newWhen.size() + " When: " + newWhen);
//		System.out.println("Total " + sortedWhen.size() + " Date: " + sortedWhen);

		// Combine named entities for same time stamp
		for(String time : sortedWhen){
			Set<String> newList = new HashSet<String>();
			newList.add(time);
			for(List<String> list : combine){
				if(list.contains(time)){
					for(String temp : list){
						newList.add(temp);
					}
				}
			}
			
			// Avoid duplicate - when there more than 1 "When" in set
			boolean needAdd = true;
			for(List<String> item : result){
				if(item.containsAll(newList))
					needAdd = false;
			}
			// Organize the named entities: When + Where + Who
			if(needAdd){
				List<String> tempList = new LinkedList<String>();
				for(String temp : newList){
					if(!whenSet.contains(temp)){
						if (whereSet.contains(temp))
							tempList.add(0, temp);
						else
							tempList.add(temp);
					}
				}
				tempList.add(0, time);
				
				result.add(tempList);
			}				
		}
		
		System.out.println("Number of sets before combining: " + count + "; Number of sets after combining: " + result.size());
		
		return result;
	}
	
/*	private static void produceNamedEntityFile(List<List<String>> entitiesInSentences, String outputFile){
		File dest = new File(outputFile);
		try {
			PrintWriter pw = new PrintWriter(dest);
			for(List<String> entitiesPerSentence: entitiesInSentences){
				for(String entity : entitiesPerSentence){
					pw.print(entity + ",");
				}
				pw.println();
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
	
	private static void produceNamedEntityFile(List<String> entitiesInDoc, PrintWriter pw){
		try {
			int i = 0;
			String separator = " : ";
			for(String entity : entitiesInDoc){
				pw.print(entity);
				if(i < (entitiesInDoc.size() - 1))
					pw.print(separator);
				i++;
			}
			pw.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static int countTermFrequencyInList(String term, final List<List<CoreLabel>> out){
		int result = 0;
		
		for (List<CoreLabel> sentence : out) {
			for (CoreLabel cl : sentence) {
				String word = cl.word();
				if(word.equals(term))
					result++;
			}
		}
		return result;
	}
	
	public static void main(String args[]){
		String inputFile = "D:\\Test\\All about Steve.txt-006.txt";
		String dest = "D:\\Test\\NamedEntitySample.txt";
		
		try {
			PrintWriter pw = new PrintWriter(dest);
			NERUtil.extractNamedEntities(inputFile, pw);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
