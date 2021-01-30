import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  * Based on "line block distribution function", no customized rules for particular site
 *  @author Wei
 *  @Modified 02/26/2014
 */
public class TextExtract {
	
	private List<String> lines;
	private final static int blocksWidth=3;
	private int threshold;
	private String html;
	private int start;
	private int end;
	private StringBuilder text;
	private ArrayList<Integer> indexDistribution;
	
	public TextExtract(int _th) {
		lines = new ArrayList<String>();
		indexDistribution = new ArrayList<Integer>();
		text = new StringBuilder();
		threshold = _th;
	}	

	/**
	 * Extract the main content of given html file
	 * @param _html String for HTML page  
	 * @return <String> main content of this webpage 
	 * @throws IOException 
	 */

	public void parse(String _html) throws IOException {
		html = _html.replaceAll("\r\n", "\n");
		String title =  getTitle(html);
		html = preProcess(html);
		
		//Create .txt file and output the result
		String content = getText();
		if (content.replaceAll("\\s+", "").length() > 10) {
			String filename = title.replaceAll("[^a-zA-Z_0-9.']+", " ");
			filename = filename.replaceAll("\\s+", " ");
			if(filename.length() > 200)
				filename = filename.substring(0, 199);
			File file = new File("d://news//"+filename+".txt");
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file,true)); 
			bw.write("Title: " + title + "\r\n" + content);
			bw.close();
			
			System.out.println("Output " + title);
		}
	}
	
	private static String preProcess(String source) {
		// remove common html tags
		source = source.replaceAll("(?is)<!--.*?-->", "");
		source = source.replaceAll("(?is)<!DOCTYPE(.|\n)*?>", "");
		source = source.replaceAll("(?is)<[hH][tT][mM][lL] [xX][mM][lL][nN][sS].*?>", ""); 
		source = source.replaceAll("(?is)<[sS][tT][yY][lL][eE].*?>.*?</[sS][tT][yY][lL][eE]>", "");
		source = source.replaceAll("(?is)<[sS][cC][rR][iI][pP][tT].*?>.*?</[sS][cC][rR][iI][pP][tT]>", "");		
		source = source.replaceAll("(?is)<[iI][nN][pP][uU][tT].*?>", " ");
		source = source.replaceAll("(?is)<[fF][oO][rR][mM].*?>.*?</[fF][oO][rR][mM]>", " ");
		source = source.replaceAll("(?is)<[aA].*?>", " ");
		source = source.replaceAll("(?is)<[lL][iI][nN][kK].*?>", " ");
		source = source.replaceAll("(?is)<[fF][rR][aA][mM][eE].*?>", " ");
		source = source.replaceAll("(?is)<[bB][oO][dD][yY].*?>", " "); 
		source = source.replaceAll("(?is)<[dD][iI][vV].*?>", " ");
		source = source.replaceAll("(?is)<[iI][mM][gG].*?>", " "); 
		source = source.replaceAll("(?is)<[aA][rR][eE][aA].*?>", " ");
		source = source.replaceAll("(?is)<[tT][aA][bB][lL][eE].*?>", " ");
		source = source.replaceAll("(?is)<[mM][eE][tT][aA].*?>", " ");
		source = source.replaceAll("(?is)<[aA].*?>", " ");
		source = source.replaceAll("(?is)<[pP].*?>", "\n");
		source = source.replaceAll("(?is)<[bB][rR].*?>", "\n");
		source = source.replaceAll("&.{2,5};|&#.{2,5};", " ");	// remove special char		
		// remove span, which very likely contain hyperlink-paragraph (usually ads)
		source = source.replaceAll("<[sS][pP][aA][nN](.|\n)*?>", "");
		source = source.replaceAll("</[sS][pP][aA][nN]>", "");
		// remove other tags
		source = source.replaceAll("<(.|\n)*?>", " ");

		return source;
	}
	
	private static String getTitle(String _html) {
		String title = "";
		Pattern pa = Pattern.compile("<title>((.|\n)*?)</title>", Pattern.CASE_INSENSITIVE);
		Matcher ma = pa.matcher(_html);
		if(ma.find()) {
			title = ma.group(1);
		}
		System.out.println("Title: " + title);
		return title;
	 }
	
	private String getText() {
		lines = Arrays.asList(html.split("\n"));
		indexDistribution.clear();
		int empty = 0;// number of empty lines
		for (int i = 0; i < lines.size() - blocksWidth; i++) {			
			int wordsNum = 0;// number of words in each "block"
			for (int j = i; j < i + blocksWidth; j++) {
				wordsNum += lines.get(j).replaceAll("\\s+", "").length();
			}
			indexDistribution.add(wordsNum);

			if (lines.get(i).replaceAll("\\s+", "").length() == 0)
			{
				empty++;
			}
		}
		System.out.println("Empty Line = " + empty);
		System.out.println("lines.size = " + lines.size());
		
		int sum = 0, max = 0;
		for (int i=0; i< indexDistribution.size(); i++) {
			sum += indexDistribution.get(i);
			max = Math.max(max, indexDistribution.get(i));
		}
		
		if(threshold == -1) {
//			threshold = (sum/indexDistribution.size())<<(empty/(lines.size()-empty)>>>1);
			threshold = sum*empty/indexDistribution.size()/(lines.size()-empty);
			threshold = Math.max( Math.max(threshold, max/2), 100);
			threshold = Math.min( Math.min(threshold, max), 300);
		}
		System.out.println("Sum = " + sum);
		System.out.println("Max = " + max);
		System.out.println("indexDistribution.size = " + indexDistribution.size());
		System.out.println("Threshold = " + threshold);
		
		start = -1; end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indexDistribution.size() - 1; i++) {

			if (indexDistribution.get(i) > threshold && ! boolstart) {
				if (indexDistribution.get(i+1).intValue() != 0 
					|| indexDistribution.get(i+2).intValue() != 0
					|| indexDistribution.get(i+3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0 
					|| indexDistribution.get(i+1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}		
			if (boolend) {
				buffer.setLength(0);
				for (int ii = start; ii <= end; ii++) {
					if (lines.get(ii).replaceAll("\\s+", "").length() < 5) continue;					
					buffer.append(lines.get(ii).replaceAll("\\s{1,}", " ") + "\r\n");
				}
				String str = buffer.toString();
				text.append(str);
				boolstart = boolend = false;
			}
		}		
		if (start > end)
		{
			buffer.setLength(0);
			int size_1 = lines.size()-1;
			for (int ii = start; ii <= size_1; ii++) {
				if (lines.get(ii).replaceAll("\\s+", "").length() < 5) continue;
				buffer.append(lines.get(ii) + "\r\n");
			}
			String str = buffer.toString().replaceAll("\\s{1,}", " ");
			text.append(str);
		}		

		return text.toString();
	}
	
	public static void main(String[] args)
	{
		String s = "<img  class='fit-image' onload='javascript:if(this.width>498)this.width=498;' />hello";
		System.out.println(TextExtract.preProcess(s));
	}

}