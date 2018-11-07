//Files: Project3.java, Dictionary.java, Word.java, Strategy.java, words.txt
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Project3 {

	public static Scanner stdin = new Scanner(System.in); // standard input Scanner
	public static Scanner reader;
	// Pseudo random number generator and it's seed
	private static final int SEED = 777;
	public static Random prng = new Random(SEED);
	public static final int MAX_WORD_LENGTH = 30;

	// Print three things:
	// 1. Letters guessed log
	// 2. Strikes status
	// 3. Word reveal status
	public static void printScore(Word word, int strike) {
		ArrayList<Character> guessLog = word.getGuessLog();
		if (guessLog.isEmpty()) {
			System.out.println("No letters guessed yet.");
		} else {
			System.out.print("Letters guessed so far: ");
			for (int i = 0; i < guessLog.size(); ++i) {
				System.out.print(guessLog.get(i) + ((i == guessLog.size() - 1) ? "" : " "));
			}
			System.out.println("");
		}

		if (strike == 0) {
			System.out.println("No strikes yet -- all ten still remaining.");
		} else {
			System.out.println(
					"Guesser currently has " + strike + " strikes (" + (10 - strike) + " to go until automatic loss).");
		}

		System.out.print("Word revealed thus far: " + word);
	}

	// Convert ArrayList<Integer> to string like "[1, 2]"
	public static String posArrayToString(ArrayList<Integer> posArray) {
		String result = "";
		for (int i = 0; i < posArray.size(); ++i) {
			result += posArray.get(i) + 1;
			if (i != posArray.size() - 1) {
				result += ", ";
			}
		}
		return "[" + result + "]";
	}

	// main method starts from here
	public static void main(String[] args) throws IOException {
		Dictionary dict = new Dictionary("words.txt");

		while (true) {
			String role = "";// save the user input role
			boolean humanIsGuesser = true;
			Word word = null;
			int strike = 0;

			// This loop prompts user to enter until entering 'C' or 'G', also counting lowerCase input into upperCase
			do {
				System.out.print("Do you wish to be the word [C]hooser or the word [G]uesser: ");
				role = stdin.nextLine().toUpperCase();
			} while (role.length() == 0 || !(role.startsWith("C") || role.startsWith("G")));

			humanIsGuesser = role.startsWith("G");

			if (humanIsGuesser) {
				word = dict.pickRandomWord();
			} else {
				do {
					System.out.print("\nHow many letters are in your chosen word: ");
					String letters = stdin.nextLine();

					if (letters.length() == 0) {
						continue;
					}

					reader = new Scanner(letters);
					if (reader.hasNextInt()) {
						int length = reader.nextInt();
						if (length <= 0 || length > MAX_WORD_LENGTH) {
							System.out.printf("Your word must be have between 1 and %d letters, try again.\n",
									MAX_WORD_LENGTH);
							reader.close();
							continue;
						}
						word = dict.pickRandomWordInLength(length);
						reader.close();
						break;
					} else {
						System.out.println("You must enter an integer, try again.");
						reader.close();
						continue;
					}
				} while (true);
			}
			// if the strikes < 10 and the word hasn't been guessed successfully, keep doing this loop
			while (strike < 10 && !word.isResolved()) {
				String input = "";
				printScore(word, strike);

				if (humanIsGuesser) {
					do {
						System.out.print("\n\nEnter a letter: ");
						input = stdin.nextLine();
						if (input.length() == 0) {
							printScore(word, strike);
						}
					} while (input.length() == 0);
					char guess = input.toLowerCase().charAt(0);
					ArrayList<Integer> rtn = Strategy.humanGuess(guess, word, dict);
					if (rtn.isEmpty()) {
						++strike;
						System.out.printf("The letter '%c' is not in the word -- Strike!\n", guess);
					} else {
						System.out.printf("The letter '%c' is at positions: %s\n", guess, posArrayToString(rtn));
					}
				} else {
					System.out.println("");
					char guess = Strategy.chooseNextCharacter(dict, word);
					if (guess == ' ') {
						System.out.println("The computer does not know your word.");
						break;
					}

					System.out.printf("\nThe computer chooses the letter '%c' for its turn.\n", guess);
					do {
						System.out.print("Enter locations that computer's guess appears in your word (0 if none): ");
						input = stdin.nextLine();

						// validation
						if (input.length() == 0) {
							System.out.println("You must enter at least one number, try again.");
							continue;
						}

						// new a Scanner for reading position ArrayList
						reader = new Scanner(input);
						boolean validPosArray = true;
						ArrayList<Integer> pos = new ArrayList<Integer>();
						while (reader.hasNext()) {
							int prev = -1;
							if (pos.size() > 0) {
								prev = pos.get(pos.size() - 1);
							}
							if (prev == 0) {
								System.out.println("There should be no entries after a 0");
								validPosArray = false;
							}
							if (!reader.hasNextInt()) {
								System.out.println("All entries must be integers, try again.");
								validPosArray = false;
								break;
							}
							int current = reader.nextInt();
							if (prev > current) {
								System.out.println("Positions must be entered in increasing order, try again.");
								validPosArray = false;
							}
							if (current < 0 || current > word.getLength()) {
								System.out.printf("Numbers must be in range of 0 up to %d, the word length.\n",
										word.getLength());
								validPosArray = false;
							}
							if (!validPosArray) {
								break;
							}
							pos.add(current);
						}
						reader.close();

						if (!validPosArray) {
							continue;
						}

						word.unmask(guess, pos);

						if (pos.get(0) == 0) {
							++strike;
						}

						break;
					} while (true);
				}
			}

			if (strike >= 10 || !word.isResolved()) {
				System.out.println("\nThe guesser has lost by not finding the word before reaching ten strikes.");
				if (humanIsGuesser) {
					System.out.printf("\nThe computer had chosen the word '%s'.\n", word.getWord().toLowerCase());
				} else {
					String result = "";
					do {
						System.out.print("\nThe Chooser should reveal their word now: ");
						result = stdin.nextLine().toLowerCase();
					} while (result.length() == 0);

					if (result.length() != word.getLength()) {
						System.out.println("That does not match the length you stated at the start of the game.");
					}
					ArrayList<Character> guessLog = word.getGuessLog();
					for (int i = 0; i < guessLog.size(); ++i) {
						char ch = guessLog.get(i);
						if ((ch >= 'a' && ch <= 'z' && result.indexOf(ch) >= 0)
								|| (ch >= 'A' && ch <= 'Z' && result.indexOf(ch - 'A' + 'a') == -1)) {
							System.out.printf(
									"Your statement on my guess '%c' is not consistent with your reported solution.\n",
									ch);
						}
					}
				}
			} else {
				System.out.printf("\nThe guesser has won by finding the word '%s' before the tenth strike.\n",
						word.toString(false));
			}

			String contOrNot = "";
			do {
				System.out.print("Do you wish to play again (Y/N)? ");
				contOrNot = stdin.nextLine();
			} while (contOrNot.length() == 0);
			if (contOrNot.toLowerCase().startsWith("n")) {
				break;
			}
		}
	}
}
