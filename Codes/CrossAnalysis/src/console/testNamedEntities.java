package console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import utility.FileUtil;
import ner.NERUtil;

/** 
 * 
 * @author Wei Huang
 * 4/11/2014
 */

public class testNamedEntities {
	
	public static void main(String args[]){
		
		final File sourceDirFile = new File("D:\\Test\\Apple3days");
		String dest = "D:\\Test\\NamedEnties.txt";
		List<File> list = new ArrayList<File>();
		FileUtil.findAllTextFiles(sourceDirFile, list);
		System.out.println("Got File List:" + list.size());
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(dest);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (File file : list){
			
			String inputFile = file.getAbsolutePath();
			System.out.println("Title: " + file.getName());
					
			try {
				NERUtil.extractNamedEntities(inputFile,pw);
				} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println();
		}
		
		pw.close();
	}
}