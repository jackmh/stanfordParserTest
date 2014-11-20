package proteinREG;

import java.util.HashMap;

import org.omg.CORBA.PRIVATE_MEMBER;

/*
 * input: 摘要中的每一个句子
 * output: 将每一个句子中的蛋白质识别出来, 
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
	
	// 添加识别出来的蛋白质, 保存蛋白质-Gene对以及其在句子中的位置
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
	
	// 句子中识别出的蛋白质数目
	public int getRecognitionProteinNum() {
		return proteinMap.size();
	}
}
