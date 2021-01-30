package utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Ansi2Utf8 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String inputFileDir="D:\\Ddisk\\all";
		String outFileDir="D:\\Ddisk\\new";
		File fs = new File(inputFileDir);
		if (fs.isDirectory()) {
			File[] fn = fs.listFiles();

			for(int i=0;i<fn.length;i++){
				transferFile(fn[i].getAbsolutePath(),outFileDir+File.separator+fn[i].getName());
			}
			
		}
	}
	private static void transferFile(String srcFileName, String destFileName) throws IOException {
		  String line_separator = System.getProperty("line.separator"); 
		  FileInputStream fis = new FileInputStream(srcFileName);
		  StringBuffer content = new StringBuffer();
		  DataInputStream in = new DataInputStream(fis);
		  BufferedReader d = new BufferedReader(new InputStreamReader(in, "GBK"));// , "UTF-8"  
		  String line = null;
		  while ((line = d.readLine()) != null)
		   content.append(line + line_separator);
		  d.close();
		  in.close();
		  fis.close();
			Writer ow = new OutputStreamWriter(new FileOutputStream(
					destFileName), "utf-8");
		       
//		  Writer ow = new OutputStreamWriter(new FileOutputStream(destFileName), "utf-8");
		  ow.write(content.toString());
		  ow.close();
		 }
}
