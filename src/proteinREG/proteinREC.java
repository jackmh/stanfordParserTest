package proteinREG;

import java.util.HashMap;
import java.util.HashSet;

/*
 * input: ժҪ�е�ÿһ������
 * output: ��ÿһ�������еĵ�����ʶ�����, 
 */

public class proteinREC {
	private String sentence;

	/**
	 * First parameter: HashMap [key: original string; values: newGene]
	 * Following: the location of ths hashmap in this sentence.
	 */
	private HashMap<HashMap<String, String>, Integer> proteinMap;

	public proteinREC() {
		sentence = "";
		proteinMap.clear();
	}

	public proteinREC(String sen) {
		// TODO Auto-generated constructor stub
		sentence = sen;
		proteinMap.clear();
	}

	public static void proteinRecognition(String oldSentence,
			HashSet<String> allKeysSets, HashMap<String, String> firstCharDict,
			HashMap<String, String> geneSynProteinDict) {
	}

	public HashMap<HashMap<String, String>, Integer> getProteinMap() {
		return proteinMap;
	}

	public HashMap<String, String> setProtein(String oldPhrases, String newGene) {
		HashMap<String, String> newProtein = new HashMap<String, String>();
		newProtein.put(oldPhrases, newGene);
		return newProtein;
	}

	// ���ʶ������ĵ�����, ���浰����-Gene���Լ����ھ����е�λ��
	public void addProteinMap(int index, HashMap<String, String> newProteinMap) {
		if (!proteinMap.containsKey(newProteinMap)) {
			proteinMap.put(newProteinMap, Integer.valueOf(index));
		}
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	// ������ʶ����ĵ�������Ŀ
	public int getRecognitionProteinNum() {
		return proteinMap.size();
	}

	/*
	 * ���ӱ߽������ʽ����㷨�� ��1����.?!(�Ϳ��ܵ�;:-)����λ��֮���һ������ľ��ӱ߽硣
	 * ��2���������߽���������ţ���ô�Ѽ���߽��Ƶ����ź��档 ��3����ȥ��������о��ı߽��ʸ�
	 * -����ھ��֮ǰ��һ�����ܳ����ھ���ĩβ��������֪����д��ʽ������ͨ��������һ ����д�����֣�����Prof.����vs.��
	 * -������ǰ����һ��������֪����д��ʽ�����Ǿ�����û�д�д�ʡ�����������ȷ�ش�
	 * ����etc.����Jr.�����Ĵ������д�÷�����Щ��дһ������ھ��ӵ��м����ĩβ�� ��4�����������������������ȥ?����!�ı߽��ʸ�
	 * -��Щ���ź������һ��Сд��ĸ������һ����֪���֣��� ��5����Ϊ��������߽���Ǿ��ӵı߽硣
	 * 
	 * �����ӱ߽���Կ�����һ���������⡣
	 */
}
