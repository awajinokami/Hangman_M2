import java.util.ArrayList;

/**
 * This class is the encapsulation for the guessing word separate encapsulation
 * for the word we know and the word we don't know and including the guessing
 * method, unmask method, and having the method isLikely, and some getter and setter
 * and also having toString method, one is with padding, another is without padding.
 * 
 * @author Yifan Peng
 * Date: 08/09/2017
 * 
 */
public class Word {
	private String word = "";
	private boolean needGuess = true;
	private ArrayList<Character> guessLog = new ArrayList<Character>();

	/**
	 * The constructor is for human chooser, only length is provided
	 * 
	 * @param length
	 */
	public Word(int length) {
		for (int i = 0; i < length; ++i) {
			this.word += "*";
		}
		this.needGuess = false;
	}

	/**
	 * The constructor is for human guesser, the word is provided
	 * 
	 * @param word
	 */
	public Word(String word) {
		this.word = word.toUpperCase();
		this.needGuess = true;
	}

	/**
	 * This method is for human guesser to guess the character
	 * 
	 * @param ch character the user guessed            
	 * @return the position array list of the guessed character
	 */
	public ArrayList<Integer> guess(char ch) {
		ch = Character.toUpperCase(ch);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < this.word.length(); ++i) {
			if (ch == this.word.charAt(i)) {
				result.add(i);
			}
		}
		if (result.isEmpty()) {
			this.guessLog.add(Character.toLowerCase(ch));
		} else {
			this.guessLog.add(ch);
		}
		return result;
	}

	/**
	 * This method is for human chooser, to reveal the character under the mask in the specific position
	 * 
	 * @param ch the character the computer guessed          
	 * @param pos the position user type in
	 *            
	 */
	public void unmask(char ch, ArrayList<Integer> pos) {
		ch = Character.toUpperCase(ch);
		if (pos.get(0) == 0) { // not contains
			this.guessLog.add(Character.toLowerCase(ch));
		} else {
			this.guessLog.add(ch);
			String temp = "";
			for (int i = 0; i < this.word.length(); ++i) {
				if (pos.contains(i + 1)) {
					temp += ch;
				} else {
					temp += this.word.charAt(i);
				}
			}
			this.word = temp;
		}
	}

	/**
	 * the getter for guessLog
	 * 
	 * @return the private guessLog field
	 */
	public ArrayList<Character> getGuessLog() {
		return this.guessLog;
	}

	/**
	 * the setter for guessLog
	 * 
	 * @param guessLog
	 */
	public void setGuessLog(ArrayList<Character> guessLog) {
		this.guessLog = new ArrayList<Character>();
		for (int i = 0; i < guessLog.size(); ++i) {
			this.guessLog.add(guessLog.get(i));
		}
	}

	/**
	 * Through this method we can get the length of the word
	 * 
	 * @return the length of the word
	 */
	public int getLength() {
		return this.word.length();
	}

	/**
	 * the getter for word
	 * 
	 * @return String
	 */
	public String getWord() {
		return this.word;
	}

    /**
     * This method is for computer chooser, set the private field "word"
     * @param word the new word should replace the old word
     */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * For human guesser, it will check all character is in guessLog or not.
	 * For human chooser, it will check whether the word contains * or not
	 * 
	 * @return the word is resolved or not
	 */
	public boolean isResolved() {
		if (this.needGuess) {
			for (int i = 0; i < this.word.length(); ++i) {
				if (!this.guessLog.contains(this.word.charAt(i))) {
					return false;
				}
			}
			return true;
		} else {
			return this.word.indexOf('*') == -1;
		}
	}

	/**
	 * Return if all unmasked character is same in this and given word
	 * @param word the word need to be compared
	 *             
	 * @return boolean
	 */
	public boolean isLikely(String word) {
		if (this.word.length() != word.length()) {
			return false;
		}

		word = word.toUpperCase();
		String internalWord = this.toString(false);
		for (int i = 0; i < word.length(); ++i) {
			char theirs = word.charAt(i);
			char ours = internalWord.charAt(i);
			
			if (ours != '*' && ours != theirs) {
				return false;
			}
			
			if (this.guessLog.contains(Character.toLowerCase(theirs))) {
				return false;
			}
			
			if (ours == '*' && this.guessLog.contains(theirs)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * This method is converting word to String.
	 * For human guesser, even the word is known, it will use guessLog to mask character not guessed yet.
	 * For human chooser, all character in this.word will be printed.
	 * When parameter"withPadding" switch to true, it will add space around the character
	 * @param withPadding
	 *            
	 * @return String
	 */
	public String toString(boolean withPadding) {
		String temp = "";
		for (int i = 0; i < this.word.length(); ++i) {
			char currentChar = this.word.charAt(i);
			if (this.needGuess) {
				if (this.guessLog.contains(currentChar)) {
					temp += currentChar;
				} else {
					temp += '*';
				}
			} else {
				temp += currentChar;
			}
			if (withPadding && i != this.word.length()) {
				temp += ' ';
			}
		}
		return temp;
	}

	/**
	 * Override Object.toString()
	 */
	public String toString() {
		return this.toString(true);
	}
}
