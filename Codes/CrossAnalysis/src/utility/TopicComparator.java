package utility;

import java.util.Comparator;

import model.Topic;

public class TopicComparator implements Comparator {

	@Override
	public int compare(Object t1, Object t2) {
		if (!(t1 instanceof Topic) || !(t2 instanceof Topic)) {
			System.err.println("Either of the two objects is NOT Topic!");
			return 0;
		}
		
		double p1 = ((Topic)t1).getProbability();
		double p2 = ((Topic)t2).getProbability();
		
		if(p1 == p2)
			return 0;
		//Sort the objects in a descending order
		if(p1 < p2)
			return 1;
		else
			return -1;
	}

}
