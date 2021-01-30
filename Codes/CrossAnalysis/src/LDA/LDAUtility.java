package LDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Document;

import org.kohsuke.args4j.CmdLineParser;

import preprocess.StanfordLemmatizer;
import utility.FileUtil;
import utility.TextUtil;

public class LDAUtility {
	private static preprocess.StanfordLemmatizer slem = StanfordLemmatizer.getInstance();
	
	/**
	 * Produce the input file which is needed by LDA
	 * 
	 * @param DirOfSegmentedTexts	the directory of segmented text files 
	 * @param LDAFileURL	the output file (input of LDA)
	 */
	public static void produceLDAFile(String DirOfSegmentedTexts, String LDAFileURL){
		PrintWriter pw;
		
		try{
			pw = new PrintWriter(LDAFileURL);
			List<File> fileList = new ArrayList<File>();
			File dir = new File (DirOfSegmentedTexts);
			FileUtil.findAllTextFiles(dir, fileList);
			
			pw.println(fileList.size());
			for(File file : fileList){
				System.out.println("Read file [" + file.getName() + "]");
				String[] words = LDAUtility.retrieveArticleContentTerms(file.getAbsolutePath());
				if (words != null)
					for (String str : words) {
						pw.print(str + " ");
					}
				pw.println();
			}
			pw.close();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void produceLDAFileFromTitles(String DirOfSegmentedTexts, String LDAFileURL){
		PrintWriter pw;
		
		try{
			pw = new PrintWriter(LDAFileURL);
			List<File> fileList = new ArrayList<File>();
			File dir = new File (DirOfSegmentedTexts);
			FileUtil.findAllTextFiles(dir, fileList);
			
			pw.println(fileList.size());
			for(File file : fileList){
				System.out.println("Read file [" + file.getName() + "]");
				String[] words = LDAUtility.retrieveTitleTerms(file.getName());
				if (words != null)
					for (String str : words) {
						pw.print(str + " ");
					}
				pw.println();
			}
			pw.close();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Produce a LDA file from the specified title list
	 * 
	 * @param titleFileURL
	 * @param LDAFileURL
	 */
	public static void produceLDAFileFromTitleFile(String titleFileURL, String LDAFileURL){
		PrintWriter pw;
		
		try{
			pw = new PrintWriter(LDAFileURL);
			List<String> titleList = new ArrayList<String>();
			File titleFile = new File (titleFileURL);
			
			BufferedReader br;
			InputStreamReader isr;
			FileInputStream fis;
			
			fis = new FileInputStream(titleFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			String line = br.readLine();
			while(line != null){
				line = line.trim();
				if(line.length() > 0)
					titleList.add(line);
				line = br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
			
			pw.println(titleList.size());
			for(String title : titleList){
				String[] words = LDAUtility.retrieveTitleTerms(title);
				if (words != null)
					for (String str : words) {
						pw.print(str + " ");
					}
				pw.println();
			}
			pw.close();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void produceLDAFileFromClusteredTitles(List<Document> documentList, String LDAFileURL){
		PrintWriter pw;
		File file = new File(LDAFileURL);
		
		try{
			File dir = file.getParentFile();
			if(!dir.exists())
				dir.mkdirs();
			System.err.println("Writing file: " + file.getAbsolutePath());
			pw = new PrintWriter(LDAFileURL);
			pw.println(documentList.size());
			for(Document doc : documentList){
				String content = doc.getContent();
				System.out.println(content);
				pw.println(content);
			}
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void produceLDAFileFromTitles(List<Document> list,
			String ldaFileUrl) {
		PrintWriter pw;
		File file = new File(ldaFileUrl);
		System.err.println("Creating" + ldaFileUrl);
		
		try{
			File dir = file.getParentFile();
			if(!dir.exists())
				dir.mkdirs();
			pw = new PrintWriter(ldaFileUrl);
			
			pw.println(list.size());
			for(Document doc : list){
				String[] words = LDAUtility.retrieveTitleTerms(doc.getTitle());
				if (words != null)
					for (String str : words) {
						pw.print(str + " ");
					}
				pw.println();
			}
			pw.close();
			System.err.println(ldaFileUrl + " created!");
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Read segmented terms as an array from specified TXT file, pre-processed by the Stanford NLP tool.
	 * 
	 * @param URL
	 * @return
	 */
	public static String[] retrieveArticleContentTerms(String URL){
		StringBuffer buffer = new StringBuffer();
		String[] result = null;
		try{
			BufferedReader br;
			InputStreamReader isr;
			FileInputStream fis;
			
			fis = new FileInputStream(URL);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			String line = br.readLine();
			while(line != null){
				line = line.trim();
				if(line.length() > 0)
					buffer.append(line + " ");
				line = br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		//Do stemming and remove stop-words
		String text = buffer.toString().trim();
		List<String> strs = slem.lemmatizeAndFilterStopWord(text, true,
				true);
		
		result = new String[strs.size()];
		result = strs.toArray(result);
		return result;
	}
	
	/**
	 * Retrieve the pre-processed string array of the specified file name.
	 * Analyzed by Stanford Lemma
	 *   
	 * @param fileName
	 * @return
	 */
	private static String[] retrieveTitleTerms(String fileName) {
		String[] result = null;
		String text = null;
		int boundary = fileName.indexOf(".txt");
		
		if(boundary >= 0)
			text = fileName.substring(0, boundary);
		else
			text = fileName;
		
		//Do stemming
		List<String> strs = slem.lemmatizeAndFilterStopWord(text, true,
				true);
		
		result = new String[strs.size()];
		result = strs.toArray(result);
		return result;
	}
	
	
    /**
     * Convert the unicode of the LDA output (model-final.twords) into oriental text (Chinese)
     * 
     * @param raw
     * @param output
     */
    public static void translateLDAResult(String raw, String output, int termsPerTopic){
		BufferedReader br;
		InputStreamReader isr;
		FileInputStream fis;
		PrintWriter pw;
		
		try{
			
			fis = new FileInputStream(raw); // "D:\\Data\\Shiseido\\lefeng\\LDA\\Test5\\model-final.twords"
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			pw = new PrintWriter(output); //"D:\\Data\\Shiseido\\lefeng\\LDA\\Test5\\result.txt"
			
			String line = br.readLine();
			StringBuffer buffer = new StringBuffer();
			int termCount = 0;
			while(line != null){
				if(line.indexOf("Topic") >= 0){
					termCount = 1;
					line = br.readLine();
					continue;
				}
				
				String[] array = line.trim().split(" ");
				buffer.append(array[0] + " ");

				if(termCount == termsPerTopic){
					pw.println(buffer.toString());
					buffer = new StringBuffer();
				}
				
				termCount++;
				line = br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
			
			pw.close();
		}catch (Exception e){
			e.printStackTrace();
		}    	
    }
    
    public static Set<String> retrieveStringsFromOutput(String output){
    	Set<String> result = new HashSet<String>();
    	
    	BufferedReader br;
		InputStreamReader isr;
		FileInputStream fis;
		
		try{
			
			fis = new FileInputStream(output); // "D:\\Data\\Shiseido\\lefeng\\LDA\\Test5\\model-final.twords"
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
						
			String line = br.readLine();
			while(line != null){
				String[] array = line.trim().split(" ");
				
				for(String str:array){
					if(TextUtil.isChinese(str)){//Get the Chinese characters in each string
						result.add(str);
					}
				}
							
				line = br.readLine();
			}
			
			br.close();
			isr.close();
			fis.close();
						
		}catch (Exception e){
			e.printStackTrace();
		}    	
    	
    	return result;
    }
    
    
    public static Word[] retrieveWordsFromString(String strings){
    	if(strings == null)
    		return null;
    	
    	//TODO
    	return null;
    }
    
    public static void retrieveFeatures(String output){
    	Set<String> set = retrieveStringsFromOutput(output);
    	
    	for(String str:set){
    		Word[] words = retrieveWordsFromString(str);
    		if(words == null)
    			continue;
    		if(words.length == 2){
    			if(Filter.isNounAndAdjective(words))
    				System.err.println(str);
    			if(Filter.isFOVAndVerb(words))
    				System.err.println(str);
    			if(Filter.isNounAndFOV(words))
    				System.err.println(str);
    			if(Filter.isVerbAndNoun(words))
    				System.err.println(str);
    		}
    	}
    }
    
    /**
     * Show help message
     * 
     * @param parser
     */
    public static void showHelp(CmdLineParser parser){
    	System.out.println("LDA [options ...] [arguments...]");
    	parser.printUsage(System.out);
    }
    
    public static void main(String args[]){
    	retrieveFeatures("D:\\Data\\Hisence_TV\\LDA\\Test1\\result.txt");
    }



}
