package proteinREG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.PTBTokenizer;

/*
 * input: 摘要中的每一个句子
 * output: 将每一个句子中的蛋白质识别出来, 
 */

public class proteinREC {
	/***********************************************************************************/
	/********************* private variable ********************************************/
	
	private String originalSentence;
	private String newSentence;
	/**
	 * First parameter: HashMap [key: original string; values: newGene]
	 * Following: the location of ths hashmap in this sentence.
	 */
	private HashMap<Integer, proteinEntity> proteinMap = new HashMap<Integer, proteinEntity>();
	private HashMap<Integer, String> relationWordsMap = new HashMap<Integer, String>();
	private HashMap<Integer, String> negativeWordsMap = new HashMap<Integer, String>();
	
	/******************* end private variable *****************************************/
	
	public proteinREC() {
		newSentence = originalSentence = "";
		relationWordsMap.clear();
		proteinMap.clear();
		negativeWordsMap.clear();
	}

	public proteinREC(String sentence) {
		newSentence = originalSentence = sentence;
		relationWordsMap.clear();
		proteinMap.clear();
		negativeWordsMap.clear();
	}
	
	public proteinREC(List<HasWord> sentence) {
		newSentence = originalSentence = PTBTokenizer.labelList2Text(sentence);
		relationWordsMap.clear();
		proteinMap.clear();
		negativeWordsMap.clear();
	}
	
	/****************************************************************************************/
	
	/**
	 * @param sentenceList: 句子列表，包含句子中的一个个单词
	 * @param allKeysSets: 所有蛋白质和基因集合
	 * @param firstCharDict: 当蛋白质全称并非一个单词时, 我们需要比较全称是否存在与句子列表中
	 * @param geneSynProteinDict: 蛋白质和基因集合作为key, 对应的官方基因名称作为Value
	 * 从原始句子列表中识别出蛋白质、保存于字典proteinMap中，并将识别出的个数保存此中.
	 */
	/*
	 * Input: OldSentence; Output: NewSentence
	 * 		(if Old_sentence ！= NewSentence and VariedWords>=2, then print this sentence.)
	 * Deal with each sentence:
	 * 1. At first, we should tokenized
	 * 1. 首先对每一个句子进行分词. 对每一个单词, 先判断它是否存在于allKeysSets集合中:
	 * 		{若存在, 则直接修改该单词, 且VariedWords+1;
	 * 		  若不存在, 则判断firstCharDict的所有keys中是否包含的此单词:
	 * 			{若不包含, 直接返回; 
	 * 			  若包含, 则判断此单词后序单词是否和firstCharDict.get(firstWord)中某一个相等:
	 * 				{若相等, 则返回当前单词以及后续相等单词的个数num, 且VariedWords+1; 若不相等, 则直接返回1.}
	 * 			}
	 * 		}
	 * 2. 返回一个句子中出现两个或两个以上蛋白质的句子
	 * */
	
	public void proteinRecognition(List<HasWord> sentenceList,
			HashSet<String> allKeysSets,
			HashSet<String> relationKeySet,
			HashMap<String, String> firstCharDict,
			HashMap<String, String> geneSynProteinDict) {
		originalSentence = PTBTokenizer.labelList2Text(sentenceList);
		
		HashSet<String> conjwordset = new HashSet<String>(
				Arrays.asList("to", "of", "the", "and",
				"but", "an", "for", "are", "if",
				"is", "was", "it", "in", "as"));
		
		int index = 0, numOfList = sentenceList.size();
		ArrayList<String> newSentList = new ArrayList<String>();
		
		HashSet<String> variedWordsHashSet = new HashSet<String>();
		
		/***********************************************************************************/
		//  有点乱，如下代码需要重新整理
		/***********************************************************************************/
		/************************************************************************************/
		
		/*************  主要功能如下： 
		 * 1. 识别出sentenceList中的蛋白质
		 *    
		 ***********************************************************************************/
		HashSet<String> negativeWordsSet = new HashSet<String>(
				Arrays.asList("no", "not", "neither", "nor", "n't"));
		
		String key, word;
		while (index < numOfList) {
			HasWord wordInSent = sentenceList.get(index);
			word = wordInSent.word();
			key = word.toLowerCase();
			
			if (negativeWordsSet.contains(key))
			{
				newSentList.add(word);
				negativeWordsMap.put(index, word);
			}
			else if (relationKeySet.contains(key))
			{
				newSentList.add(word);
				relationWordsMap.put(index+1, word);
			}
			else if (conjwordset.contains(key))
			{
				newSentList.add(word);
			}
			else if (allKeysSets.contains(key))
			{
				if (!variedWordsHashSet.contains(key))
				{
					variedWordsHashSet.add(key);
				}
				String value = geneSynProteinDict.get(key);
				newSentList.add(value);
				proteinEntity proteinFindEntity = new proteinEntity();
				proteinFindEntity.setProteinEntity(word, value, 1);
				proteinMap.put(index+1, proteinFindEntity);
			}
			// 识别蛋白质全称, 先检测其第一个字母是否在集合中
			else if (firstCharDict.keySet().contains(key))
			{
				proteinEntity newProtein = checkProteinFullnameExists(key, index, firstCharDict, sentenceList);
				String newSubkeyStr = newProtein.getOriginalProteinName();
				// 根据返回新串和首单词进行对比，若不相等，说明存在字符串全称是识别出来的蛋白质
				if (0 != newSubkeyStr.compareTo(key)) {
					String value = geneSynProteinDict.get(newSubkeyStr);
					if (!variedWordsHashSet.contains(newSubkeyStr)) {
						variedWordsHashSet.add(newSubkeyStr);
					}
					proteinEntity proteinFindEntity = new proteinEntity();
					proteinFindEntity.setProteinEntity(newSubkeyStr, value, newProtein.getIntNumber());
					proteinMap.put(index+1, proteinFindEntity);
					newSentList.add(value);
				}
				else {
					newSentList.add(word);
				}
				index += newProtein.getIntNumber() - 1;
			}
			else {
				newSentList.add(word);
			}
			index += 1;
		}
		newSentence = PTBTokenizer.labelList2Text(Sentence.toUntaggedList(newSentList));
	}
	
	/*
	 * 检查蛋白质数据库中，包含第一个单词的字符串全称是否存在于该数据库中
	 * 如存在，则返回集合（蛋白质串、全称、全称包含单词个数）
	 * 检查蛋白质全称是否存在, 若存在则返回(蛋白质全称字符串、对应长度)
	 * */
	private proteinEntity checkProteinFullnameExists(
			String firstCharLowerCase,
			int index,
			HashMap<String, String> firstCharDict,
			List<HasWord> sentenceList
			)
	{
		String value = firstCharDict.get(firstCharLowerCase);		
		String[] allValueList = value.split("\\|");
		ArrayList<String> sameFirstCharList = new ArrayList<String>();
		for (String elemString  : allValueList) {
			elemString = elemString.trim();
			sameFirstCharList.add(elemString);
		}
		
		/*
		 *  compare the key list in the allkeysSet start with key
		 */
		int wordListNum = sentenceList.size();
		String[] keyList = null;
		int numSubKey = 0, i;
		proteinEntity proteins = new proteinEntity(firstCharLowerCase, firstCharLowerCase, 1);
		for (String subKeyStr: sameFirstCharList)
		{
			keyList = subKeyStr.split(" ");
			numSubKey = keyList.length;
			i = 0;
			i = 0;
			while ((i < numSubKey) && ((index+i) < wordListNum)) {
				String keystrString = keyList[i].trim();
				String curWordString = sentenceList.get(index+i).toString();
				if (keystrString.compareTo(curWordString) != 0) {
					break;
				}
				i += 1;
			}
			if (i == numSubKey) {
				proteins.setOriginalProteinName(subKeyStr.trim());
				proteins.setInt(numSubKey);
				return proteins;
			}
		}
		return proteins;
	}
	
	public HashMap<Integer, proteinEntity> getProteinMap() {
		return proteinMap;
	}

	public HashMap<String, String> setProtein(String oldPhrases, String newGene) {
		HashMap<String, String> newProtein = new HashMap<String, String>();
		newProtein.put(oldPhrases, newGene);
		return newProtein;
	}

	// 添加识别出来的蛋白质, 保存蛋白质-Gene对以及其在句子中的位置
	public void addProteinMap(int index, proteinEntity newProteinMap) {
		if (!proteinMap.containsKey(newProteinMap)) {
			proteinMap.put(Integer.valueOf(index) + 1, newProteinMap);
		}
	}

	public String getOriginalSentence() {
		return originalSentence;
	}
	
	public String getSentence() {
		return newSentence;
	}

	public void setSentence(String sentence) {
		newSentence = sentence;
	}
	
	public HashMap<Integer, String> getRelationWordsMap() {
		return relationWordsMap;
	}

	public void setRelationWordsMap(HashMap<Integer, String> relationWordsMap) {
		this.relationWordsMap = relationWordsMap;
	}

	public HashMap<Integer, String> getNegativeWordsMap() {
		return negativeWordsMap;
	}

	public void setNegativeWordsMap(HashMap<Integer, String> negativeWordsMap) {
		this.negativeWordsMap = negativeWordsMap;
	}

	public void setProteinMap(HashMap<Integer, proteinEntity> proteinMap) {
		this.proteinMap = proteinMap;
	}
	
	/**
	 * Get the index of specified words in a Hashmap with <int, string>
	 */
	public int getLocationOfSpecifiedWords(HashMap<Integer, String> IntStrWordsMap, String words)
	{
		Set<Integer> keySet = IntStrWordsMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		
		for (Integer key: keyList) {
			String value = IntStrWordsMap.get(key);
			if (words.compareTo(value) == 0) {
				return key; // the location in the array
			}
		}
		return -1;
	}
	

	/******************************************************************************************************/
	/**
	 * Get the negative words number in current line, and Print all negative words in current line if needed.
	 */
	public int getNumberOfNegativeWords() {
		return negativeWordsMap.size();
	}
	public String getStringOfNegativeWords() {
		
		String relationStr = "";
		
		Set<Integer> keySet = negativeWordsMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		
		int firstFlag = 1;
		for (Integer key: keyList) {
			String value = negativeWordsMap.get(key);
			if (firstFlag == 1) {
				relationStr = key + ": ";
				firstFlag = 0;
			}
			else {
				relationStr += " || " + key + ": ";
			}
			relationStr += value;
		}
		return relationStr.trim();
	}
	/******************************************************************************************************/

	
	/******************************************************************************************************/
	/**
	 * Get the number of relation words in current line.
	 */
	public int getNumberOfRelationWords() {
		return relationWordsMap.size();
	}
	/**
	 * Print all the recognition words and the corresponding location in current line.
	 */
	public String getStringOfRelationWords() {
		String relationStr = "";
		
		Set<Integer> keySet = relationWordsMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		
		int firstFlag = 1;
		for (int key: keyList) {
			String value = relationWordsMap.get(key);
			if (firstFlag == 1) {
				relationStr = key + ": ";
				firstFlag = 0;
			}
			else {
				relationStr += " || " + key + ": ";
			}
			relationStr += value;
		}
		return relationStr.trim();
	}
	/******************************************************************************************************/
	
	/******************************************************************************************************/
	/**
	 * Get the recognition proteins in current line.
	 */
	public int getNumberOfRecognitionProteins() {
		return proteinMap.size();
	}
	
	/**
	 * get the location of recognition protein in current HashMap
	 * @param protein
	 * @return
	 */
	public int getLocationOfRecognitionProtein(String protein) {
		Set<Integer> keySet = proteinMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		
		for (int key : keyList) {
			proteinEntity valueEntity = proteinMap.get(key);
			String value = valueEntity.getNewProteinFullName();
			if (protein.compareTo(value) == 0) {
				return key;
			}
		}
		return -1;
	}
	
	/**
	 * Print all the recognition Proteins and the corresponding location in current line.
	 */
	public String getStringOfRecognitionProtein() {
		String recognitionStr = "";
		
		Set<Integer> keySet = proteinMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		
		int firstFlag = 1;
		for (Integer key: keyList) {
			proteinEntity value = proteinMap.get(key);
			if (firstFlag == 1) {
				recognitionStr = key + ": ";
				firstFlag = 0;
			}
			else {
				recognitionStr += " || " + key + ": ";
			}
			recognitionStr += value.getOriginalProteinName() + " -> " + value.getNewProteinFullName();
		}
		return recognitionStr;
	}
	/******************************************************************************************************/

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