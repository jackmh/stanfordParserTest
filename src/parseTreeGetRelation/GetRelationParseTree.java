package parseTreeGetRelation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class GetRelationParseTree {
	
	private String sentence = null;
	
	// Three relation extraction rules.
	private String[] relationPPIRule = new String[]
			{
				"/^NN.*/=GeneA .. (/^NN.*/=GeneB .. /^VB.*|^NN.*/=Relation)",
				"/^NN.*/=GeneA .. (/^VB.*/=Relation .. /^NN.*|^CD.*/=GeneB)",
				"/^VB.*|^NN.*/=Relation .. (/^NN.*/=GeneA .. /^NN.*/=GeneB)"
			};
	
	public GetRelationParseTree() {
		super();
		this.sentence = "";
	}
	
	public GetRelationParseTree(String sentence) {
		super();
		this.sentence = sentence;
	}
	
	// 区别模式匹配中加括号的区别以及括号加在某个位置的区别
	public void getRelateion(LexicalizedParser lParser,
			HashSet<String> geneSet,
			HashSet<String> relationKeySet)
		{
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory
				.getTokenizer(new StringReader(sentence));

		List<CoreLabel> rawWords = tok.tokenize();
		
		// 解析成语法树比较耗时
		Tree parseTree = lParser.apply(rawWords);
		
		//parseTree.pennPrint();
		
		System.out.println("=============================================");
		System.out.println(sentence);
		System.out.println(parseTree.taggedYield());
		System.out.println(parseTree.taggedLabeledYield());
		System.out.println("---------------------------------------------");
		int k = 0;
		for (String RuleStr: relationPPIRule) {
			relationExtract(parseTree, RuleStr, geneSet, relationKeySet, k);
			k += 1;
		}
		System.out.println("=============================================\n\n");
	}
	
	/*
	 * 这里需要进一步处理：
	 * 1. 判断A、C是否在Gene库中，B是否是关系词(访问Gene库文件、关系词库文件)
	 * 2. 判断在三种情况下PPI、IPP、PIP三种情况下中I的词性，PP之间词的个数
	 * 	  1> PIP：I的词性常为VP或NN
	 *    2> PPI/IPP情况下I的词性，PP之间词的个数
	 * 3. 否定词识别
	 * */
	private void relationExtract(Tree parseTree,
				String RuleStr,
				HashSet<String> geneSet,
				HashSet<String> relationKeySet,
				int k
				)
	{
		// 区别模式匹配中加括号的区别以及括号加在某个位置的区别
		TregexPattern tregrex = TregexPattern.compile(RuleStr);
		TregexMatcher mat = tregrex.matcher(parseTree);
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
				switch (k)
				{
					case 0: // PPI
						System.out.println("---------------------------------------------\nPPI:");
						System.out.println(GeneAStr + "\t" + GeneBStr + "\t" + relationStr);
						break;
					case 1: // PIP
						System.out.println("---------------------------------------------\nPIP:");
						System.out.println(GeneAStr + "\t" + relationStr + "\t" + GeneBStr);
						break;
					case 2: // IPP
						System.out.println("---------------------------------------------\nIPP:");
						System.out.println(relationStr + "\t" + GeneAStr + "\t" + GeneBStr);
						break;
					default:
						break;
				}
			}
		}
	}
	
	/*
	 * 从TregexMatcher中根据Tree结点名称得到相应的结点值
	 * */
	private String getStrFromTregexMatcher(TregexMatcher matcher, String nodeName) {
		Tree gene = matcher.getNode(nodeName);
		ArrayList<Word> valueList = gene.yieldWords();
		String geneNameStr = valueList.get(0).toString();
		return geneNameStr.trim();
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
}
