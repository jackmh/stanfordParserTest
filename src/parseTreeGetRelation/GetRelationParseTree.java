package parseTreeGetRelation;

import java.awt.List;
import java.io.StringReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;

public class GetRelationParseTree {
	
	private static String sentence = null;
	private LexicalizedParser lParser;
	
	private String relationRulePPI = "/^NN.*/=GeneA .. (/^NN.*/=GeneB .. /^VB.*|^NN.*/=Relation)";
	private String relationRulePIP = "/^NN.*/=GeneA .. (/^VB.*/=Relation .. /^NN.*|^CD.*/=GeneB)";
	private String relationRuleIPP = "/^VB.*|^NN.*/=Relation .. (/^NN.*/=GeneA .. /^NN.*/=GeneB)";

	public GetRelationParseTree() {
		super();
		this.sentence = "";
	}
	
	public GetRelationParseTree(String sentence) {
		super();
		this.sentence = sentence;
	}
	
	/*
	 * 这里需要进一步处理：
	 * 1. 判断A、C是否在Gene库中，B是否是关系词(访问Gene库文件、关系词库文件)
	 * 2. 判断在三种情况下PPI、IPP、PIP三种情况下中I的词性，PP之间词的个数
	 * 	  1> PIP：I的词性常为VP或NN
	 *    2> PPI/IPP情况下I的词性，PP之间词的个数
	 * 3. 否定词识别
	 * */
	// 区别模式匹配中加括号的区别以及括号加在某个位置的区别
	
	public static void getRelateion() {
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory
				.getTokenizer(new StringReader(sentence));
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
}
