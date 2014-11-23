package proteinREG;

/**
 * @author jack_mhdong
 * ��ʶ�𵰰����Ƿ�����ھ���ʱ�����Ǵ洢�� �����ʵ�ȫ�� �� ȫ�ư������ʳ���
 */
public class proteinEntity {
	
	private String originalProteinFullName;
	private int Count;			//������ȫ���а������ʸ���
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
