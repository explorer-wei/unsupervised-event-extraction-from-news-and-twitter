package LDA;

public class Filter {
	
	public static boolean isNounAndAdjective(Word[] words){
		if(words[0].getPos().equals("名词") && words[1].getPos().equals("形容词"))
			return true;
		return false;
	}
	
	public static boolean isFOVAndVerb(Word[] words){
		if(words[0].getPos().equals("形谓词") && words[1].getPos().equals("动词"))
			return true;
		return false;
	}
	
	public static boolean isNounAndFOV(Word[] words){
		if(words[0].getPos().equals("名词") && words[1].getPos().equals("形谓词"))
			return true;
		return false;
	}
	
	public static boolean isVerbAndNoun(Word[] words){
		if(words[0].getPos().equals("动词") && words[1].getPos().equals("名词"))
			return true;
		return false;
	}

}
