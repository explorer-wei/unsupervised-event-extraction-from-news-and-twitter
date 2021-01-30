package encoding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class EncodingUtil {
	
	/**
	 * Convert the specified file from one encoding format to the other
	 * 
	 * @param fromURL
	 * @param toURL
	 * @param fromCode
	 * @param toCode
	 */
	public static void convertEncoding(String fromURL, String toURL, String fromCode, String toCode){
		BufferedReader br;
		InputStreamReader isr;
		FileInputStream fis;
		PrintWriter pw;
		
		try{
			fis = new FileInputStream(fromURL);
			isr = new InputStreamReader(fis, fromCode);
			br = new BufferedReader(isr);
			
			StringBuffer buffer = new StringBuffer();
			String line = br.readLine();
			while(line != null){
				buffer.append(line + "\n");
				line = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
			
			pw = new PrintWriter(toURL, toCode);
			pw.print(buffer.toString());
			pw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
