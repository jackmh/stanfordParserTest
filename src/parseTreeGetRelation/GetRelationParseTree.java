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
	 * ������Ҫ��һ������
	 * 1. �ж�A��C�Ƿ���Gene���У�B�Ƿ��ǹ�ϵ��(����Gene���ļ�����ϵ�ʿ��ļ�)
	 * 2. �ж������������PPI��IPP��PIP�����������I�Ĵ��ԣ�PP֮��ʵĸ���
	 * 	  1> PIP��I�Ĵ��Գ�ΪVP��NN
	 *    2> PPI/IPP�����I�Ĵ��ԣ�PP֮��ʵĸ���
	 * 3. �񶨴�ʶ��
	 * */
	// ����ģʽƥ���м����ŵ������Լ����ż���ĳ��λ�õ�����
	
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
