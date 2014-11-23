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
	private static HashMap<Integer, proteinEntity> proteinMap = new HashMap<Integer, proteinEntity>();	
	
	/******************* end private variable *****************************************/
	
	public proteinREC() {
		newSentence = originalSentence = "";
		proteinMap.clear();
	}

	public proteinREC(String sentence) {
		newSentence = originalSentence = sentence;
		proteinMap.clear();
	}
	
	public proteinREC(List<HasWord> sentence) {
		newSentence = originalSentence = PTBTokenizer.labelList2Text(sentence);
		proteinMap.clear();
	}
	
	/****************************************************************************************/
	
	/**
	 * @param sentenceList: �����б����������е�һ��������
	 * @param allKeysSets: ���е����ʺͻ��򼯺�
	 * @param firstCharDict: ��������ȫ�Ʋ���һ������ʱ, ������Ҫ�Ƚ�ȫ���Ƿ����������б���
	 * @param geneSynProteinDict: �����ʺͻ��򼯺���Ϊkey, ��Ӧ�Ĺٷ�����������ΪValue
	 * ��ԭʼ�����б���ʶ��������ʡ��������ֵ�proteinMap�У�����ʶ����ĸ����������.
	 */
	/*
	 * ����: OldSentence; ����ֵ: NewSentence
	 * 		(��Oldsentence ��= NewSentence��VariedWords>=2, ������þ���)
	 * ����ÿһ������:
	 * 1. ���ȶ�ÿһ�����ӽ��зִ�. ��ÿһ������, ���ж����Ƿ������allKeysSets������:
	 * 		{������, ��ֱ���޸ĸõ���, ��VariedWords+1;
	 * 		  ��������, ���ж�firstCharDict������keys���Ƿ�����Ĵ˵���:
	 * 			{��������, ֱ�ӷ���; 
	 * 			  ������, ���жϴ˵��ʺ��򵥴��Ƿ��firstCharDict.get(firstWord)��ĳһ�����:
	 * 				{�����, �򷵻ص�ǰ�����Լ�������ȵ��ʵĸ���num, ��VariedWords+1; �������, ��ֱ�ӷ���1.}
	 * 			}
	 * 		}
	 * 2. ����һ�������г����������������ϵ����ʵľ���
	 * */
	
	public static void proteinRecognition(List<HasWord> sentenceList,
			HashSet<String> allKeysSets,
			HashMap<String, String> firstCharDict,
			HashMap<String, String> geneSynProteinDict) {
		originalSentence = PTBTokenizer.labelList2Text(sentenceList);
		
		HashSet<String> conjwordset = new HashSet<String>(
				Arrays.asList("to", "of", "the", "and",
				"but", "an", "for", "not", "are", "if",
				"is", "was", "it", "in", "as"));
		
		int index = 0, numOfList = sentenceList.size();
		ArrayList<String> newSentList = new ArrayList<String>();
		
		HashSet<String> variedWordsHashSet = new HashSet<String>();
		
		/***********************************************************************************/
		//  �е��ң����´�����Ҫ��������
		/***********************************************************************************/
		/************************************************************************************/
		
		/*************  ��Ҫ�������£� 
		 * 1. ʶ���sentenceList�еĵ�����
		 *    
		 ***********************************************************************************/
		String key, word;
		while (index < numOfList) {
			HasWord wordInSent = sentenceList.get(index);
			word = wordInSent.word();
			key = word.toLowerCase();
			
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
				proteinEntity proteinFindEntity = new proteinEntity();
				proteinFindEntity.setProteinEntity(word, value, 1);
				proteinMap.put(index+1, proteinFindEntity);
			}
			// ʶ�𵰰���ȫ��, �ȼ�����һ����ĸ�Ƿ��ڼ�����
			else if (firstCharDict.keySet().contains(key))
			{
				proteinEntity newProtein = checkProteinFullnameExists(key, index, firstCharDict, sentenceList);
				String newSubkeyStr = newProtein.getOriginalProteinName();
				// ���ݷ����´����׵��ʽ��жԱȣ�������ȣ�˵�������ַ���ȫ����ʶ������ĵ�����
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
	 * ��鵰�������ݿ��У�������һ�����ʵ��ַ���ȫ���Ƿ�����ڸ����ݿ���
	 * ����ڣ��򷵻ؼ��ϣ������ʴ���ȫ�ơ�ȫ�ư������ʸ�����
	 * ��鵰����ȫ���Ƿ����, �������򷵻�(������ȫ���ַ�������Ӧ����)
	 * */
	public static proteinEntity checkProteinFullnameExists(
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

	// ���ʶ������ĵ�����, ���浰����-Gene���Լ����ھ����е�λ��
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

	// ������ʶ����ĵ�������Ŀ
	public int getRecognitionProteinNum() {
		return proteinMap.size();
	}
	
	public void getCurRecognitionProtein() {
		Set<Integer> keySet = proteinMap.keySet();
		List<Integer> keyList = new ArrayList<Integer>(keySet);
		Collections.sort(keyList);
		int firstFlag = 1;
		for (Integer key: keyList) {
			proteinEntity value = proteinMap.get(key);
			if (firstFlag == 1) {
				System.out.print(key + ": ");
				firstFlag = 0;
			}
			else {
				System.out.print(" | " + key + ": ");
			}
			System.out.print(value.getOriginalProteinName() + " -> " + value.getNewProteinFullName());
		}
		System.out.println();
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