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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.w3c.dom.ranges.RangeException;

import parseTreeGetRelation.GetRelationParseTree;
import process.DocumentPreprocessor;
import proteinREG.proteinREC;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;


class ParserDemo {

	/**
	 * The main method demonstrates the easiest way to load a parser. Simply
	 * call loadModel and specify the path of a serialized grammar model, which
	 * can be a file, a resource on the classpath, or even a URL. For example,
	 * this demonstrates loading from the models jar file, which you therefore
	 * need to include in the classpath for ParserDemo to work.
	 */
	public static boolean __DEBUG = true;
	public static final String BaseDIR = "./proteinPI";
	public static final String geneDictFilename = BaseDIR + File.separator + "0_dictOfAllMergeGeneProtein.name";
	public static final String relateionKeyFilename = BaseDIR + File.separator + "0_relationKeys.name";
	
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
	
	public static void main(String[] args) {
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// D:/keTiInHIT_FROM2014_08/testData/16043634
		// D:/keTiInHIT_FROM2014_08/tools/stanford-parser-full-2014-06-16/data/english-onesent.txt
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
		/*
		 * 这里加上
		 * 1. 从文件中读取摘要,对摘要进行分句([.?!]).
		 * 2. 对每句话进行蛋白质识别和标准化. 检索出至少包含2个蛋白质的句子
		 * */
		String testAbstractText = BaseDIR + File.separator + "17342744";
		splitAbstractIntoSentence(testAbstractText);
		
//		// This option shows loading and using an explicit tokenizer
//		String sent2 = "HSPB1 and HSPA4 interact with MME in C4-2 prostate cancer cells.";
//		//String sent2 = "TSNAX is a human protein that bears a homology to TSN and interacts with it.";
//		//String sent2 = "TSNAX is interacted with TSN.";
//		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
//				new CoreLabelTokenFactory(), "");
//		Tokenizer<CoreLabel> tok = tokenizerFactory
//				.getTokenizer(new StringReader(sent2));
//		List<CoreLabel> rawWords2 = tok.tokenize();
//		
//		Tree parse = lp.apply(rawWords2);
//
//		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
//		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
//		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
//		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
//		System.out.println(tdl);
//		System.out.println();
//		parse.pennPrint();
//
//		System.out.println();
//		System.out.println("=============================================");
//		System.out.println(sent2);
//		System.out.println(parse.taggedYield());
//		System.out.println(parse.taggedLabeledYield());
//		System.out.println("=============================================");
//		System.out.println();
//		// pattern test
//		String s1 = "/^VB.*|^NN.*/=Relation .. (/^NN.*/=GeneA .. /^NN.*/=GeneB)";
//		String s2 = "/^NN.*/=GeneA .. (/^VB.*/=Relation .. /^NN.*|^CD.*/=GeneB)";
//		String s3 = "/^NN.*/=GeneA .. (/^NN.*/=GeneB .. /^VB.*|^NN.*/=Relation)";
//		/*
//		 * 这里需要进一步处理：
//		 * 1. 判断A、C是否在Gene库中，B是否是关系词(访问Gene库文件、关系词库文件)
//		 * 2. 判断在三种情况下PPI、IPP、PIP三种情况下中I的词性，PP之间词的个数
//		 * 	  1> PIP：I的词性常为VP或NN
//		 *    2> PPI/IPP情况下I的词性，PP之间词的个数
//		 * 3. 否定词识别
//		 * */
//		// 区别模式匹配中加括号的区别以及括号加在某个位置的区别
//		
//		TregexPattern tregrex = TregexPattern.compile(s2);
//		TregexMatcher mat = tregrex.matcher(parse);
//		
//		String GeneAStr = "", GeneBStr = "", relationStr = "";
//		while (mat.find()) {
//			GeneAStr = getStrFromTregexMatcher(mat, "GeneA");
//			GeneBStr = getStrFromTregexMatcher(mat, "GeneB");
//			relationStr = getStrFromTregexMatcher(mat, "Relation");
//			if (geneSet.contains(GeneAStr) &&
//					geneSet.contains(GeneBStr) &&
//					relationKeySet.contains(relationStr)
//					)
//			{
//				System.out.println("=============================================");
//				System.out.println(GeneAStr + "\t" + GeneBStr + "\t" + relationStr);
//				System.out.println("=============================================");
//			}
//		}
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
			if (line.compareTo("") == 0 || line.compareTo("\t") == 0)
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
			*************************************************************/
			geneSynProteinDict.put(protein, gene);
		}
	}
	
	/*
	 * 1. 对摘要进行分句;
	 * 2. 针对摘要中的每一句, 识别出其中的蛋白质并对蛋白质进行标准化
	 * 3. 对每一句话应用Stanford Parser进行解析, 得到语法树
	 * 4. 由3得到的语法树, 结合关系词、gene库找到候选的蛋白质相互作用对
	 * */
	public static String splitAbstractIntoSentence(String abstractPath) {
		Iterable<String> allLines = IOUtils.readLines(abstractPath);
		/*********************************************************/ 
		/*
		 * 1. Convert the Abstract text into sentenceList. each list contain of sentence list.
		 * 2. In each paragraph, split each sentence with char[.?!] as default.
		 */
		String newAbstractText = "";
		List<List<HasWord>> sentenceListWord = new LinkedList<List<HasWord>>();
		for (String paragraph : allLines) {
			if (paragraph.compareTo("") == 0 || paragraph.compareTo("\n") == 0)
			{
				newAbstractText += paragraph;
				continue;
			}
			Reader reader = new StringReader(paragraph);
			DocumentPreprocessor dp = new DocumentPreprocessor(reader);
			
			Iterator<List<HasWord>> it = dp.iterator();
			// each sentence in paragraph.
			sentenceListWord.clear();
			while (it.hasNext()) {
			   List<HasWord> sentence = it.next();
			   sentenceListWord.add(sentence);
			}
			// convert the list into string, append it into newAbstractText
			for(List<HasWord> sentence:sentenceListWord) {
 				proteinREC proteinSent = new proteinREC();
 				proteinREC.proteinRecognition(sentence, allKeysSets, firstCharDict, geneSynProteinDict);
 				newAbstractText += proteinSent.getSentence() + " ";
 				if (proteinSent.getRecognitionProteinNum() >= 2) {
 					if (__DEBUG == true) {
 						System.out.println();
 						System.out.println(proteinSent.getOriginalSentence());
 						System.out.println(proteinSent.getSentence());
 					}
	 				GetRelationParseTree relationExtracTree = new GetRelationParseTree(proteinSent.getSentence());
	 				relationExtracTree.getRelateion(lp, geneSet, relationKeySet);
 				}
			}
			newAbstractText = newAbstractText.trim();
			newAbstractText += "\n";
		}
		newAbstractText = newAbstractText.trim();
		if (__DEBUG == true)
		{
			System.out.println("\n---------------------------------------------------------------");
			System.out.println(newAbstractText);
			System.out.println("---------------------------------------------------------------");
		}
		/**************************************************************************************/
		return newAbstractText;
	}
}