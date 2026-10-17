package sportspulse;

import java.util.*;

/**
 * MODULE 2: Suffix Structures - Document Similarity (M2 - KAGGLE DATASET USER INPUT DRIVEN)
 * Algorithms: Suffix Array, Kasai's LCP Array
 */
public class M2_SuffixStructures {

    public static class Suffix {
        int index;
        String text;

        public Suffix(int index, String text) {
            this.index = index;
            this.text = text;
        }
    }

    public static Integer[] buildSuffixArray(String text) {
        int n = text.length();
        Suffix[] suffixes = new Suffix[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = new Suffix(i, text.substring(i));
        }
        Arrays.sort(suffixes, (a, b) -> a.text.compareTo(b.text));

        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) {
            sa[i] = suffixes[i].index;
        }
        return sa;
    }

    public static int[] buildLCPArray(String text, Integer[] sa) {
        int n = text.length();
        int[] lcp = new int[n];
        int[] invSa = new int[n];

        for (int i = 0; i < n; i++) {
            invSa[sa[i]] = i;
        }

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (invSa[i] == n - 1) {
                k = 0;
                continue;
            }
            int j = sa[invSa[i] + 1];
            while (i + k < n && j + k < n && text.charAt(i + k) == text.charAt(j + k)) {
                k++;
            }
            lcp[invSa[i]] = k;
            if (k > 0) k--;
        }
        return lcp;
    }

    public static String findLongestRepeatedPhrase(String text, Integer[] sa, int[] lcp) {
        int maxLen = 0;
        int maxIdx = -1;
        for (int i = 0; i < text.length() - 1; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                maxIdx = sa[i];
            }
        }
        if (maxLen > 0 && maxIdx != -1) {
            return text.substring(maxIdx, maxIdx + maxLen);
        }
        return "No repeated phrase found";
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 2: SUFFIX STRUCTURES (M2 - KAGGLE DATASET)");
        System.out.println("=================================================");

        String filterTeam = "";
        if (scanner != null) {
            System.out.print("Enter team/player filter for match commentary (press Enter for all): ");
            if (scanner.hasNextLine()) {
                filterTeam = scanner.nextLine().trim();
            }
        }

        List<DatasetLoader.BatRecord> batRecords = DatasetLoader.getBatRecords();
        StringBuilder reportBuilder = new StringBuilder();
        int count = 0;

        for (DatasetLoader.BatRecord r : batRecords) {
            if (!r.howOut.isEmpty() && !r.howOut.equalsIgnoreCase("not out")) {
                if (filterTeam.isEmpty() || r.matchName.toLowerCase().contains(filterTeam.toLowerCase()) || r.playerName.toLowerCase().contains(filterTeam.toLowerCase())) {
                    reportBuilder.append(r.howOut).append(" | ");
                    count++;
                    if (count >= 40) break;
                }
            }
        }

        if (reportBuilder.length() < 10) {
            reportBuilder.append("c Kaif b Agarkar | c Dravid b Sriram | lbw b Agarkar | c Kaif b Agarkar");
        }

        String matchReport = reportBuilder.toString();
        System.out.println("\n[USER INPUT FILTER]: " + (filterTeam.isEmpty() ? "None (Full Dataset)" : "'" + filterTeam + "'"));
        System.out.println("[KAGGLE DATASET COMMENTARY CORPUS (Extracted " + count + " dismissal lines)]");
        System.out.println("\"" + (matchReport.length() > 150 ? matchReport.substring(0, 150) + "..." : matchReport) + "\"\n");

        Integer[] sa = buildSuffixArray(matchReport);
        System.out.println("1. Suffix Array (Top 5 Lexicographical Suffixes):");
        for (int i = 0; i < Math.min(5, sa.length); i++) {
            String snippet = matchReport.substring(sa[i]);
            if (snippet.length() > 50) snippet = snippet.substring(0, 50) + "...";
            System.out.printf("   SA[%d] = %3d  ->  \"%s\"\n", i, sa[i], snippet);
        }

        int[] lcp = buildLCPArray(matchReport, sa);
        System.out.println("\n2. Kasai's LCP Array Computed across " + sa.length + " suffixes.");

        String longestPhrase = findLongestRepeatedPhrase(matchReport, sa, lcp);
        System.out.println("\n3. Longest Common Highlight Phrase in Kaggle Corpus:");
        System.out.println("   Detected Repeated Phrase: \"" + longestPhrase.trim() + "\"");
        System.out.println("-------------------------------------------------\n");
    }
}
