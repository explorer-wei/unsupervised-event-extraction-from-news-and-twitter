package model;

import java.util.Date;
import java.util.List;

public class Document implements Cloneable {

	private String title;

	private String content;
	
	private List<String> paragraphs;

	private Date date;

	private String path;

	private int categoryID;

	private String categoryStr;

	private int[] termFrequencies;

	public Document(String title, String content, Date date, String path) {
		super();
		this.title = title;
		this.content = content;
		this.date = date;
		this.path = path;
	}
	
	public Document(String title, List<String> paragraphs, String path){
		this.title = title;
		this.paragraphs = paragraphs;
		this.path = path;
	}
	
	public Document(String title, String content, String path){
		this.title = title;
		this.content = content;
		this.path = path;
	}

	public Document(String title, String content, String path,
			String categoryStr) {
		this.title = title;
		this.content = content;
		this.path = path;
		this.categoryStr = categoryStr;
	}
	
	public Document(String title, String content, String path,
			int categoryID) {
		this.title = title;
		this.content = content;
		this.path = path;
		this.categoryID = categoryID;
	}

	public Document(String title, String content, Date date, String path,
			int categoryID, String categoryStr, int[] termFrequencies) {
		super();
		this.title = title;
		this.content = content;
		this.date = date;
		this.path = path;
		this.categoryID = categoryID;
		this.categoryStr = categoryStr;
		this.termFrequencies = termFrequencies;
	}

	public String getCategoryStr() {
		return categoryStr;
	}

	public void setCategoryStr(String categoryStr) {
		this.categoryStr = categoryStr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public List<String> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<String> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public int[] getTermFrequencies() {
		return termFrequencies;
	}

	public void setTermFrequencies(int[] termFrequencies) {
		this.termFrequencies = termFrequencies;
	}

	public Document clone() {
		return new Document(this.title, this.content, this.date, this.path,
				this.categoryID, this.categoryStr, this.termFrequencies);
	}

}
