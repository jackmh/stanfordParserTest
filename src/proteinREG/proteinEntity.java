package proteinREG;

/**
 * @author jack_mhdong
 * 在识别蛋白质是否存在于句子时，我们存储此 蛋白质的全称 和 全称包含单词长度
 */
public class proteinEntity {
	
	private String proteinFullName;
	private int Count;
	
	public proteinEntity() {
		// TODO Auto-generated constructor stub
		proteinFullName = "";
		Count = 0;
	}
	
	public proteinEntity (String tmp, int num) {
		proteinFullName = tmp;
		Count = num;
	}
	
	public String getStr () {
		return proteinFullName;
	}
	
	public int getIntNumber() {
		return Count;
	}
	
	public void setStr(String newStr) {
		proteinFullName = newStr;
	}
	
	public void setInt(int num) {
		Count = num;
	}
}
