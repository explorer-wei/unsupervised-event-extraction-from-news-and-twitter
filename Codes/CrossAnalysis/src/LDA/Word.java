package LDA;

public class Word {
	private String text;
	private String pos;
	
	public Word(String text, String pos) {
		super();
		this.text = text;
		this.pos = pos;
	}
	
	public Word(){
		this.text = null;
		this.pos = null;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}
}
