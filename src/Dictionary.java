import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is the encapsulation for words.txt, to read the whole words.txt,
 * and put every line into this.lines and providing a method to read word
 * randomly, including method pickRandomWord, pickRandomWordInLength, and method
 * getAllQualifiedWords, and getAllWordInSameLength.
 * 
 * 
 * @author Yifan Peng
 * Date: 08/09/2017
 */
public class Dictionary {

	public ArrayList<String> lines = new ArrayList<String>();

	/**
	 * Read file to this.lines
	 * 
	 * @param path
	 * @throws IOException
	 */
	public Dictionary(String path) throws IOException {
		FileInputStream file = new FileInputStream(path);
		Project3.reader = new Scanner(file);
		while (Project3.reader.hasNextLine()) {
			lines.add(Project3.reader.nextLine());
		}
		Project3.reader.close();
	}

	/**
	 * This method picks a random line then return it in word list
	 * 
	 * @return a Word
	 */
	public Word pickRandomWord() {
		int randomIndex = Project3.prng.nextInt(this.lines.size());
		String randomWord = this.lines.get(randomIndex);
		return new Word(randomWord);
	}

	/**
	 * This method picks a random line in specified length
	 * 
	 * @param length the given length           
	 * @return a Word
	 */
	public Word pickRandomWordInLength(int length) {
		ArrayList<String> lines = this.getAllWordInSameLength(length);
		if (lines.size() == 0) {
			return new Word(length);
		}
		int randomIndex = Project3.prng.nextInt(lines.size());
		String randomWord = lines.get(randomIndex);
		return new Word(randomWord.length());
	}

	/**
	 * Get all word in dictionary which have the same length
	 * 
	 * @param length
	 * @return the ArrayList<String> form of words
	 */
	public ArrayList<String> getAllWordInSameLength(int length) {
		ArrayList<String> rtn = new ArrayList<String>();
		for (int i = 0; i < this.lines.size(); ++i) {
			if (this.lines.get(i).length() == length) {
				rtn.add(this.lines.get(i));
			}
		}
		return rtn;
	}

	/**
	 * Return a list of String which is qualified for the given word
	 * 
	 * @param word
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getAllQualifiedWords(Word word) {
		ArrayList<String> candidates = this.getAllWordInSameLength(word.getLength());

		ArrayList<String> qualifieds = new ArrayList<String>();
		for (int i = 0; i < candidates.size(); ++i) {
			String candidate = candidates.get(i);
			if (word.isLikely(candidate)) {
				qualifieds.add(candidate);
			}
		}

		return qualifieds;
	}
}
