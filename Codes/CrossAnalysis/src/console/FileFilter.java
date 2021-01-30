package console;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import utility.FileUtil;

public class FileFilter {
	public static final String[] keywords = {"Ukraine", "Kiev", "Crimea", "Yanukovych", 
		"Tymoshenko", "Turchynov", "Russia", "Putin", "Crisis", "Military", "NATO",
		"sanctions", "Troop", "protesters", "Protests", "separatists"};
	
	
	public static void main(String[] args) {
		String input = "D:\\Test\\UkraineCrisis";
		String output = "D:\\Test\\Filtered";
		File inputFile = new File(input);
		
		// Get the file list
		List<File> fileList = new ArrayList<File>();
		FileUtil.findAllTextFiles(inputFile, fileList);
		int total = fileList.size();
		
		int filterNum = 0;
		DecimalFormat df = new DecimalFormat("0.00");
		// For every file, split its contents into paragraphs, check how many percent of paragraphs contains any of above keywords
		// For the files which has less than 0.25, move them to a specified directory 
		for(File file : fileList){
			List<List<String>> paragraphList = FileUtil.splitDocumentIntoParagraphs(file, 0);
			
			int numOfQualified = 0;
			//For every paragraph, check whether it contains any of above keywords
			for(List<String> paragraph : paragraphList){
				int match = FileUtil.numberOfMatchedWord(paragraph, keywords);
				if(match > 0)
					numOfQualified++;
			}
			
			double rate = ((double)numOfQualified) / paragraphList.size();
			
			if(rate < 0.25){
				filterNum++;
				System.out.println("Rate: <" + df.format(rate) + "> File [" + file.getName() + "] filtered!");
				FileUtil.moveFile(file, output);
			}
		}
		
		System.err.println(filterNum + " / " + total + " files filtered to " + output);

	}

}
