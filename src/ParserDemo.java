import io.IOUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import org.w3c.dom.ranges.RangeException;

import parseTreeGetRelation.GetRelationParseTree;
import process.DocumentPreprocessor;
import proteinREG.proteinREC;
import pubmedTextProcessing.ppiTextProcess;
import sun.misc.OSEnvironment;
import config.config;
import dataAnalysis.resultanalysis;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentProcessor;


class ParserDemo {

	/**
	 * The main method demonstrates the easiest way to load a parser. Simply
	 * call loadModel and specify the path of a serialized grammar model, which
	 * can be a file, a resource on the classpath, or even a URL. For example,
	 * this demonstrates loading from the models jar file, which you therefore
	 * need to include in the classpath for ParserDemo to work.
	 */
	
	public static final String geneDictFilename = config.BaseDIR + File.separator + "dictOfAllMergeGeneProtein.name";
	public static final String relateionKeyFilename = config.BaseDIR + File.separator + "relationKeys.name";
	
	//private static HashMap<String, String> geneSynProteinDict = new HashMap<String, String>();
	
	/*************************************************/
	private static HashMap<String, String> geneSynProteinDict = new HashMap<String, String>();
	//private static HashMap<String, ArrayList<String>> geneSynProteinDict = new HashMap<String, ArrayList<String>>();
		
	/*************************************************/
	
	private static HashSet<String> geneSet = new HashSet<String>();
	private static HashSet<String> relationKeySet = new HashSet<String>();
	
	private static HashSet<String> allKeysSets = new HashSet<String>();
	private static HashMap<String, String> firstCharDict = new HashMap<String, String>();
	private static LexicalizedParser lp = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	
	private static String pubmedID = "17342744New";
	
	public static void main(String[] args) {
		demoAPI(lp);
	}
	/**
	 * demoAPI demonstrates other ways of calling the parser with already
	 * tokenized text, or in some cases, raw text that needs to be tokenized as
	 * a single sentence. Output is handled with a TreePrint object. Note that
	 * the options used when creating the TreePrint can determine what results
	 * to print out. Once again, one can capture the output by passing a
	 * PrintWriter to TreePrint.printTree.
	 */
	public static void demoAPI(LexicalizedParser lp) {
		try {
			init();
		} catch (RangeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * 这里加上
		 * 1. 从文件中读取摘要,对摘要进行分句([.?!]).
		 * 2. 对每句话进行蛋白质识别和标准化. 检索出至少包含2个蛋白质的句子
		 * */
		LinkedList<String> pubmedFileList = new LinkedList<String>();
		File dir = new File(config.srcPubmedText);
		File fileList[] = dir.listFiles();
		for (int i = 0; i < fileList.length; i ++)
		{
			if (fileList[i].isFile())
			{
				pubmedFileList.add(fileList[i].getName());
			}
		}
		System.out.println(pubmedFileList.size());
		
		String testFileList[] = new String[]
				{
				"2446864"
				}; //  "19740107", "11943787", "2446864", "18062930", "18424275", "20578993", "11278549", "17342744New" 
		HashMap<String, String> interactiveProteinsMap = new HashMap<String, String>();
		ppiTextProcess pubmedTextProcess = new ppiTextProcess();
		
		for (String file : pubmedFileList) {
			setfilenameExp(file);
			System.out.println(pubmedID);
			String pubmedTextFullName = config.srcPubmedText + File.separator + pubmedID;
			if (config.__DEBUG__ == true) {
				pubmedTextFullName = config.BaseDIR + File.separator + pubmedID;
			}
			
			pubmedTextProcess.setPubmedID(pubmedID);
			pubmedTextProcess.pubmedTextProcessing(pubmedTextFullName,
					geneSet, allKeysSets, relationKeySet, firstCharDict, geneSynProteinDict, lp);
			
			if (!interactiveProteinsMap.containsKey(pubmedID))
			{
				String pubmedIDValues = ConvertHashSetIntoStr(pubmedTextProcess.getInteractiveProteinPairSet());
				interactiveProteinsMap.put(pubmedID, pubmedIDValues);
			}
		}
		String allInteractiveProteinPairStr = ConvertHashMapIntoStr(interactiveProteinsMap, "# Pubmed id\tGene1|Gene2\n");
		writeIntoFile(allInteractiveProteinPairStr, config.allRecognitionPPIFname);
		
		if (config.__ANALYSISFLAG__ == true && config.__DEBUG__ != true)
		{
			resultanalysis resAnalysis = new resultanalysis();
			resAnalysis.proteinExtractionStatistic();
			System.out.println("Accuracy: " + resAnalysis.getAccuracy()
					+ "\nRecall: " + resAnalysis.getRecall()
					+ "\nF1-Measure: " + resAnalysis.getF1_Measure() + "\n");
			System.out.println("-------------------------------------");
		}
	}
	
	/*
	 * 从TregexMatcher中根据Tree结点名称得到相应的结点值
	 * */
	public static String getStrFromTregexMatcher(TregexMatcher matcher, String nodeName) {
		Tree gene = matcher.getNode(nodeName);
		ArrayList<Word> valueList = gene.yieldWords();
		String geneNameStr = valueList.get(0).toString();
		return geneNameStr.trim();
	}
	
	public static void setfilenameExp(String filename1) {
		pubmedID = filename1;
	}

	private ParserDemo() {
	} // static methods only
	
	/*
	 * 从文件中读取Gene、关系词, 将读取出来的数据分别存入geneSet, relationKeySet集合中
	 * 同时, 建立一个protein-gene哈希表, 将句子中出现protein的单词或词组替换成对应的gene
	 * 
	 * 1. 保存所有的Gene, 存放于集合geneSet中
	 * 2. 保存所有的蛋白质(存放于集合allKeysSets中);
	 * 	     如果蛋白质protein单词长度大于1, 则保存在字典firstCharDict中(key: 第一个单词, Values: protein1全称|protein2全称)
	 * 3. 建立protein-Gene哈希表; (key: protein全称; Values: Gene名)
	 * */
	private static void init() throws IOException, RangeException{
		if (config.__WriteIntoFileFlag__ == true)
		{
			deleteFile(config.DstDIR);
		}
		String[] arrs  = null;
		/*
		 * Relation words set
		 * Saving all relation words with it's low case.
		 * */
		Iterable<String> relationLines = IOUtils.readLines(relateionKeyFilename);
		String relationWord = "";
		Pattern pattern = Pattern.compile("\t|,");
		for (String tmpLine: relationLines) {
			arrs = pattern.split(tmpLine);
			for (String word : arrs) {
				relationWord = word.trim();
				relationWord = relationWord.toLowerCase();
				relationKeySet.add(relationWord);
			}
		}
		
		String firstChar = "", values = "";
		String gene = "", protein = "";
		Iterable<String> allLines = IOUtils.readLines(geneDictFilename); 
		for (String line : allLines)
		{
			line = line.trim();
			if (line.compareTo("") == 0 || line.substring(0, 1).compareTo("#") == 0)
				continue;
			arrs = line.split("\t");
			protein = arrs[0].trim();
			gene = arrs[1].trim();
			
			// build a firstChar Sets, check later in a word in sentence not in keysets.
			arrs = protein.split(" ");
			firstChar = arrs[0];
			if (arrs.length > 1)
			{
				values = "";
				if (firstCharDict.containsKey(firstChar))
				{
					values = firstCharDict.get(firstChar) + "|";
				}
				values += protein;
				firstCharDict.put(firstChar, values);
			}
			// Establish a hashset contains protein fullname as key, check first.
			allKeysSets.add(protein);
			// Construct a hashset contains all geneName
			geneSet.add(gene);
			// Build gene dictionary
			
			/************************************************************
			// 这里会出现内存过大和后面的代码出现内存冲突问题
			 * 设置VM参数如下:
			 * -Xms512M
			 * -Xmx512M
			*************************************************************/
			geneSynProteinDict.put(protein, gene);
		}
	}
	
	/**
	 * * 删除此路径名表示的文件或目录。 
     * 如果此路径名表示一个目录，则会先删除目录下的内容再将目录删除，所以该操作不是原子性的。 
     * 如果目录中还有目录，则会引发递归动作。 
     * @param filePath 
     *            要删除文件或目录的路径。 
     * @return 当且仅当成功删除文件或目录时，返回 true；否则返回 false。 
     */
	public static void delFolder(String folderPath) {
		try {
			deleteFile(folderPath); //删除完里面所有内容
			String filePathString = folderPath;
			filePathString = filePathString.toString();
			File myFilePath = new File(filePathString);
			myFilePath.delete(); //删除空文件夹
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static boolean deleteFile(String filePath)
	{
		boolean flag = false;
		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			return flag;
		}
		String[] tempFileList = file.list();
		File tempFile = null;
		for (int i = 0; i < tempFileList.length; i ++) {
			if (filePath.endsWith(File.separator))
			{
				tempFile = new File(filePath + tempFileList[i]);
			}
			else {
				tempFile = new File(filePath + File.separator + tempFileList[i]);
			}
			
			if (tempFile.isFile())
			{
				tempFile.delete();
				flag = true;
			}
			if (tempFile.isDirectory()) {
				deleteFile(filePath + File.separator + tempFileList[i]);
				delFolder(filePath + File.separator + tempFileList[i]);
				flag = true;
			}
		}
		return flag;
	}
	/**************************************************/
	
	public static String ConvertHashSetIntoStr(HashSet<String> interactiveProteinPairSet)
	{
		String interactiveResultStr = "";
		int firstFlag = 1;
		for (String proteinPair:interactiveProteinPairSet)
		{
			if (firstFlag == 1)
			{
				interactiveResultStr += proteinPair;
				firstFlag = 0;
				continue;
			}
			interactiveResultStr += "," + proteinPair; 
		}
		return interactiveResultStr;
	}
	
	public static String ConvertHashMapIntoStr(HashMap<String, String> hashMap, String HeadTitle)
	{
		String resultOfHashMapStr = "";
		if (hashMap.size() > 0)
		{
			resultOfHashMapStr = HeadTitle;
			for (String key: hashMap.keySet())
			{
				resultOfHashMapStr += key + "\t";
				if (hashMap.get(key).compareTo("") != 0) {
					resultOfHashMapStr += hashMap.get(key) + "\n";
				}
				else {
					resultOfHashMapStr += "NA" + "\n";
				}
			}
		}
		return resultOfHashMapStr.trim();
	}
	
	public static void writeIntoFile(String text, String filename)
	{
		try {
			FileWriter writerConn = new FileWriter(filename);
			writerConn.write(text);
			writerConn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}