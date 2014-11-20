/**
 * 
 */
package process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.util.Generics;

/**
 * @author jack_mhdong
 *
 */
public class wordToSentence {
	public wordToSentence() {
	}
	
	private static HashMap<String, String> DEFAULT_BOUNDARY = new HashMap<String, String>(){
		{put("-LRB-", "("); put("-LSB-", "["); put("-LCB-", "{");
		put("-RRB-", ")"); put("-RSB-", "]"); put("-RCB-", "}"); put(",", ",");}
	};
	
	private static HashSet<String> BoundaryLeftSet = new HashSet<String>() {
		{ add("("); add("["); add("{");}
	};
	
	/*
	 * Convert a word list to String. For example as followed:
	 * [Translin, repeats, d, -LRB-, GT, -RRB-, n, and, the, corresponding, transcripts, -LRB-, GU, -RRB-, n]
	 * 			||
	 * Translin repeats d(GT)n and the corresponding transcripts (GU)n
	 */
	public static String wordToString(ArrayList<String> hasWordList) {
		String NewSentence = "";
		int index = 0, len = hasWordList.size();
		while (index < len) {
			String word = hasWordList.get(index);
			if (index == 0)
			{
				NewSentence = word;
				if (DEFAULT_BOUNDARY.containsKey(word))
				{
					NewSentence = DEFAULT_BOUNDARY.get(word);
				}
			}
			else {
				if (DEFAULT_BOUNDARY.containsKey(word))
				{
					String value = DEFAULT_BOUNDARY.get(word);
					if (BoundaryLeftSet.contains(value)) {
						NewSentence += " " + value;
					}
					else {
						NewSentence += value;
					}
				}
				else {
					String preWord = hasWordList.get(index-1);
					if (DEFAULT_BOUNDARY.containsKey(preWord) && 
							BoundaryLeftSet.contains(DEFAULT_BOUNDARY.get(preWord))) {
						NewSentence += word;
					}
					else {
						NewSentence += " " + word;
					}
				}
			}
			index += 1;
		}
		return NewSentence;
	}
}
