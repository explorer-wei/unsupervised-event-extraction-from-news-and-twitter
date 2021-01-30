package htmlparser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main function to call TextExtractor
 * @author Wei Huang
 * @Modified 02/26/2014
 */

public class UseDemo {
	
	public static void main(String[] args) throws IOException{
		List<File> list = new ArrayList<File>();
		File rootDir = new File("D://ARCHIVEIT"); // ARCHIVEIT//http, TestSet
		findAllHTMLFiles(rootDir, list);
		System.out.println("Got File List:" + list.size());
		
		for (File item : list){
			
			String fullPath = item.getAbsolutePath();
			String content = new UseDemo().getHTML(item);
			System.out.println("Read " + fullPath);
			
			try {
				/* larger threshold means smaller recall rate and usually make accuracy improved
				   small threshold causes greater noise, but can extract short content */
				int threshold = -1; //default: -1; can also try: 300 
				new TextExtract(threshold).parse(content);
				System.out.println("Page Complete");		
				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		System.out.println("TextExtract Complete");
	}
	
	public String getHTML(File file) throws IOException {
		StringBuffer abstr = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String temp="";
		while((temp=reader.readLine())!=null){
			abstr.append(temp);
			abstr.append("\n");
		}
		reader.close();
		String html =abstr.toString(); // here you get all the string within your file.
		return html;
		}
	
	// Obtain all the .html files under the root folder
	public static void findAllHTMLFiles(File dir, List<File> list){
		if (dir == null || !dir.isDirectory())
			return;
		File[] array = dir.listFiles();
		if(array == null || array.length == 0)
			return;
		for(File item : array){
			if(item.isFile()){
				String fileName = item.getName();
				item.getAbsolutePath();
				if(fileName != null && (fileName.endsWith(".html") || fileName.endsWith(".htm")))
					list.add(item);
				}else 
					findAllHTMLFiles(item,list);
			}
		}
}