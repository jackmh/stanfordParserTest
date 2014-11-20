package proteinREG;

import java.util.HashMap;

import org.omg.CORBA.PRIVATE_MEMBER;

/*
 * input: ժҪ�е�ÿһ������
 * output: ��ÿһ�������еĵ�����ʶ�����, 
 */

public class proteinRecognition {
	
	private String sentence;
	
	/**
	 * First parameter: HashMap [key: original string; values: newGene]
	 * Following: the location of ths hashmap in this sentence.
	 */
	private HashMap<HashMap<String, String>, Integer> proteinMap;
	
	public proteinRecognition(String sen) {
		// TODO Auto-generated constructor stub
		sentence = sen;
		proteinMap.clear();
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
}
