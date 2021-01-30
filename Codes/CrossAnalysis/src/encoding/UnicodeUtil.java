package encoding;



public class UnicodeUtil {
	
	/**
	 * Convert plain text to unicode
	 * 
	 * @param s
	 * @return
	 */
	public static String toUnicode(final String s) {
		String s1 = "";
		String trimed = s.trim();
		for (int i = 0; i < s.length(); i++) {
			s1 += "\\u" + Integer.toHexString(trimed.charAt(i) & 0xffff);
		}
		return s1;
	}
	
	/**
	 * Convert unicode to oriental language (e.g.Chinese)
	 * @param dataStr
	 * @return
	 */
    public static String decodeUnicode(final String dataStr) {  
        int start = 0;  
        int end = 0;  
        final StringBuffer buffer = new StringBuffer();  
        while (start > -1) {  
            end = dataStr.indexOf("\\u", start + 2);  
            String charStr = "";  
            if (end == -1) {  
                charStr = dataStr.substring(start + 2, dataStr.length());  
            } else {  
                charStr = dataStr.substring(start + 2, end);  
            }  
            char letter = (char) Integer.parseInt(charStr, 16); 
            buffer.append(new Character(letter).toString());  
            start = end;  
        }  
        return buffer.toString();  
    } 
    

	
	public static void main(String args[]){
		String str = "\\ufeff\\u7528";
		System.err.println(UnicodeUtil.decodeUnicode(str));
		
	}
}
