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
 * input: ժҪ�е�ÿһ������
 * output: ��ÿһ�������еĵ�����ʶ�����, 
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
	 * ��ԭʼ�����б���ʶ��������ʡ��������ֵ�proteinMap�У�����ʶ����ĸ����������.
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
	 * ��鵰����ȫ���Ƿ����, �������򷵻�(������ȫ���ַ�������Ӧ����)
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

	// ���ʶ������ĵ�����, ���浰����-Gene���Լ����ھ����е�λ��
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
	
	//public static List<List<HasWord>> convertAbstract2List
	
}



