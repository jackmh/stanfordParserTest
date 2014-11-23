
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


import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import process.DocumentPreprocessor;
import proteinREG.proteinREC;

import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;


class ParserDemo {

	/**
	 * The main method demonstrates the easiest way to load a parser. Simply
	 * call loadModel and specify the path of a serialized grammar model, which
	 * can be a file, a resource on the classpath, or even a URL. For example,
	 * this demonstrates loading from the models jar file, which you therefore
	 * need to include in the classpath for ParserDemo to work.
	 */
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (args.length > 0) {
			demoDP(lp, args[0]);
			// D:/keTiInHIT_FROM2014_08/testData/16043634
			// D:/keTiInHIT_FROM2014_08/tools/stanford-parser-full-2014-06-16/data/english-onesent.txt
		} else {
			demoAPI(lp);
		}
	}

	/**
	 * demoDP demonstrates turning a file into tokens and then parse trees. Note
	 * that the trees are printed by calling pennPrint on the Tree object. It is
	 * also possible to pass a PrintWriter to pennPrint if you want to capture
	 * the output.
	 */
	public static void demoDP(LexicalizedParser lp, String filename) {
		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			parse.pennPrint();
			System.out.println();

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection tdl = gs.typedDependenciesCCprocessed();
			System.out.println(tdl);
			System.out.println();
		}
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
		 * �������
		 * 1. ���ļ��ж�ȡժҪ,��ժҪ���з־�([.?!]).
		 * 2. ��ÿ�仰���е�����ʶ��ͱ�׼��. ���������ٰ���2�������ʵľ���
		 * */
		String testAbstractText = "D:/keTiInHIT_FROM2014_08/testData/16043634";
		splitAbstractIntoSentence(testAbstractText);

		// This option shows loading and using an explicit tokenizer
		String sent2 = "HSPB1 and HSPA4 interact with MME in C4-2 prostate cancer cells.";
		//String sent2 = "TSNAX is a human protein that bears a homology to TSN and interacts with it.";
		//String sent2 = "TSNAX is interacted with TSN.";
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory
				.getTokenizer(new StringReader(sent2));
		List<CoreLabel> rawWords2 = tok.tokenize();
		
		
		
		Tree parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);
		System.out.println();
		parse.pennPrint();

		System.out.println();
		System.out.println("=============================================");
		System.out.println(sent2);
		System.out.println(parse.taggedYield());
		System.out.println(parse.taggedLabeledYield());
		System.out.println("=============================================");
		System.out.println();
		// pattern test
		String s1 = "/^VB.*|^NN.*/=Relation .. (/^NN.*/=GeneA .. /^NN.*/=GeneB)";
		String s2 = "/^NN.*/=GeneA .. (/^VB.*/=Relation .. /^NN.*|^CD.*/=GeneB)";
		String s3 = "/^NN.*/=GeneA .. (/^NN.*/=GeneB .. /^VB.*|^NN.*/=Relation)";
		/*
		 * ������Ҫ��һ������
		 * 1. �ж�A��C�Ƿ���Gene���У�B�Ƿ��ǹ�ϵ��(����Gene���ļ�����ϵ�ʿ��ļ�)
		 * 2. �ж������������PPI��IPP��PIP�����������I�Ĵ��ԣ�PP֮��ʵĸ���
		 * 	  1> PIP��I�Ĵ��Գ�ΪVP��NN
		 *    2> PPI/IPP�����I�Ĵ��ԣ�PP֮��ʵĸ���
		 * 3. �񶨴�ʶ��
		 * */
		// ����ģʽƥ���м����ŵ������Լ����ż���ĳ��λ�õ�����
		
		TregexPattern tregrex = TregexPattern.compile(s2);
		TregexMatcher mat = tregrex.matcher(parse);
		
		String GeneAStr = "", GeneBStr = "", relationStr = "";
		while (mat.find()) {
			GeneAStr = getStrFromTregexMatcher(mat, "GeneA");
			GeneBStr = getStrFromTregexMatcher(mat, "GeneB");
			relationStr = getStrFromTregexMatcher(mat, "Relation");
			if (geneSet.contains(GeneAStr) &&
					geneSet.contains(GeneBStr) &&
					relationKeySet.contains(relationStr)
					)
			{
				System.out.println("=============================================");
				System.out.println(GeneAStr + "\t" + GeneBStr + "\t" + relationStr);
				System.out.println("=============================================");
			}
		}
	}
	
	/*
	 * ��TregexMatcher�и���Tree������Ƶõ���Ӧ�Ľ��ֵ
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
	 * ���ļ��ж�ȡGene����ϵ��, ����ȡ���������ݷֱ����geneSet, relationKeySet������
	 * ͬʱ, ����һ��protein-gene��ϣ��, �������г���protein�ĵ��ʻ�����滻�ɶ�Ӧ��gene
	 * 
	 * 1. �������е�Gene, ����ڼ���geneSet��
	 * 2. �������еĵ�����(����ڼ���allKeysSets��);
	 * 	     ���������protein���ʳ��ȴ���1, �򱣴����ֵ�firstCharDict��(key: ��һ������, Values: protein1ȫ��|protein2ȫ��)
	 * 3. ����protein-Gene��ϣ��; (key: proteinȫ��; Values: Gene��)
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
			// ���������ڴ����ͺ���Ĵ�������ڴ��ͻ����
			*************************************************************/
			geneSynProteinDict.put(protein, gene);
		}
	}
	
	/*
	 * 1. ��ժҪ���з־�;
	 * 2. ���ժҪ�е�ÿһ��, ʶ������еĵ����ʲ��Ե����ʽ��б�׼��
	 * 3. ��ÿһ�仰Ӧ��Stanford Parser���н���, �õ��﷨��
	 * 4. ��3�õ����﷨��, ��Ϲ�ϵ�ʡ�gene���ҵ���ѡ�ĵ������໥���ö�
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
	 				System.out.println(proteinSent.getOriginalSentence());
	 				System.out.println(proteinSent.getSentence());
	 				System.out.println();
 				}
			}
			newAbstractText = newAbstractText.trim();
			newAbstractText += "\n";
		}
		newAbstractText = newAbstractText.trim();
		System.out.println("---------------------------------------------------------------");
		System.out.println("---------------------------------------------------------------");
		System.out.println(newAbstractText);
		System.out.println("---------------------------------------------------------------");
		System.out.println("---------------------------------------------------------------");
		
		/**************************************************************************************/
		return newAbstractText;
	}
}