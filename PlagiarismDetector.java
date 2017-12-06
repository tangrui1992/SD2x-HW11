

import java.io.File;
import java.util.*;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

public class PlagiarismDetector {

	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {
		File dirFile = new File(dirName);
		String[] files = dirFile.list();
		int filesLength = files.length;
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();
		
		for (int i = 0; i < filesLength; i++) {
			String file1 = files[i];

			for (int j = 0; j < filesLength; j++) { 
				String file2 = files[j];
				
				Set<String> file1Phrases = createPhrases(dirName + "/" + file1, windowSize); 
				Set<String> file2Phrases = createPhrases(dirName + "/" + file2, windowSize); 
				//TODO should use maps to store sets of phrases, and abandon the use of the loops
				
				if (file1Phrases == null || file2Phrases == null)
					return null;
				
				Set<String> matches = findMatches(file1Phrases, file2Phrases);
				//TODO get the methods createPhrases() and findMatches() out of the loops
				
				if (matches == null)
					return null;

				int matchesSize = matches.size();
				if (matchesSize > threshold) {
					String key = file1 + "-" + file2;
					if (numberOfMatches.containsKey(file2 + "-" + file1) == false && file1.equals(file2) == false) {
						numberOfMatches.put(key,matchesSize);
					}
					//TODO definitely get the "file1.equals(file2) == false" to the front
				}				
			}
			
		}		
		
		return sortResults(numberOfMatches);

	}

	
	/*
	 * This method reads the given file and then converts it into a Collection of Strings.
	 * It does not include punctuation and converts all words in the file to uppercase.
	 */
	protected static List<String> readFile(String filename) {
		if (filename == null) return null;
		
//		List<String> words = new LinkedList<String>();
		List<String> words = new ArrayList<String>(); // because of frequent getting from the list
		
		try {
			Scanner in = new Scanner(new File(filename));
			while (in.hasNext()) {
				words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return words;
	}

	
	/*
	 * This method reads a file and converts it into a Set/List of distinct phrases,
	 * each of size "window". The Strings in each phrase are whitespace-separated.
	 */
	protected static Set<String> createPhrases(String filename, int window) {
		if (filename == null || window < 1) return null;
				
		List<String> words = readFile(filename);
		int wordsSize = words.size(); // TODO the readfile() can return an object containing this size!!!!!!!!!!!!!
		Set<String> phrases = new HashSet<String>();
		
		for (int i = 0; i < wordsSize - window + 1; i++) {
			String phrase = "";
			for (int j = 0; j < window; j++) {
				phrase += words.get(i+j) + " "; // words should use arraylist for fast getting
			}

			phrases.add(phrase);

		}
		
		return phrases;		
	}

	

	
	/*
	 * Returns a Set of Strings that occur in both of the Set parameters.
	 * However, the comparison is case-insensitive.
	 */
	protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
	
		Set<String> matches = new HashSet<String>();
		
		if (myPhrases != null && yourPhrases != null) {
		
			// TODO seems this loop shit could be modified and improved
			for (String mine : myPhrases) {
				for (String yours : yourPhrases) {
					if (mine.equalsIgnoreCase(yours)) {
						matches.add(mine);
					}
				}
			}
		}
		return matches;
	}
	
	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	protected static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
		
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		Map<String, Integer> copy = new HashMap<String, Integer>();

		
		for (String key : possibleMatches.keySet()) { // TODO use the value set maybe?
			copy.put(key, possibleMatches.get(key));
		}	
		
		LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>(); // TODO use treemap maybe?

		for (int i = 0; i < copy.size(); i++) {
			int maxValue = 0;
			String maxKey = null;
			for (String key : copy.keySet()) {
				int currentvalue = copy.get(key);
				if (currentvalue > maxValue) {
					maxValue = currentvalue;
					maxKey = key;
				}
			}
			
			list.put(maxKey, maxValue);
			
			copy.put(maxKey, -1); // TODO WTF is the (Map)copy doing here?! Delete! -- okay turns out it has a purpose
		}

		return list;
	}
	
	/*
	 * This method is here to help you measure the execution time and get the output of the program.
	 * You do not need to consider it for improving the efficiency of the detectPlagiarism method.
	 */
    public static void main(String[] args) {
    	if (args.length == 0) {
    		System.out.println("Please specify the name of the directory containing the corpus.");
    		System.exit(0);
    	}
    	String directory = args[0];
    	long start = System.currentTimeMillis();
    	Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
    	long end = System.currentTimeMillis();
    	double timeInSeconds = (end - start) / (double)1000;
    	System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
    	Set<Map.Entry<String, Integer>> entries = map.entrySet();
    	for (Map.Entry<String, Integer> entry : entries) {
    		System.out.println(entry.getKey() + ": " + entry.getValue());
    	}
    }

}
