package utility;

import java.util.Arrays;

public class MathUtil {
	public static double log(double parameter, double base){
		
		if (parameter == 0)
			return 0;
		else
			return Math.log(parameter) / Math.log(base);
	}
	
	/**
	 * Get the median of the specified array
	 * 
	 * @param dbArray
	 * @return
	 */
	public static double median (final double[] dbArray){
		double[] array = new double[dbArray.length];
		System.arraycopy(dbArray, 0, array, 0, dbArray.length);
		
		Arrays.sort(array);
		double median;
		if (array.length % 2 == 0)
		    median = (array[array.length/2] + array[array.length/2+1])/2;
		else
		    median = array[array.length/2];
		
		return median;
	}
}
