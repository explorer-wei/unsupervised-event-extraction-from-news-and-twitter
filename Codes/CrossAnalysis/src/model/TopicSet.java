package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import utility.TopicComparator;

public class TopicSet {
	private Set<Topic> set = new TreeSet<Topic>(new TopicComparator());

	public TopicSet(Set<Topic> set) {
		super();
		this.set = set;
	}

	public TopicSet() {
		
	}

	public Set<Topic> getSet() {
		return set;
	}

	public void setSet(Set<Topic> set) {
		this.set = set;
	}
	
	public Iterator<Topic> getTopicIterator(){
		return this.set.iterator();
	}
	
	public void addTopic(Topic topic){
		this.set.add(topic);
	}
	
	public void printDistribution(){
		int i = 0;
		java.text.DecimalFormat format = new java.text.DecimalFormat("0.0000");
		for(Topic topic : this.set){
			System.out.println("Topic [" + i + "] probability: [" + format.format(topic.getProbability()) 
					+ "]\t\tWords: " + topic.getWordMap().toString());
			i++;
		}
	}
	
	public void printTopicTerms(){
		int i = 0;
		java.text.DecimalFormat format = new java.text.DecimalFormat("#.0000");
		for(Topic topic : this.set){
			Set<String> wordSet = topic.getWordSet();
			System.out.println("Topic [" + i + "] probability: [" + format.format(topic.getProbability())
					+ "]\t\tWords: " + wordSet);
			i++;
		}
	}
	
	public List<Set<String>> getTopicList(){
		List<Set<String>> result = new ArrayList<Set<String>>();
		
		for(Topic topic : set){
			Set<String> entry = topic.getWordSet();
			result.add(entry);
		}
		
		return result;
	}

}
