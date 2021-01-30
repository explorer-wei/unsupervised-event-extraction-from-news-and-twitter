package model;

public class NamedEntity {
	private String term;

	private String type;
	
	public NamedEntity(String term, String type) {
		super();
		this.term = term;
		this.type = type;
	}
	
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
