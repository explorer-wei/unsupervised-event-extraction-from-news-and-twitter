package utility;

import java.util.Iterator;
import java.util.Map;

import model.TermMetric;

public class TermMetricUtil {
	/**
	 * Convert a Map<String, Integer> or a Map<String, Double> to a TermMetric array
	 * @param map
	 * @return
	 */
	public static TermMetric[] mapToTermMetricArray(Map map){
		if(map == null){
			System.err.println("NULL Pointer at TermMetric.mapToTermMetricArray");
			return null;
		}
		
		TermMetric[] result = new TermMetric[map.size()];
		
		int num = 0;
		Iterator iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = (String)iterator.next();
			Object value = map.get(key);
			
			double dValue = 0;
			if(value instanceof Integer){
				dValue = ((Integer)value).doubleValue();
			}else if(value instanceof Double){
				dValue = ((Double)value).doubleValue();
			}
			
			TermMetric tm = new TermMetric(key, dValue);
			result[num] = tm;
			num++;
		}
		return result;
		
	}

}
