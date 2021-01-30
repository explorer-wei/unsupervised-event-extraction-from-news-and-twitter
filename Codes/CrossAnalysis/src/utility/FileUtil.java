package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import console.TFIDF;
import preprocess.StanfordLemmatizer;
import utility.OSCheck.OSType;
import model.Document;

public class FileUtil {
	public static void findAllTextFiles(File dir, List<File> list) {
		if (dir == null || !dir.isDirectory())
			return;
		File[] array = dir.listFiles();
		if (array == null || array.length == 0)
			return;
		for (File item : array) {
			if (item.isFile()) {
				String fileName = item.getName();
				item.getAbsolutePath();
				if (fileName != null && fileName.endsWith(".txt"))
					list.add(item);
			} else
				findAllTextFiles(item, list);
		}
	}
	
	/**
	 * Split the specified document into paragraphs and return a list, where every entry is a paragraph (list of strings)
	 * 
	 * @param file
	 * @param skipTopLines
	 * @return
	 */
	public static List<List<String>> splitDocumentIntoParagraphs(File file, int skipTopLines){
		List<List<String>> result = new LinkedList<List<String>>();
		
		preprocess.StanfordLemmatizer stem = StanfordLemmatizer.getInstance();
		String content = FileUtil.readTextFromFile(file);
		String[] array = content.split("\n");

		//If there're too few lines in the document, skip it
		if(array.length <= skipTopLines)
			return result;
		
		int count = 1;
		for (String text : array) {
			if (text.trim().length() == 0)
				continue;
			
			//Skip the top N lines (usually the top 3 lines are the topic, time and agency of the news)
			if(count <= skipTopLines){
				count++;
				continue;
			}
			
			List<String> strs = stem.lemmatize(text);
			result.add(strs);
			count++;
		}
		
		return result;
	}

	public static void splitDocumentsIntoParagraphs(List<Document> list,
			String outputDir) {
		if (list == null || outputDir == null)
			return;

		File outputFile = new File(outputDir);
		boolean flag = false;
		if (!outputFile.exists()) {
			flag = outputFile.mkdirs();
			if (!flag) {
				System.err.println("Failed to create output directory!");
				return;
			}
		}

		for (Document doc : list) {
			String title = doc.getTitle();
			String content = doc.getContent();
			String[] array = content.split("\n");

			int count = 0;
			for (String text : array) {
				if (text.trim().length() == 0)
					continue;
				// the name of the file is like title-001.txt
				String paraFileUrl = outputDir + File.separator + title + "-"
						+ String.format("%03d", count) + ".txt";
				printTextToFile(text, paraFileUrl);
				count++;
			}

		}
	}
	

	public static void splitDocumentsIntoParagraphs(String sourceDir,
			String outputDir, boolean stemming, int skipTopLines) {
		if (sourceDir == null || outputDir == null)
			return;

		preprocess.StanfordLemmatizer slem = StanfordLemmatizer.getInstance();

		File outputFile = new File(outputDir);
		boolean flag = false;
		if (!outputFile.exists()) {
			flag = outputFile.mkdirs();
			if (!flag) {
				System.err.println("Failed to create output directory!");
				return;
			}
		}

		File sourceDirFile = new File(sourceDir);
		List<File> list = new ArrayList<File>();
		findAllTextFiles(sourceDirFile, list);

		for (File file : list) {
			String title = file.getName();
			String content = readTextFromFile(file);
			Date date = getNewsDate(file);
			String strDate = dateToString(date);
			String[] array = content.split("\n");

			//If there're too few lines in the document, skip it
			if(array.length <= skipTopLines)
				continue;
			
			int count = 1;
			for (String text : array) {
				if (text.trim().length() == 0)
					continue;
				
				//Skip the top N lines (usually the top 3 lines are the topic, time and agency of the news)
				if(count <= skipTopLines){
					count++;
					continue;
				}
				
				// the name of the file is like title-001.txt
				String paraFileUrl = outputDir + File.separator + title + "-"
						+ String.format("%03d", count) + ".txt";

				if(stemming){
					// Do stemming to the text before writing them to files.
					StringBuffer buffer = new StringBuffer();
					if(strDate != null)//Append the date information to the paragraph
						buffer.append("Date: " + strDate + ".. ");
					List<String> strs = slem.lemmatizeAndFilterStopWord(text, true,
							true);
					for (String item : strs) {
						buffer.append(item + " ");
					}

					printTextToFile(buffer.toString().trim(), paraFileUrl);
	
				}else{
					if(strDate != null)//Append the date information to the paragraph
						text = "Date: " + strDate + ".. " + text;
					printTextToFile(text, paraFileUrl);
				}
				
				count++;
			}

		}
	}
	
	/**
	 * Find the matching cases in the specified string list and array
	 * 
	 * @param list
	 * @param array
	 * @return
	 */
	public static int numberOfMatchedWord(List<String> list, String[] array){
		int i = 0;
		for(String item : list){
			for(String str : array){
				if(str.equalsIgnoreCase(item.trim()))
						i++;
			}
		}
		
		return i;
	}
	
	/**
	 * Extract the news creation date according to its path in the file system
	 * e.g.D:\Test\MH370\2014\3\8\Angry Chinese families want more information on missing Malaysia Airlines flight.txt
	 * @param file
	 * @return
	 */
	public static Date getNewsDate(File file){
		if(file == null || !file.exists() || file.isDirectory())
			return null;
		
		String path = file.getAbsolutePath();
		OSType osType = OSCheck.getOperatingSystemType();
		String[] array;
		
		if(osType == OSType.Windows)
			array = path.split("\\\\");
		else
			array = path.split("/");
		int length = array.length;
		
		Calendar calendar = Calendar.getInstance();
		try{
			int day = Integer.parseInt(array[length-2]);
			int month = Integer.parseInt(array[length-3]);
			int year = Integer.parseInt(array[length-4]);
			
			calendar.set(year, month-1, day);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return calendar.getTime();
	}
	
	public static String dateToString(Date date){
		if(date == null)
			return null;
		
		DateFormat df = DateFormat.getDateInstance();
		return df.format(date);
	}

	public static void printTextToFile(String text, String url) {
		if (text == null || url == null)
			return;

		try {
			PrintWriter pw = new PrintWriter(url);
			pw.print(text);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readTextFromFile(File file) {
		StringBuffer buffer = new StringBuffer();
		
		char[] array = new char[4*1024];
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int num = br.read(array);
			
			while (num > 0) {
				buffer.append(array);
				array = new char[4*1024];
				num = br.read(array);
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
			return buffer.toString();
		}

		return buffer.toString();
	}
	
	public static void writeFileNamesToFile(String dir, String dest){
		if(dir == null || dest == null){
			System.err.println("NULL point happens in FileUtil.writeFileNamesToFile");
			return;
		}
		
		try {
			File rootFile = new File(dir);
			List<File> list = new ArrayList<File>();
			
			findAllTextFiles(rootFile, list);
			
			PrintWriter pw = new PrintWriter(dest);
			for(File file : list){
				String fName = file.getName();
				String newsTitle = fName.substring(0, fName.lastIndexOf(".txt"));
				pw.println(newsTitle);	
			}
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Regarding all TXT files in a directory, write the title and all relevant paragraphs of each file to the specified TXT file
	 * 
	 * @param dir
	 * @param dest
	 * @param skipTopLines
	 */
	public static void writeRelevantParagraphsToFile(String dir, String dest, int skipTopLines){
		if(dir == null || dest == null){
			System.err.println("NULL point happens in FileUtil.writeFileNamesToFile");
			return;
		}
		
		try {
			List<Document> docList = TFIDF.getRelevantParagraphs(dir, skipTopLines);
			PrintWriter pw = new PrintWriter(dest);

			for(Document doc : docList){
				String title = doc.getTitle();
				pw.print(title + " ");
				List<String> paragraphList = doc.getParagraphs();
				for(String paragraph : paragraphList){
					pw.print(paragraph + " ");
				}
				pw.println();
			}
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Clean up all the files/sub-directories under the specified directory
	 * @param rootDir
	 */
	public static void emptyDirectory (File rootDir){
		if(rootDir == null)
			return;
		
		File[] array = rootDir.listFiles();
		if(array == null || array.length == 0)
			return;
		
		for(File file : array){
			if(file.isDirectory())
				emptyDirectory(file);
			else
				file.delete();
		}
	}
	
	/**
	 * Move a file to the specified directory
	 * 
	 * @param file
	 * @param dest
	 * @return
	 */
	public static boolean moveFile(File file, String dest){
		if(file == null || dest == null){
			System.err.println("NULL point happens in FileUtil.moveFile");
			return false;
		}
		
		if(!file.exists() || file.isDirectory()){
			System.err.println("The input file not exists or is a directory");
			return false;
		}
		
		File outputDir = new File(dest);
		boolean flag = true;
		if(!outputDir.exists())
			flag = outputDir.mkdirs();
		
		if(!flag){
			System.err.println("Failed to create a directory for the output file");
			return false;
		}
		
		//Clean the output directory
		//emptyDirectory(outputDir);
		
		String fileName = file.getName();
		String destPath = dest + File.separator + fileName;
		
		File destFile = new File(destPath);
		PrintWriter pw = null;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(file));
			pw = new PrintWriter(destFile);
			
			String line = br.readLine();
			while(line != null){
				pw.println(line);
				line = br.readLine();
			}
			br.close();
			pw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
			
		
		return file.delete();
	}
	
	/**
	 * Read URL list from the configuration file
	 * 
	 * @param location
	 * @return
	 */
	public static Map<String, String> readKeyValueList(String location) {
		Map<String, String> result = new TreeMap<String, String>();
		if (location == null || location.length() <= 0) {
			System.err.println("NULL Company code list!");
			return result;
		}

		try {
			File file = new File(location);
			if(!file.exists()){
				System.err.println("Failed to read configuration! Please put the 'config.properties' file and 'company-code.txt' under installation directory!");
				System.exit(0);
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				if (line != null && line.indexOf(':') > 0) {
					String[] array = line.split(":", 2);
					result.put(array[0].trim(), array[1].trim());
				}
				line = br.readLine();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
}
