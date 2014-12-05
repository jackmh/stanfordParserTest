package pubmedTextProcessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import config.config;

import parseTreeGetRelation.GetRelationParseTree;
import proteinREG.proteinREC;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class ppiTextProcess {
	
	private String pubmedID;
	private HashSet<String> interactiveProteinPairSet = new HashSet<String>();
	private String textOfInteractiveProtein;
	private String newPubmedText;
	
	/*
	* 1. 对摘要进行分句;
	* 2. 针对摘要中的每一句, 识别出其中的蛋白质并对蛋白质进行标准化
	* 3. 对每一句话应用Stanford Parser进行解析, 得到语法树
	* 4. 由3得到的语法树, 结合关系词、gene库找到候选的蛋白质相互作用对
	* */
	

	public String getNewPubmedText() {
		return newPubmedText;
	}

	public void setNewPubmedText(String newPubmedText) {
		this.newPubmedText = newPubmedText;
	}
	
	public String getPubmedID() {
		return pubmedID;
	}
	
	public void setPubmedID(String pubmedID) {
		this.pubmedID = pubmedID;
		if (interactiveProteinPairSet.size() > 0)
		{
			interactiveProteinPairSet.clear();
		}
		textOfInteractiveProtein = "";
		newPubmedText = "";
	}
	
	public HashSet<String> getInteractiveProteinPairSet() {
		return interactiveProteinPairSet;
	}
	
	public void setInteractiveProteinPairSet(
			HashSet<String> interactiveProteinPairSet) {
		this.interactiveProteinPairSet = interactiveProteinPairSet;
	}
	
	public String getTextOfInteractiveProtein() {
		return textOfInteractiveProtein;
	}
	
	public void setTextOfInteractiveProtein(String textOfInteractiveProtein) {
		this.textOfInteractiveProtein = textOfInteractiveProtein;
	}
	
	public ppiTextProcess() {
		textOfInteractiveProtein = "";
		newPubmedText = "";
	}
	
	public void pubmedTextProcessing(String pubmedTextFullName,
			HashSet<String> geneSet,
			HashSet<String> allKeysSets,
			HashSet<String> relationKeySet,
			HashMap<String, String> firstCharDict,
			HashMap<String, String> geneSynProteinDict,
			LexicalizedParser lParser
			)
	{
		if (pubmedTextFullName == "")
		{
			return ;
		}
		/*********************************************************/ 
        /*  
         * 1. Convert the Abstract text into sentenceList. each list contain of sentence list.
         * 2. In each paragraph, split each sentence with char[.?!] as default.
         */
		HashSet<String> interactiveProteinPairSet = new HashSet<String>();
		interactiveProteinPairSet.clear();
		List<List<HasWord>> sentenceListWord = new LinkedList<List<HasWord>>();
		
		Iterable<String> allLines = IOUtils.readLines(pubmedTextFullName);
		String newPubmedText = "", newTextOfRecProtein = "";
		for (String paragraph : allLines)
		{
			paragraph = paragraph.trim();
			if (paragraph.compareTo("") == 0)
			{
				continue;
			}
			Reader paraReader = new StringReader(paragraph);
			DocumentPreprocessor dPreprocessor = new DocumentPreprocessor(paraReader);
			Iterator<List<HasWord>> it = dPreprocessor.iterator();
			// each sentence in paragraph.
			sentenceListWord.clear();
			while (it.hasNext())
			{
				List<HasWord> sentenceHasWords = it.next();
				sentenceListWord.add(sentenceHasWords);
			}
			// convert the list into string, append it into newAbstractText
			for (List<HasWord> sentence : sentenceListWord)
			{
				proteinREC proteinSentRec = new proteinREC();
				proteinSentRec.proteinRecognition(sentence, allKeysSets, relationKeySet, firstCharDict, geneSynProteinDict);
				newPubmedText += proteinSentRec.getNewSentenceStr() + " ";
				if (config.__DEBUG__ == true)
				{
					System.out.println(proteinSentRec.getNumberOfRecognitionProteins() + "\t" + proteinSentRec.getNumberOfRelationWords() +
							"\n" + proteinSentRec.getPreSentenceStr() + "\n" + proteinSentRec.getNewSentenceStr() + "\n");
				}
				if (proteinSentRec.getNumberOfRecognitionProteins() >= 2 && 
						proteinSentRec.getNumberOfRelationWords() > 0)
				{
					newTextOfRecProtein += proteinSentRec.getPreSentenceStr() + proteinSentRec.getNewSentenceStr() + "\n";
					GetRelationParseTree proteinRelationExtracTree  = new GetRelationParseTree(proteinSentRec.getNewSentenceStr());
					newTextOfRecProtein += proteinRelationExtracTree.getRelateion(lParser, geneSet, relationKeySet, proteinSentRec);
					
					if (proteinRelationExtracTree.getNumberOfInteractionPair() > 0)
					{
						HashSet<String> sentenceInteracticePairSet = proteinRelationExtracTree.getInteractionPairSet();
						for (String pairStr : sentenceInteracticePairSet)
						{
							String[] GenePair = pairStr.split("\\|");
							if (GenePair.length < 2)
								continue;
							String GeneA = GenePair[0].trim();
							String GeneB = GenePair[1].trim();
							String oppositeGenePair = GeneB + "|" + GeneA;
							if (!interactiveProteinPairSet.contains(pairStr) &&
									!interactiveProteinPairSet.contains(oppositeGenePair))
							{
								interactiveProteinPairSet.add(pairStr);
							}
						}
					}
					
				}
			}
			newPubmedText = newPubmedText.trim();
			newPubmedText += "\n";
		}
		if (config.__DEBUG__ == true)
		{
			System.out.println("\n--------------------------------------\n" + newPubmedText +
					"\n--------------------------------------\n");
		}
		setInteractiveProteinPairSet(interactiveProteinPairSet);
		setNewPubmedText(newPubmedText);
		setTextOfInteractiveProtein(
				getInteractiveProteinText(interactiveProteinPairSet, newTextOfRecProtein));
		if (config.__WriteIntoFileFlag__ == true)
		{
			writePubmedTextIntoFile();
		}
	}
	
	private String getInteractiveProteinText(HashSet<String> interactiveProteinPairSet, String newTextOfRecProtein)
	{
		String newInteractiveText = "";
		if (interactiveProteinPairSet.size() <= 0)
		{
			return newInteractiveText;
		}
		newInteractiveText = "# Pubmed id: " + getPubmedID();
		// get all interactive protein pair.
		boolean firstFlag = true;
		for (String proteinPair : interactiveProteinPairSet)
		{
			if (firstFlag == true)
			{
				newInteractiveText += "\t" + proteinPair;
				firstFlag = false;
				continue;
			}
			newInteractiveText += "," + proteinPair;
		}
		newInteractiveText += "\n------------------------------\n\n" + newTextOfRecProtein;
		return newInteractiveText;
	}
	
	private void writePubmedTextIntoFile()
	{
		String newfilename = config.DstDIR + File.separator + getPubmedID();
		try {
			FileWriter writerConn = new FileWriter(newfilename);
			writerConn.write(getTextOfInteractiveProtein());
			writerConn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
