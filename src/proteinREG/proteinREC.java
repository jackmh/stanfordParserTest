package proteinREG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import process.wordToSentence;

import sun.nio.cs.ext.ISCII91;

import edu.stanford.nlp.ling.HasWord;

/*
 * input: 摘要中的每一个句子
 * output: 将每一个句子中的蛋白质识别出来, 
 */

public class proteinREC {
	
	/***********************************************************************************/
	/********************* private variable ********************************************/
	
	private static String originalSentence;
	private static String newSentence;
	/**
	 * First parameter: HashMap [key: original string; values: newGene]
	 * Following: the location of ths hashmap in this sentence.
	 */
	private static HashMap<HashMap<String, String>, Integer> proteinMap = new HashMap<HashMap<String,String>, Integer>();	
	
	/******************* end private variable *****************************************/
	
	public proteinREC() {
		newSentence = "";
		originalSentence = "";
		proteinMap.clear();
	}

	public proteinREC(String sen) {
		// TODO Auto-generated constructor stub
		originalSentence = sen;
		newSentence = sen;
		proteinMap.clear();
	}
	
	/****************************************************************************************/
	
	/**
	 * @param sentenceList
	 * @param allKeysSets
	 * @param firstCharDict
	 * @param geneSynProteinDict
	 * 从原始句子列表中识别出蛋白质、保存于字典proteinMap中，并将识别出的个数保存此中.
	 */
	public static void proteinRecognition(List<HasWord> sentenceList,
			HashSet<String> allKeysSets,
			HashMap<String, String> firstCharDict,
			HashMap<String, String> geneSynProteinDict) {
		HashSet<String> conjwordset = new HashSet<String>(
				Arrays.asList("to", "of", "the", "and",
				"but", "an", "for", "not", "are", "if",
				"is", "was", "it", "in", "as"));
		
		int index = 0, numOfList = sentenceList.size();
		ArrayList<String> newSentList = new ArrayList<String>();
		HashSet<String> variedWordsHashSet = new HashSet<String>();
		HashMap<String, String> variedWordsHashMap = new HashMap<String, String>();
		
		String key, word;
		while (index < numOfList) {
			word = sentenceList.get(index).toString();
			key = word.toLowerCase();
			variedWordsHashMap.clear();
			
			if (conjwordset.contains(key))
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
				variedWordsHashMap.put(word, value);
				proteinMap.put(variedWordsHashMap, index);
			}
			else if (firstCharDict.keySet().contains(key))
			{
				proteinEntity newProtein = checkProteinFullnameExists(key, index, firstCharDict, sentenceList);
				String newSubkeyStr = newProtein.getStr();
				if (0 != newSubkeyStr.compareTo(key)) {
					// different with key
					String value = geneSynProteinDict.get(key);
					if (!variedWordsHashSet.contains(newSubkeyStr)) {
						variedWordsHashSet.add(newSubkeyStr);
						value = geneSynProteinDict.get(newSubkeyStr);
					}
					variedWordsHashMap.put(word, value);
					proteinMap.put(variedWordsHashMap, index);
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
		newSentence = wordToSentence.wordToString(newSentList);
	}
	
	/*
	 * 检查蛋白质全称是否存在, 若存在则返回(蛋白质全称字符串、对应长度)
	 * */
	public static proteinEntity checkProteinFullnameExists(String firstCharLowerCase,
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
		proteinEntity proteins = new proteinEntity(firstCharLowerCase, 1);
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
				proteins.setStr(subKeyStr.trim());
				proteins.setInt(numSubKey);
				return proteins;
			}
		}
		return proteins;
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
		return newSentence;
	}

	public void setSentence(String sentence) {
		this.newSentence = sentence;
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
	
	//public static List<List<HasWord>> convertAbstract2List
	
}



