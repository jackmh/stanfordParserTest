package dataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import config.config;
import edu.stanford.nlp.ling.CoreAnnotations.LEndAnnotation;

public class resultanalysis {
	
	/**
	 *  1. 正确率 = 正确识别的个体总数 /  识别出的个体总数
   		2. 召回率 = 正确识别的个体总数 /  测试集中存在的个体总数
   		3. F值  = 正确率 * 召回率 * 2 / (正确率 + 召回率) （F 值即为正确率和召回率的调和平均值）
	 */
	private float Accuracy;
	private float Recall;	
	private float F1_Measure;
	
	private int allRecognitionPubmed2Proteins;
	private int rightRecognitionNum;
	private int testSetNum;
	
	public resultanalysis() {
		allRecognitionPubmed2Proteins = 0;
		rightRecognitionNum = 0;
		testSetNum = 0;
		Accuracy = 0;
		Recall = 0;
		F1_Measure = 0;
	}
	
	public void proteinExtractionStatistic()
	{
		HashMap<String, String> interactiveProteinsMap = gethashMapFromFname(config.allRecognitionPPIFname);
		String srcFilename = config.pubmed2GenesIntFname;
		
		// 计算识别出的个体总数
		allRecognitionPubmed2Proteins = getAllRecognitionPubmed2Proteins(interactiveProteinsMap);
		// 计算正确识别的个体总数 and 计算测试集合中存在的个体总数
		getAccuracyRecognitionPubmed2Proteins(interactiveProteinsMap, srcFilename);
		
		// 正确率 = 正确识别的个体总数 /  识别出的个体总数
		Accuracy = (float) (rightRecognitionNum*1.0/allRecognitionPubmed2Proteins);
		// 召回率 = 正确识别的个体总数 /  测试集中存在的个体总数
		Recall = (float) (rightRecognitionNum*1.0/testSetNum);
		// F值  = 正确率 * 召回率 * 2 / (正确率 + 召回率)
		F1_Measure = Accuracy*Recall*2 / (Accuracy+Recall);
	}
	
	// 计算正确识别的个体总数
	// 计算测试集合中存在的个体总数	
	private void getAccuracyRecognitionPubmed2Proteins(HashMap<String, String> interactiveProteinsMap,
			String srcFname
			)
	{
		File file = new File(srcFname);
		BufferedReader bReader = null;
		rightRecognitionNum = 0;
		testSetNum = 0;
		try {
			bReader = new BufferedReader(new FileReader(file));
			String lineStr = null;
			while ((lineStr = bReader.readLine()) != null)
			{
				lineStr = lineStr.trim();
				if (lineStr.substring(0, 1).compareTo("#") == 0 || lineStr.compareTo("") == 0) {
					continue;
				}
				String[] lineValueList = lineStr.split("\t");
				if (lineValueList.length < 2) {
					continue;
				}
				String pubmedID = lineValueList[0].trim();
				String proteinPairs = lineValueList[1].trim();
				HashSet<String> proteinSet = ConvertStringIntoHashSet(proteinPairs, "\\,");
				testSetNum += proteinSet.size();
				
				// 计算正确识别的个体总数
				String recognitionProteinPairs = interactiveProteinsMap.get(pubmedID);
				if (recognitionProteinPairs == null || 0 == recognitionProteinPairs.compareTo("NA")) {
					continue;
				}
				String[] recogProteinsList = recognitionProteinPairs.split("\\,");
				for (String proteinPair : recogProteinsList)
				{
					String GeneA = proteinPair.split("\\|")[0].trim();
					String GeneB = proteinPair.split("\\|")[1].trim();
					String newProteinPairs = GeneB + "|" + GeneA;
					if (proteinSet.contains(proteinPair) || proteinSet.contains(newProteinPairs))
					{
						rightRecognitionNum += 1;
					}
				}
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 计算识别出的个体总数
	private int getAllRecognitionPubmed2Proteins(HashMap<String, String> interactiveProteinsMap)
	{
		int count = 0;
		if (interactiveProteinsMap.size() > 0)
		{
			for (String key : interactiveProteinsMap.keySet())
			{
				String proteinPairs = interactiveProteinsMap.get(key);
				if (proteinPairs.compareTo("NA") == 0)
				{
					continue;
				}
				String[] proteinPairList = proteinPairs.split("\\,");
				count += proteinPairList.length;
			}
		}
		return count;
	}
	
	private HashSet<String> ConvertStringIntoHashSet(String ProteinValues, String delimiter)
	{
		if (ProteinValues.compareTo("") == 0) {
			return null;
		}
		HashSet<String> proteinSet = new HashSet<String>();
		String[] proteinList = ProteinValues.split(delimiter);
		for (String protein : proteinList)
		{
			proteinSet.add(protein);
		}
		return proteinSet;
	}
	
	private HashMap<String, String> gethashMapFromFname(String hasMapFname)
	{
		HashMap<String, String> interactiveProteinsMap = new HashMap<String, String>();
		
		File file = new File(hasMapFname);
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new FileReader(file));
			String lineStr = null;
			while ((lineStr = bReader.readLine()) != null)
			{
				lineStr = lineStr.trim();
				if (lineStr.substring(0, 1).compareTo("#") == 0 || lineStr.compareTo("") == 0) {
					continue;
				}
				String[] lineValueList = lineStr.split("\t");
				if (lineValueList.length < 2) {
					continue;
				}
				String pubmedID = lineValueList[0].trim();
				String proteinPairs = lineValueList[1].trim();
				interactiveProteinsMap.put(pubmedID, proteinPairs);
			}
			bReader.close();
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		return interactiveProteinsMap;
	}

	public float getAccuracy() {
		return Accuracy;
	}

	public void setAccuracy(float accuracy) {
		Accuracy = accuracy;
	}

	public float getRecall() {
		return Recall;
	}

	public void setRecall(float recall) {
		Recall = recall;
	}

	public float getF1_Measure() {
		return F1_Measure;
	}

	public void setF1_Measure(float f1_Measure) {
		F1_Measure = f1_Measure;
	}
}
