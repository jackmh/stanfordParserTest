package proteinREG;

/**
 * @author jack_mhdong
 * ��ʶ�𵰰����Ƿ�����ھ���ʱ�����Ǵ洢�� �����ʵ�ȫ�� �� ȫ�ư������ʳ���
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
