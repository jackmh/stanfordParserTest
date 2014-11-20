import java.util.Collection;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;

import edu.stanford.nlp.semgraph.*;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;

/**
 * A small demo that shows how to convert a tree to a SemanticGraph and then run
 * a SemgrexPattern on it
 * 
 * @author John Bauer
 */
public class SemgrexDemo {
	public static void main(String[] args) {
		String treeString = "(ROOT  (S (NP (PRP$ My) (NN dog)) (ADVP (RB also)) (VP (VBZ likes) (S (VP (VBG eating) (NP (NN sausage))))) (. .)))";
		// Typically the tree is constructed by parsing or reading a
		// treebank. This is just for example purposes
		Tree tree = Tree.valueOf(treeString);

		// This creates English uncollapsed dependencies as a
		// SemanticGraph. If you are creating many SemanticGraphs, you
		// should use a GrammaticalStructureFactory and use it to generate
		// the intermediate GrammaticalStructure instead
		SemanticGraph graph = SemanticGraphFactory
				.generateUncollapsedDependencies(tree);
		
		IndexedWord root = graph.getFirstRoot();
		// IndexedWord subj = graph.getChildWithReln(root, "nsubj");
	 	System.out.println(graph.getFirstRoot());
	 	
	 	tree.pennPrint();
	 	System.out.println();
	 	System.out.println(tree.taggedYield());
	 	tree.taggedYield();
	 	System.out.println();
	 	for (TaggedWord word : tree.taggedYield()) {
	 		System.out.print(word.word() + " ");
		}
	 	System.out.println('\n');

		// Alternatively, this could have been the Chinese params or any
		// other language supported. As of 2014, only English and Chinese
		TreebankLangParserParams params = new EnglishTreebankParserParams();
		GrammaticalStructureFactory gsf = params.treebankLanguagePack()
				.grammaticalStructureFactory(
						params.treebankLanguagePack()
								.punctuationWordRejectFilter(),
						params.typedDependencyHeadFinder());

		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		// Same graph, but with a DocId and an index included
		SemanticGraph graph2 = SemanticGraphFactory
				.generateUncollapsedDependencies(gs, "demo", 0);

		// Note the result is the same
		System.err.println(graph);
		System.err.println(graph2);

		SemgrexPattern semgrex = SemgrexPattern.compile("{}=A <<nsubj {}=B"); //
		SemgrexMatcher matcher = semgrex.matcher(graph);
		// Verb with subject found by this pattern is stored by the name "B"
		// This will produce two results on the given tree: "likes" is an
		// ancestor of both "dog" and "my" via the nsubj relation
		while (matcher.find()) {
			System.out.println(matcher.getMatch().word());
			System.err.println(matcher.getNode("A") + "\t<<nsubj\t" + matcher.getNode("B"));
		}
	}
}
