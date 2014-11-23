package proteinREG;

/**
 * @author jack_mhdong
 * 在识别蛋白质是否存在于句子时，我们存储此 蛋白质的全称 和 全称包含单词长度
 */
public class proteinEntity {
	
	private String originalProteinFullName;
	private int Count;			//蛋白质全称中包含单词个数
	private String newProtein2GeneName;
	
	public proteinEntity() {
		setNewProteinFullName("");
		Count = 0;
	}
	
	public void setProteinEntity(String oldProtein, String newProtein, int num) {
		setOriginalProteinName(oldProtein);
		setNewProteinFullName(newProtein);
		setInt(num);
	}
	
	public proteinEntity (String tmp, int num) {
		setNewProteinFullName(tmp);
		Count = num;
	}
	
	public proteinEntity (String oldProtein, String newProtein, int num) {
		setOriginalProteinName(oldProtein);
		setNewProteinFullName(newProtein);
		Count = num;
	}

	public int getIntNumber() {
		return Count;
	}
	
	public void setInt(int num) {
		Count = num;
	}

	public String getOriginalProteinName() {
		return originalProteinFullName;
	}

	public void setOriginalProteinName(String originalProteinName) {
		this.originalProteinFullName = originalProteinName;
	}

	public String getNewProteinFullName() {
		return newProtein2GeneName;
	}

	public void setNewProteinFullName(String newProteinFullName) {
		this.newProtein2GeneName = newProteinFullName;
	}
}
