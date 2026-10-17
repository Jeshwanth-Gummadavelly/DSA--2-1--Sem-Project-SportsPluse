package sportspulse;

import java.util.*;

/**
 * MODULE 1: String Search Algorithms (M1 - CO1) - KAGGLE DATASET USER INPUT DRIVEN
 * Algorithms: KMP, Rabin-Karp, Aho-Corasick Multi-Keyword Detector
 */
public class M1_StringAlgorithms {

    // 1. KMP ALGORITHM
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty()) return matches;

        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);

        int i = 0, j = 0;
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0, i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    // 2. RABIN-KARP ALGORITHM
    public static List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || pattern.length() > text.length()) {
            return matches;
        }

        int d = 256, q = 101;
        int n = text.length();
        int m = pattern.length();
        int pHash = 0, tHash = 0, h = 1;

        for (int i = 0; i < m - 1; i++) h = (h * d) % q;
        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % q;
            tHash = (d * tHash + text.charAt(i)) % q;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) matches.add(i);
            }
            if (i < n - m) {
                tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % q;
                if (tHash < 0) tHash += q;
            }
        }
        return matches;
    }

    // 3. AHO-CORASICK MULTI-KEYWORD SEARCH
    public static class AhoCorasick {
        private static class Node {
            Map<Character, Node> children = new HashMap<>();
            Node fail = null;
            List<String> output = new ArrayList<>();
        }

        private final Node root = new Node();

        public void addWord(String word) {
            Node curr = root;
            for (char ch : word.toLowerCase().toCharArray()) {
                curr = curr.children.computeIfAbsent(ch, k -> new Node());
            }
            curr.output.add(word);
        }

        public void buildFailureLinks() {
            Queue<Node> queue = new LinkedList<>();
            for (Node child : root.children.values()) {
                child.fail = root;
                queue.add(child);
            }
            while (!queue.isEmpty()) {
                Node curr = queue.poll();
                for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                    char ch = entry.getKey();
                    Node child = entry.getValue();
                    Node temp = curr.fail;
                    while (temp != null && !temp.children.containsKey(ch)) {
                        temp = temp.fail;
                    }
                    child.fail = (temp != null) ? temp.children.get(ch) : root;
                    child.output.addAll(child.fail.output);
                    queue.add(child);
                }
            }
        }

        public Map<String, List<Integer>> search(String text) {
            Map<String, List<Integer>> results = new HashMap<>();
            Node curr = root;
            String lowerText = text.toLowerCase();
            for (int i = 0; i < lowerText.length(); i++) {
                char ch = lowerText.charAt(i);
                while (curr != null && !curr.children.containsKey(ch)) {
                    curr = curr.fail;
                }
                curr = (curr != null) ? curr.children.get(ch) : root;
                for (String matchedWord : curr.output) {
                    results.computeIfAbsent(matchedWord, k -> new ArrayList<>()).add(i - matchedWord.length() + 1);
                }
            }
            return results;
        }
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 1: STRING SEARCH ALGORITHMS (M1 - KAGGLE DATASET)");
        System.out.println("=================================================");

        String kmpPattern = "Dhoni";
        String rkPattern = "Agarkar";
        String keywordsInput = "SIX, FOUR, WICKET, Dhoni, Agarkar, Kaif";

        if (scanner != null) {
            System.out.print("Enter player/term to search using KMP (default: 'Dhoni'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) kmpPattern = line;
            }

            System.out.print("Enter term to search using Rabin-Karp (default: 'Agarkar'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) rkPattern = line;
            }

            System.out.print("Enter comma-separated keywords for Aho-Corasick (default: 'SIX, FOUR, WICKET, Dhoni'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) keywordsInput = line;
            }
        }

        System.out.println("\n[USER INPUT PARAMETERS]");
        System.out.println("   KMP Search Pattern        : \"" + kmpPattern + "\"");
        System.out.println("   Rabin-Karp Search Pattern : \"" + rkPattern + "\"");
        System.out.println("   Aho-Corasick Keywords     : " + keywordsInput);

        // Build commentary text from Kaggle bat.csv dataset
        List<DatasetLoader.BatRecord> batRecords = DatasetLoader.getBatRecords();
        StringBuilder datasetText = new StringBuilder();
        List<String> recordLines = new ArrayList<>();

        for (int i = 0; i < Math.min(100, batRecords.size()); i++) {
            DatasetLoader.BatRecord r = batRecords.get(i);
            String rec = r.playerName + " played for " + r.matchName + " scoring " + r.runs + " runs (" + r.fours + " FOURs, " + r.sixes + " SIXes). How out: " + (r.howOut.isEmpty() ? "not out" : r.howOut) + ".";
            datasetText.append(rec).append(" ");
            recordLines.add(rec);
        }

        String fullText = datasetText.toString();
        System.out.println("\n[KAGGLE DATASET REFERENCE SEARCH RESULTS]");
        System.out.println("Searching across " + batRecords.size() + " Kaggle records...\n");

        // 1. KMP Search Results
        List<Integer> kmpMatches = kmpSearch(fullText, kmpPattern);
        System.out.println("1. KMP Pattern Search for '" + kmpPattern + "':");
        System.out.println("   Total occurrences in dataset snippet: " + kmpMatches.size());
        int count = 0;
        for (String rec : recordLines) {
            if (rec.toLowerCase().contains(kmpPattern.toLowerCase())) {
                System.out.println("   - [Kaggle Record] " + rec);
                count++;
                if (count >= 5) break;
            }
        }
        if (count == 0) System.out.println("   - No matching records found for '" + kmpPattern + "' in dataset.");

        // 2. Rabin-Karp Search Results
        List<Integer> rkMatches = rabinKarpSearch(fullText, rkPattern);
        System.out.println("\n2. Rabin-Karp Rolling Hash Search for '" + rkPattern + "':");
        System.out.println("   Total occurrences in dataset snippet: " + rkMatches.size());
        count = 0;
        for (String rec : recordLines) {
            if (rec.toLowerCase().contains(rkPattern.toLowerCase())) {
                System.out.println("   - [Kaggle Record] " + rec);
                count++;
                if (count >= 5) break;
            }
        }
        if (count == 0) System.out.println("   - No matching records found for '" + rkPattern + "' in dataset.");

        // 3. Aho-Corasick Multi-Keyword Search
        AhoCorasick ac = new AhoCorasick();
        String[] kwArray = keywordsInput.split(",");
        List<String> keywords = new ArrayList<>();
        for (String kw : kwArray) {
            String trimmed = kw.trim();
            if (!trimmed.isEmpty()) {
                keywords.add(trimmed);
                ac.addWord(trimmed);
            }
        }
        ac.buildFailureLinks();

        Map<String, List<Integer>> acResults = ac.search(fullText);
        System.out.println("\n3. Aho-Corasick Multi-Keyword Event Detector:");
        System.out.println("   Tracked Keywords: " + keywords);
        for (String kw : keywords) {
            List<Integer> positions = acResults.getOrDefault(kw, Collections.emptyList());
            System.out.printf("   - Keyword '%-12s' found %2d times in Kaggle dataset.\n", kw, positions.size());
        }
        System.out.println("-------------------------------------------------\n");
    }
}
