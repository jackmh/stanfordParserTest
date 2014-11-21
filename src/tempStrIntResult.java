import java.util.List;

import edu.stanford.nlp.ling.HasWord;


public class tempStrIntResult {
	
	private String newString;
	private List<HasWord> sentenceList;
	private int Count;
	
	public tempStrIntResult() {
		// TODO Auto-generated constructor stub
		newString = "";
		Count = 0;
		sentenceList.clear();
	}
	
	public tempStrIntResult (String tmp, int num) {
		newString = tmp;
		Count = num;
	}
	
	public String getStr () {
		return newString;
	}
	
	public int getIntNumber() {
		return Count;
	}
	
	public void setStr(String newStr) {
		newString = newStr;
	}
	
	public void setInt(int num) {
		Count = num;
	}
}
