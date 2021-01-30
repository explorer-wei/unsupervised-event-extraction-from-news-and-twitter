package utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression {
	public static boolean hasTripleRepeatedLetters (String str){
		Pattern pattern = Pattern.compile("([a-z])\\1{2}");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
	}
	
	public static void main(String[] args)
    {
//        Pattern pattern = Pattern.compile("([a-z])\\1{2}");
//        Matcher matcher = pattern.matcher("ffuunnn");
//        System.out.println(matcher.find());
		System.out.println(hasTripleRepeatedLetters("ffuuunnnn"));
    }

}
