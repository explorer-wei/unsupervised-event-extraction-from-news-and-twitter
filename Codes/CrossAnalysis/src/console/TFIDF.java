package console;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import preprocess.StanfordLemmatizer;
import utility.MathUtil;
import utility.FileUtil;
import model.Distance;
import model.Document;

/**
 * @author Wei Huang 4/11/2014
 * @author Xuan Zhang 4/13/2014
 */

// TF-IDF (term frequency–inverse document frequency)
public class TFIDF {
	public static final double SIMILARITY_THRESHOLD = 0.1;

	public static List<double[]> Cal_TFIDF(List<String> docList,
			preprocess.StanfordLemmatizer slem) {

		List<List<String>> stemmedDocs = new ArrayList<List<String>>();
		
		// Create a full term set
		Set<String> termSet = new HashSet<String>();
		
		// Do stemming to all the documents and add the terms to the term set
		for (String text : docList) {
			List<String> stemmedWordList = slem.lemmatizeAndFilterStopWord(text, true,
					false);
			termSet.addAll(stemmedWordList);
			stemmedDocs.add(stemmedWordList);
		}

		List<String> docKey = new ArrayList<String>();
		
		docKey.addAll(termSet);
		int lenKey = termSet.size();
		//System.out.println("Term Set size = " + lenKey + ". " + termSet);

		int count = 1;
		double[] keyIDF = new double[lenKey];
		List<double[]> keyTF = new ArrayList<double[]>();

		// Analyze every document
		for (List<String> strs : stemmedDocs) {
			// Count total number of paragraphs where word appear
			for (String key : docKey) {
				if (strs.contains(key))
					keyIDF[docKey.indexOf(key)] += 1.0;
			}

			//System.out.println(text);
			double[] tf = new double[lenKey];
			for (String word : strs) {
				// Calculate TF
				int index = docKey.indexOf(word);
				if(index >=0 && index < lenKey)
					tf[index] += 1.0 / strs.size();
			}
			keyTF.add(tf);
			count++;
		}

		// Calculate IDF
		for (int i = 0; i < keyIDF.length; i++) {
			keyIDF[i] = MathUtil.log(((double)count) / keyIDF[i], 10);
		}

		// Calculate TF-IDF
		List<double[]> keyTFIDF = keyTF;
		for (int i = 1; i < count; i++) {
			double[] tfidf = keyTFIDF.get(i - 1);
			for (int j = 0; j < keyIDF.length; j++) {
				tfidf[j] = tfidf[j] * keyIDF[j];
			}
			keyTFIDF.set(i - 1, tfidf);
		}
		/*
		 * // Print out results for (int i = 1; i < count; i++) {
		 * System.out.println(i + ": " + doc.get(i-1)); System.out.println(i +
		 * ": TFIDF = " + Arrays.toString(keyTFIDF.get(i-1))); }
		 */
		return keyTFIDF;
	}

	/**
	 * Calculate the cosine similarity between the two vectors
	 * 
	 * @param q
	 * @param s
	 * @return
	 */
	public static double Cal_Relevance(double[] q, double[] s) {
		double qs = 0, qq = 0, ss = 0;
		for (int i = 0; i < q.length; i++) {
			qs += q[i] * s[i];
			qq += q[i] * q[i];
			ss += s[i] * s[i];
		}

		return qs / Math.sqrt(qq * ss);
	}

	/**
	 * Regarding every document in the specified directory, extract its paragraphs which are most relevant with its title. 
	 * Create a Document instance with these relevant paragraphs for every document, and put it in the returned list
	 * 
	 * @param inputDir	the directory of input files
	 * @param skipTopLines	specify how many lines to skip from the top of the document
	 * @return
	 */
	public static List<Document> getRelevantParagraphs (String inputDir,int skipTopLines) {
		List<Document> result = new ArrayList<Document>();
		
		if(inputDir == null || skipTopLines < 0){
			System.err.println("NULL input directory or negative number of skip lines");
			return result;
		}

		final File sourceDirFile = new File(inputDir); // D:\\Test\\Apple3days
		List<File> list = new ArrayList<File>();
		FileUtil.findAllTextFiles(sourceDirFile, list);
		System.out.println("Got File List:" + list.size());

		preprocess.StanfordLemmatizer slem = StanfordLemmatizer.getInstance();

		// Skip the top N lines (usually the top 3 lines are the topic, time and
		// agency of the news)

		for (File file : list) {
			List<String> filteredList = new ArrayList<String>();

			// Extract paragraphs of a document
			String content = FileUtil.readTextFromFile(file);
			List<String> paragraphList = new ArrayList<String>(
					Arrays.asList(content.split("\n")));
			// If there're too few lines in the document, skip it
			if (paragraphList.size() <= skipTopLines)
				return result;
			paragraphList = paragraphList.subList(skipTopLines,
					paragraphList.size());

			// First line is title
			String title = file.getName();
			System.out.println("Title: " + title);
			// cut ".txt"
			title = title.substring(0, title.length() - 4);
			paragraphList.add(0, title); 
																			 

			// Text to low case
			for (int i = 0; i < paragraphList.size(); i++) {
				String temp = paragraphList.get(i).toLowerCase().trim();
				if (temp.length() == 0) {
					paragraphList.remove(i);
					i--;
				} else
					paragraphList.set(i, temp);
			}

			// Calculate TFIDF for each word in each paragraph
			List<double[]> tfidf = Cal_TFIDF(paragraphList, slem);

			// Calculate relevance between title and each paragraph
			List<Distance> dis = new ArrayList<Distance>();
			for (int i = 0; i < tfidf.size(); i++) {
				dis.add(new Distance(Cal_Relevance(tfidf.get(0), tfidf.get(i)),
						i));
			}

			// Sort the relevance
			Collections.sort(dis, new Comparator<Distance>() {
				public int compare(Distance o1, Distance o2) {
					return Double.compare(o1.getDis(), o2.getDis());
				}
			});
			Collections.reverse(dis);

			// Print distance and corresponding content
			double score = 0;
			int effectiveline = 0;
			for (int i = 0; i < dis.size(); i++) {

				score = dis.get(i).getDis();
				// set filter threshold
				if (score > TFIDF.SIMILARITY_THRESHOLD && score < 1) {
					String relevantParagraph = paragraphList.get(dis.get(i).getIndex());
					System.out.println(score + ": "
							+ relevantParagraph);
					filteredList.add(relevantParagraph);
					effectiveline++;
				}
			}

			System.out.println(effectiveline + " relevant, out of total "
					+ dis.size() + " paragraphs\n");
			Document document = new Document (title, filteredList, file.getAbsolutePath());
			result.add(document);
		}
		
		return result;
	}
}
