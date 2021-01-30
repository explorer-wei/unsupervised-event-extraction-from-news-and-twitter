package console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utility.FileUtil;
import database.SQLCMD;

public class ImportNewsToDB {

	public static void main(String[] args) {
		final String newsDir = "D:\\Projects\\EventExtraction\\NewsPortals\\Reuters\\MH370";
		
		//Fetch the news file list
		List<File> fileList = new ArrayList<File>();
		FileUtil.findAllTextFiles(new File(newsDir), fileList);
		
		SQLCMD cmd = new SQLCMD();
		String sql;
		
		//Insert news into DB
		for(File file : fileList){
			String fileName = file.getName();
			String title = fileName.substring(0, fileName.lastIndexOf(".txt"));
			title = title.replaceAll("'", "");
			String body = FileUtil.readTextFromFile(file).trim();
			body = body.replaceAll("'", "");
			
			sql = "INSERT INTO twapperkeeper_2.news_mh370 (id, title, body) VALUES (NULL, '" + title + "', '" + body + "')";
			cmd.executeUpdate(sql);
		}
		
		cmd.closeConnection();
	}

}
