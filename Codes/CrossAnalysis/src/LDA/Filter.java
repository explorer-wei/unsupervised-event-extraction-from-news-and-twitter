package LDA;

public class Filter {
	
	public static boolean isNounAndAdjective(Word[] words){
		if(words[0].getPos().equals("����") && words[1].getPos().equals("���ݴ�"))
			return true;
		return false;
	}
	
	public static boolean isFOVAndVerb(Word[] words){
		if(words[0].getPos().equals("��ν��") && words[1].getPos().equals("����"))
			return true;
		return false;
	}
	
	public static boolean isNounAndFOV(Word[] words){
		if(words[0].getPos().equals("����") && words[1].getPos().equals("��ν��"))
			return true;
		return false;
	}
	
	public static boolean isVerbAndNoun(Word[] words){
		if(words[0].getPos().equals("����") && words[1].getPos().equals("����"))
			return true;
		return false;
	}

}
