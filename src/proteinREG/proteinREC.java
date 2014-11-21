package proteinREG;

import java.util.HashMap;
import java.util.HashSet;

/*
 * input: 摘要中的每一个句子
 * output: 将每一个句子中的蛋白质识别出来, 
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

	/*
	 * 句子边界的启发式检测算法： （1）在.?!(和可能的;:-)出现位置之后加一个假设的句子边界。
	 * （2）如果假设边界后面有引号，那么把假设边界移到引号后面。 （3）除去以下情况中句点的边界资格：
	 * -如果在句点之前是一个不总出现在句子末尾的众所周知的缩写形式，而且通常后面会跟一 个大写的名字，例如Prof.或者vs.。
	 * -如果句点前面是一个众所周知的缩写形式，但是句点后面没有大写词。这样即可正确地处
	 * 理像etc.或者Jr.这样的大多数缩写用法，这些缩写一般出现在句子的中间或者末尾。 （4）如果下面的条件成立，则除去?或者!的边界资格：
	 * -这些符号后面跟着一个小写字母（或者一个已知名字）。 （5）认为其他假设边界就是句子的边界。
	 * 
	 * 检测句子边界可以看出是一个分类问题。
	 */
}
