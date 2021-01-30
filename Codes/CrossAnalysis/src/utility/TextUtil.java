package utility;

import java.util.Arrays;

public class TextUtil {
	
	public static boolean isChinese(char a) {
		int v = (int) a;
		if(v == 65279)//Filter the character "\ufeff"
			return false;
		return (v >= 19968 && v <= 171941);
	}
	
	/**
	 * Check whether the specified string contains non-Chinese character
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChinese(String str){
		if(str == null)
			return false;
		char[] array = str.toCharArray();
		for(char c:array){
			if(!isChinese(c))
				return false;
		}
		
		return true;
	}
	
	/**
     * Check whether the string is a number
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)
    {
    	try{
    		//Check whether there's any latin characters in the string
    		//If no, consider it as numeric string
    		char[] array = str.toCharArray();
    		boolean hasChar = false;
    		for(char c : array){
    			int iVal = (int)c;
    			if((iVal > 64 && iVal < 91) || (iVal > 96 && iVal < 123)){
    				hasChar = true;
    				break;
    			}
    		}
    		if(!hasChar)
    			return true;
    		
    		boolean result = str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    		return result;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Check whether the string is a punctuation
     * 
     * @param str
     * @return
     */
    public static boolean isPunctuation(String str){
    	if(str == null)
    		return false;
    	if(str.length() != 1)
    		return false;
    	int symbol = str.charAt(0);
    	
    	//If the symbol is letter (up case or low case), or number, return false
    	if((symbol >47 && symbol < 58) || (symbol > 64 && symbol < 91) || (symbol > 96) && (symbol < 123))
    		return false;
    	else//Otherwise,return true
    		return true;
    }
    
    /**
     * Check whether the specified string has no less than the specified number of numerical digits
     * 
     * @param str
     * @param number
     * @return
     */
    public static boolean hasNumDigits(String str, int number){
    	try{
    		int num = 0;
    		//Check whether there's any numerical characters in the string
    		char[] array = str.toCharArray();
    		for(char c : array){
    			int iVal = (int)c;
    			if((iVal > 47 && iVal < 58)){
    				num++;
    			}
    		}
    		
    		if(num >= number)
    			return true;
    		else
    			return false;
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Get the integer value of the specified string
     * 
     * @param str
     * @return
     */
    public static String getIntValueOfString(String str){
    	if(str == null)
    		return null;
    	
    	StringBuffer buffer = new StringBuffer();
    	char[] array = str.toCharArray();
		for(char c : array){
			int iVal = (int)c;
			buffer.append(iVal + "/");
		}
		
		return buffer.toString();
    	
    }
    
    /**
     * Check whether the specified string contains invalid character
     * The valid characters are letters (both upper and lower), numbers, and some special punctuation
     * ("_", ";", ".", ":")
     * @param str
     * @return
     */
    public static boolean hasInvalidCharacter(String str){
    	if(str == null)
    		return false;
    	
    	char[] array = str.toCharArray();
		for(char c : array){
			int iVal = (int)c;
			if(!((iVal > 47 && iVal < 58) || 
					(iVal > 64 && iVal < 91) ||
					(iVal > 96 && iVal < 123) ||
					iVal == 46 || iVal == 58 ||
					iVal == 59 || iVal == 95)){
				return true;
			}
		}
		return false;
    }
    
    /**
     * Shorten the string with at least 3 repeated letters to a curtailed one
     * e.g. "fuuuuunnnnnnn" -> "fun"
     * @param str
     * @return
     */
    public static String removeDuplicateLetters(String str){
    	if(str == null)
    		return null;
    	
    	if(str.length() < 4)
    		return str;
    	
    	char[] array = str.toCharArray();
		for(int i=0;i<=array.length-4;i++){
			char c1 = array[i];
			char c2 = array[i+1];
			char c3 = array[i+2];
			
			//Check whether there at least 3 successive repeated letters
			if(c2 != c1 || c3 != c1 )
				continue;
			
			String curtailed = null;
			for(int j=i+3;j<array.length;j++){
				//Looking for the next character which is different from the c1
				if(array[j] != c1){
					curtailed = str.substring(0, i+1) + str.substring(j);
					break;
				}
			}
			
			//If all the letters behind c1 is duplicate
			if(curtailed == null)
				curtailed = str.substring(0, i+1);
			
			//Make a recursive call if there're still more than 3 repeated letters in the curtailed string
			if(RegularExpression.hasTripleRepeatedLetters(curtailed))
				return removeDuplicateLetters(curtailed);
			else
				return curtailed;
		}
		
		return str;
    }
    
    /**
     * Find the index of the specified target in the specified array
     *  
     * @param array
     * @param target
     * @return
     */
    public static int binarySearch(String[] array, String target){
    	if(array == null || target == null){
    		System.err.println("Null pointer during binary search!");
    		return -1;
    	}
    	
    	return Arrays.binarySearch(array, target);
    }
    
    public static void main(String args[]){
/*    	Set<String> set = new TreeSet<String>();
    	set.add("addict");
    	set.add("add");
    	set.add("additional");
    	set.add("addicted");
    	set.add("binary");
    	
    	
    	String[] array = new String[set.size()];
    	array = set.toArray(array);
    	for(String str : array){
    		System.out.println(str);
    	}
    	
    	System.out.println(binarySearch(array,"addict"));*/
    	String sample = "fffuuuuun";
    	System.out.println(removeDuplicateLetters(sample));
    }

}
