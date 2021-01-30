package fpmin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FrequentPattern {
	private Set<FpNode> nodeSet;

	private long frequency;
	
	public FrequentPattern(Set<FpNode> nodeSet, long frequency) {
		super();
		this.nodeSet = nodeSet;
		this.frequency = frequency;
	}
	
	public Set<FpNode> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(Set<FpNode> nodeSet) {
		this.nodeSet = nodeSet;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	
	public static FrequentPattern[] convertMapToFPArray(Map<Set<FpNode>, Long> map){
		if(map == null){
			System.err.println("NULL Pointer at FrequentPattern.mapToFpRuleArray");
			return null;
		}
		
		FrequentPattern[] array = new FrequentPattern[map.size()];
		
		int i = 0;
		Iterator<Set<FpNode>> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			Set<FpNode> set = iterator.next();
			long value = map.get(set);
			
			FrequentPattern rule = new FrequentPattern(set, value);
			array[i]  = rule;
			i++;
		}
		
		//Sort the array by descending order
		Arrays.sort(array, new FPComparator());
		return array;
	}
	
}
