package fpmin;

import java.util.Comparator;

import model.TermMetric;

public class FPComparator<T> implements Comparator<T> {

	@Override
	public int compare(T c1, T c2) {
		if (!(c1 instanceof FrequentPattern) || !(c2 instanceof FrequentPattern)) {
			System.err.println("Either of the two objects is NOT FpRule!");
			return 0;
		}
		
		long v1 = ((FrequentPattern)c1).getFrequency();
		long v2 = ((FrequentPattern)c2).getFrequency();
		
		if(v1 == v2)
			return 0;
		
		//Sort the array in descending order
		if(v1 > v2)
			return -1;
		else
			return 1;
	}

}
