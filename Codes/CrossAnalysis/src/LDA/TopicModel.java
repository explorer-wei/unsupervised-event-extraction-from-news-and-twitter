package LDA;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import model.Topic;
import model.TopicSet;

public class TopicModel {
	public static final int NUM_OF_THREADS = 10;
	public static final int NUM_OF_ITERATIONS = 1000;//You can reduce it to 100 in order to accelerate

	public static TopicSet extractTopics(String filePath, int numTopics, int wordsPerTopic) throws Exception {
		TopicSet topicSet = new TopicSet();

		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		//pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
											   3, 2, 1)); // data, label, name fields

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is 
		
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(NUM_OF_THREADS);//Changed to 10 by XZ

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(NUM_OF_ITERATIONS);//iterations set to 1000 by XZ
		model.setOptimizeInterval(50);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		// Show top 5 words in topics with proportions for the first document
		for (int topicNum = 0; topicNum < numTopics; topicNum++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topicNum).iterator();
			double topicProbability = topicDistribution[topicNum];
			Map<String, Long> wordMap = new LinkedHashMap<String, Long>();
			Topic topic = new Topic(topicProbability, wordMap);
			
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topicNum, topicProbability);
			int rank = 0;
			while (iterator.hasNext() && rank < wordsPerTopic) {
				IDSorter idCountPair = iterator.next();
				String strWord = dataAlphabet.lookupObject(idCountPair.getID()).toString();
				double wordWeight = idCountPair.getWeight();
				out.format("%s (%.0f) ", strWord, wordWeight);
				
				topic.addWord(strWord, (long)wordWeight);
				rank++;
			}
			
			topicSet.addTopic(topic);
			//System.out.println(out);
			
		}
		
		topicSet.printTopicTerms();
		return topicSet;
	
	}
	
	public static void main (String args[]){
		try {
			TopicSet topicSet = extractTopics("LDA-Apple-Monthly.txt", 100, 5);
			topicSet.printDistribution();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}