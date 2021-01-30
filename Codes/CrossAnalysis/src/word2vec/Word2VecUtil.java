package word2vec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import constants.GeneralConstants;

public class Word2VecUtil {
	/**
	 * For every item in the list1, get the most similar item from list2, and return them in a list
	 *  
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<Set<String>> getRelevantWordSet(List<Set<String>> list1, List<Set<String>> list2){
		List<Set<String>> result = new ArrayList<Set<String>>();
		Word2VEC wv = new Word2VEC();
		try {
			wv.loadModel(GeneralConstants.WORD2VEC_MODEL_PATH);
			for(Set<String> set : list1){
				float minDistance = 0;
				int flag = 0;
				String[] array1 = new String[set.size()];
				array1 = set.toArray(array1);
				
				for(int i=0;i<list2.size();i++){
					Set<String> item = list2.get(i);
					String[] array2 = new String[item.size()];
					array2 = item.toArray(array2);
					float distance = wv.arrayDistance(array1, array2);
					if(distance > minDistance){
						minDistance = distance;
						flag = i;
						System.err.println("The distance of item " + flag + " is even closer!");
					}
				}
				result.add(list2.get(flag));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

}
