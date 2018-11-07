import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * The class Strategy has two method
 * The method chooseNextCharacter is for computer guesser to use, when human is the chooser
 * The method humanGuess is for human guesser to use, when computer is the chooser
 * including the way of using reverse binary to decide
 * 
 * @author Yifan Peng
 * Date: 08/09/2017
 *
 */
public class Strategy {

	/**
	 * This method is for computer guesser use, 
	 * give the dictionary and current word, determine
	 * which character could be used for guess.
	 * 
	 * @param dic the whole dictionary           
	 * @param word the Word object during the game
	 *            
	 */
	public static char chooseNextCharacter(Dictionary dict, Word word) {
		ArrayList<String> qualifieds = dict.getAllQualifiedWords(word);

		// add all letter count to rateSum and per count to sumOfLetter
		double[] sumPerLetter = new double[26];
		for (int i = 0; i < qualifieds.size(); ++i) {
			String qualified = qualifieds.get(i);
			
			// sumLock is used to prevent add same letter twice in one word
			boolean[] sumLock = new boolean[26];
			for (int j = 0; j < qualified.length(); ++j) {
				int offset = qualified.charAt(j) - 'a';
				if (!sumLock[offset]) {
					sumPerLetter[offset] += 1;
					sumLock[offset] = true;
				}
			}
		}

		// reset the count of the used letter to 0
		ArrayList<Character> guessLog = word.getGuessLog();
		for (int i = 0; i < guessLog.size(); ++i) {
			char ch = Character.toLowerCase(guessLog.get(i));
			sumPerLetter[ch - 'a'] = 0;
		}
		
		// calculate the total sum of all letters
		for (int i = 0; i < sumPerLetter.length; ++i) {
			if (i != 0) {
				sumPerLetter[i] += sumPerLetter[i - 1];
			}
		}
		
		// prevent divide by 0
		if (sumPerLetter[25] == 0) {
			return ' ';
		}

		// calculate the percentage
		for (int i = 0; i < 26; ++i) {
			sumPerLetter[i] = sumPerLetter[i] / sumPerLetter[25];
		}
		
		// roll dice
		double dice = Project3.prng.nextDouble();
		for (int i = 0; i < 26; ++i) {
			if (i == 0 && dice < sumPerLetter[0]) {
				return 'a';
			} else if (i == 26 - 1 && dice >= sumPerLetter[i]) {
				return 'z';
			} else if (i != 0 && dice >= sumPerLetter[i - 1] && dice < sumPerLetter[i]) {
				return (char) ('a' + i);
			}
		}

		return ' ';
	}

	/**
	 * The method humanGuess is for human guesser use
	 * including the way of using reverse binary to decide
	 * @param ch
	 * @param word
	 * @param dict
	 * @return the ArrayList
	 */
	public static ArrayList<Integer> humanGuess(char ch, Word word, Dictionary dict) {
		ArrayList<String> candidatesString = dict.getAllQualifiedWords(word);

		// Convert to ArrayList<Word> and set guessLog for all Words
		ArrayList<Word> candidates = new ArrayList<Word>();
		for (int i = 0; i < candidatesString.size(); ++i) {
			Word candidate = new Word(candidatesString.get(i));
			candidate.setGuessLog(word.getGuessLog());
			candidates.add(candidate);
		}

		// the map is binary map to all possible word
		HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
		for (int i = 0; i < candidates.size(); ++i) {
			Word candidate = candidates.get(i);
			ArrayList<Integer> pos = candidate.guess(ch);
			int key = 0;
			for (int j = 0; j < pos.size(); ++j) {
				key += Math.pow(2, pos.get(j));
			}

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<String>());
			}
			map.get(key).add(candidate.getWord());
		}

		int maxSize = 0;
		int minKey = Integer.MAX_VALUE;
		for (Map.Entry<Integer, ArrayList<String>> entry : map.entrySet()) {
			int currentKey = entry.getKey();
			int entrySize = entry.getValue().size();

			if (entrySize > maxSize || (entrySize == maxSize && minKey > currentKey)) {
				minKey = currentKey;
				maxSize = entrySize;
			}
		}

		ArrayList<String> wordList = map.get(minKey);
		Collections.sort(wordList);

		if (!wordList.isEmpty()) {
			String result = wordList.get(0);
			word.setWord(result);
			return word.guess(ch);
		}
		return new ArrayList<Integer>();
	}
}
