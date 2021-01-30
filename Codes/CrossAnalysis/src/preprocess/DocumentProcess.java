package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import constants.FeatureConstants;
import utility.FileUtil;
import utility.TermMetricComparator;
import utility.TextUtil;
import model.Document;
import model.TermMetric;


public class DocumentProcess {
	public static final int MINIMUM_OVERALL_FREQUENCY = 20;
	
	public static Set<String> createFullTermSet(List<Document> docList) {
		Set<String> fullTermSet = new TreeSet<String>();
		if (docList == null)
			return fullTermSet;

		for (Document doc : docList) {
			String article = doc.getContent();
			// System.out.println(doc.getTitle());

			String[] array = article.split(" ");
			for (String str : array) {
				if (str.length() > 0)
					fullTermSet.add(str);
			}
		}

		System.err.println("Size of the Full Term Set: " + fullTermSet.size());

		int num = 1;
		for (String str : fullTermSet) {
			System.out.print(str + "\t");
			if (num % 5 == 0)
				System.out.println();
			num++;
		}
		System.out.println();

		return fullTermSet;
	}
	
	/**
	 * Create a term frequency array for every document in the specified list.
	 * The dimensions of the vector are specified by the termSet
	 * 
	 * @param docList
	 * @param termSet
	 */
	public static void createTermFrequencies(List<Document> docList,
			final Set<String> termSet) {
		if (docList == null || termSet == null) {
			System.err
					.println("Null pointer appears during creating term frequency for documents!");
			return;
		}

		String[] termArray = new String[termSet.size()];
		termArray = termSet.toArray(termArray);

		for (Document doc : docList) {
			// Initialize the term frequency array with zeros
			int[] tf = new int[termSet.size()];
			for (int i = 0; i < tf.length; i++) {
				tf[i] = 0;
			}

			String article = doc.getContent();
			String[] array = article.split(" ");

			// For every appearance of certain term, increase its frequency by 1
			for (String str : array) {
				if (str == null || str.length() == 0)
					continue;

				int index = TextUtil.binarySearch(termArray, str);
				// Skip the word which is not included in the feature
				if (index < 0)
					continue;
				// Increase the corresponding array element by 1
				tf[index] += 1;
			}

			// Set the term frequencies value of this document
			doc.setTermFrequencies(tf);
		}
	}
	
	/**
	 * Get the top N terms based on their TF-IDF value
	 * 
	 * @param docList
	 * @return
	 */
	public static Set<String> getPopularTermSet (List<Document> docList){
		Set<String> result = new TreeSet<String>();
		TermMetric[] tmArray = calculateTFIDF(docList);

		//Calculate the TF-IDF of every term
		Arrays.sort(tmArray, new TermMetricComparator());
		
		System.out.println("Top 100 features:");
		//Choose the top N features (terms)
		for(int i=0; i<tmArray.length && i<FeatureConstants.TOP_N_FEATURES;i++){
			int index = tmArray.length - 1 - i;
			System.out.println(tmArray[index].getTerm() + " : " + tmArray[index].getMetric());
			result.add(tmArray[index].getTerm());
		}
		
		return result;
	}
	
	/**
	 * Calculate the TF-IDF of every term from the specified document list
	 * 
	 * @param docList
	 * @return
	 */
	public static TermMetric[] calculateTFIDF(List<Document> docList){
		Map<String, Integer> map = new TreeMap<String, Integer>();
		if (docList == null)
			return null;
		
		int[] docVolumeArray = new int[docList.size()];
		int count = 0;
		for (Document doc : docList) {
			String article = doc.getContent();
			String[] array = article.split(" ");
			docVolumeArray[count] = array.length;
			for (String str : array) {
				
				if (str.length() > 0){
					Integer value = map.get(str);
					if(value == null)
						map.put(str, new Integer(1));
					else
						map.put(str, new Integer(value.intValue() + 1));
				}
			}
			count++;
		}
		
		//Total number of words in the corpus
		long sum = 0;
		for(int i: docVolumeArray){
			sum += i;
		}
		
		TermMetric[] tmArray = new TermMetric[map.size()];
		
		count = 0;
		//Compute TF-IDF
		for(String str : map.keySet()){
			Integer value = map.get(str);
			
			double tf = value.doubleValue() / sum;
			
			int df = 1;//avoid divided by 0 when calculating IDF
			for(Document doc : docList){
				if(termExistsInDocument(str, doc))
					df++;
			}
			double idf = Math.log(((double) docList.size()) / df);
			double tf_idf = tf * idf;
			tmArray[count] = new TermMetric(str, tf_idf);
			
			count++;
		}
		
		return tmArray;
	}
	
	/**
	 * Test whether a term appears in a document
	 * 
	 * @param term
	 * @param doc
	 * @return
	 */
	private static boolean termExistsInDocument(String term, Document doc){
		if(term == null || doc == null){
			System.err.println("NULL pointer in termExistsInDocuments function");
			return false;
		}
		
		String article = doc.getContent();
		if(article.indexOf(" " + term + " ") > 0)
			return true;
		else if(article.indexOf(term + " ") == 0)
			return true;
		else if(article.endsWith("" + term))
			return true;
		
		else return false;
	}

	/**
	 * Create documents from a title file
	 * 
	 * @param titleFileUrl
	 * @return
	 */
	public static List<Document> createDocumentsFromNewsTitles(String titleFileUrl){
		List<Document> result = new ArrayList<Document>();
		File titleFile = new File (titleFileUrl);
		Date date = new Date();//Just for padding
		preprocess.StanfordLemmatizer slem = StanfordLemmatizer.getInstance();
		
		try {
			BufferedReader br;
			InputStreamReader isr;
			FileInputStream fis;
			
			fis = new FileInputStream(titleFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			String line = br.readLine();
			while(line != null){
				line = line.trim();
				if(line.length() > 0){
					//Do stemming to the text in titles
					List<String> strs = slem.lemmatizeAndFilterStopWord(line, true,
							true);
					StringBuffer buffer = new StringBuffer();
					for(String item : strs){
						buffer.append(item + " ");
					}
					
					Document doc = new Document(line, buffer.toString().trim(), date, null);
					result.add(doc);
				}
				line = br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		
		return result;
	}

	public static List<Document> createDocumentsFromNews(String newsFileDir) {
		List<Document> result = new ArrayList<Document>();
		File newsDirFile = new File (newsFileDir);
		preprocess.StanfordLemmatizer slem = StanfordLemmatizer.getInstance();
		List<File> fileList = new ArrayList<File>();
		Date date = new Date();//Just for padding
		
		FileUtil.findAllTextFiles(newsDirFile, fileList);
		
		for(File file : fileList){
			try {
				BufferedReader br;
				InputStreamReader isr;
				FileInputStream fis;
				
				fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				
				
				StringBuffer buffer = new StringBuffer();
				String line = br.readLine();
				while(line != null){
					line = line.trim();
					if(line.length() > 0){
						//Do stemming to the text in titles
						List<String> strs = slem.lemmatizeAndFilterStopWord(line, true,
								true);
						
						for(String item : strs){
							buffer.append(item + " ");
						}
						
						
					}
					line = br.readLine();
				}
				
				Document doc = new Document(file.getName(), buffer.toString().trim(), date, null);
				result.add(doc);
				
				br.close();
				isr.close();
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return result;
	}
	
	public static void main(String[] args){
		String newsDir = "D:\\ResearchProjects\\EventExtraction\\AppleNews\\3day-news";
//		List<Document> docList = createDocumentsFromNews(newsDir);
		String outputDir = "D:\\ResearchProjects\\EventExtraction\\AppleNews\\output";
		FileUtil.splitDocumentsIntoParagraphs(newsDir, outputDir, false, 3);
	}
}
