package model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Topic {
	private double probability;
	private Map<String, Long> wordMap;
	
	public Topic(double probability, Map<String, Long> wordMap) {
		super();
		this.probability = probability;
		this.wordMap = new LinkedHashMap<String, Long>();
		this.wordMap.putAll(wordMap);
	}
	
	public Topic(){
		this.wordMap = new LinkedHashMap<String, Long>();
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public Map<String, Long> getWordMap() {
		return wordMap;
	}

	public void setWordMap(Map<String, Long> wordMap) {
		this.wordMap = wordMap;
	}
	
	public long getWordWeight(String word){
		if(this.wordMap == null)
			return -1;
		
		Long item = this.wordMap.get(word);
		if(item == null)
			return -1;
		else
			return item.longValue();
	}
	
	public Set<String> getWordSet(){
		return this.wordMap.keySet();
	}
	
	public void addWord(String word, long weight){
		if(this.wordMap == null)
			this.wordMap = new LinkedHashMap<String, Long>();
		this.wordMap.put(word, new Long(weight));
	}
}
