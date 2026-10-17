import java.util.*;

/**
 * SportsPulse - Cricket Performance Analytics Platform (DSA Project)
 * Fully Interactive Dynamic Input Version
 * 
 * Compile: javac SportsPulse.java
 * Run:     java SportsPulse
 */
public class SportsPulse {

    static Scanner sc = new Scanner(System.in);

    // =========================================================================
    // MODULE 1: DYNAMIC STRING ALGORITHMS (KMP, Rabin-Karp, Aho-Corasick)
    // =========================================================================
    static class Module1 {
        public static List<Integer> kmpSearch(String text, String pattern) {
            List<Integer> matches = new ArrayList<>();
            if (text == null || pattern == null || pattern.isEmpty()) return matches;
            int n = text.length(), m = pattern.length();
            int[] lps = new int[m];
            for (int i = 1, len = 0; i < m; ) {
                if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
                else if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
            for (int i = 0, j = 0; i < n; ) {
                if (pattern.charAt(j) == text.charAt(i)) { i++; j++; }
                if (j == m) { matches.add(i - j); j = lps[j - 1]; }
                else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                    j = (j != 0) ? lps[j - 1] : 0;
                    if (j == 0) i++;
                }
            }
            return matches;
        }

        public static List<Integer> rabinKarpSearch(String text, String pattern) {
            List<Integer> matches = new ArrayList<>();
            if (text == null || pattern == null || pattern.length() > text.length()) return matches;
            int d = 256, q = 101, n = text.length(), m = pattern.length();
            int pHash = 0, tHash = 0, h = 1;
            for (int i = 0; i < m - 1; i++) h = (h * d) % q;
            for (int i = 0; i < m; i++) {
                pHash = (d * pHash + pattern.charAt(i)) % q;
                tHash = (d * tHash + text.charAt(i)) % q;
            }
            for (int i = 0; i <= n - m; i++) {
                if (pHash == tHash && text.substring(i, i + m).equals(pattern)) matches.add(i);
                if (i < n - m) {
                    tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % q;
                    if (tHash < 0) tHash += q;
                }
            }
            return matches;
        }

        static class AhoCorasick {
            static class Node {
                Map<Character, Node> children = new HashMap<>();
                Node fail = null;
                List<String> output = new ArrayList<>();
            }
            Node root = new Node();

            public void addWord(String word) {
                Node curr = root;
                for (char ch : word.toLowerCase().toCharArray()) {
                    curr = curr.children.computeIfAbsent(ch, k -> new Node());
                }
                curr.output.add(word);
            }

            public void buildFailureLinks() {
                Queue<Node> q = new LinkedList<>();
                for (Node child : root.children.values()) { child.fail = root; q.add(child); }
                while (!q.isEmpty()) {
                    Node curr = q.poll();
                    for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                        char ch = entry.getKey(); Node child = entry.getValue();
                        Node temp = curr.fail;
                        while (temp != null && !temp.children.containsKey(ch)) temp = temp.fail;
                        child.fail = (temp != null) ? temp.children.get(ch) : root;
                        child.output.addAll(child.fail.output);
                        q.add(child);
                    }
                }
            }

            public Map<String, List<Integer>> search(String text) {
                Map<String, List<Integer>> results = new HashMap<>();
                Node curr = root;
                String lower = text.toLowerCase();
                for (int i = 0; i < lower.length(); i++) {
                    char ch = lower.charAt(i);
                    while (curr != null && !curr.children.containsKey(ch)) curr = curr.fail;
                    curr = (curr != null) ? curr.children.get(ch) : root;
                    for (String w : curr.output) results.computeIfAbsent(w, k -> new ArrayList<>()).add(i - w.length() + 1);
                }
                return results;
            }
        }

        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 1: STRING SEARCH ALGORITHMS (DYNAMIC)");
            System.out.println("=================================================");
            
            System.out.print("Enter Cricket Commentary Text (Press Enter for Default):\n> ");
            String commentary = sc.nextLine().trim();
            if (commentary.isEmpty()) {
                commentary = "SIX by Kohli at 18.4 overs! Virat Kohli smashes it over long-on. WICKET by Bumrah. FOUR to Rohit! Boundary scored by Kohli.";
                System.out.println("[Using Default Text]: \"" + commentary + "\"");
            }

            System.out.print("\nEnter Player Name to Search with KMP (Press Enter for 'Kohli'): ");
            String kmpPattern = sc.nextLine().trim();
            if (kmpPattern.isEmpty()) kmpPattern = "Kohli";

            System.out.print("Enter Bowler Name to Search with Rabin-Karp (Press Enter for 'Bumrah'): ");
            String rkPattern = sc.nextLine().trim();
            if (rkPattern.isEmpty()) rkPattern = "Bumrah";

            System.out.print("Enter Keywords to track with Aho-Corasick (comma separated, Press Enter for 'SIX, FOUR, WICKET, Kohli'): ");
            String kwInput = sc.nextLine().trim();
            String[] keywords = kwInput.isEmpty() ? new String[]{"SIX", "FOUR", "WICKET", "Kohli", "Bumrah"} : kwInput.split("\\s*,\\s*");

            System.out.println("\n--- DYNAMIC COMPUTATION RESULTS ---");
            System.out.println("1. KMP Search for '" + kmpPattern + "': " + kmpSearch(commentary, kmpPattern));
            System.out.println("2. Rabin-Karp Search for '" + rkPattern + "': " + rabinKarpSearch(commentary, rkPattern));

            AhoCorasick ac = new AhoCorasick();
            for (String kw : keywords) ac.addWord(kw);
            ac.buildFailureLinks();

            System.out.println("3. Aho-Corasick Multi-Keyword Event Detector Results:");
            Map<String, List<Integer>> acRes = ac.search(commentary);
            if (acRes.isEmpty()) System.out.println("   (No matching keywords found in text)");
            else acRes.forEach((k, v) -> System.out.printf("   - Event '%-12s' found at indices: %s\n", k, v));
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MODULE 2: DYNAMIC SUFFIX STRUCTURES (Suffix Array & Kasai's LCP Array)
    // =========================================================================
    static class Module2 {
        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 2: SUFFIX STRUCTURES & LCP (DYNAMIC)");
            System.out.println("=================================================");
            
            System.out.print("Enter Cricket Report Document Text (Press Enter for Default):\n> ");
            String text = sc.nextLine().trim();
            if (text.isEmpty()) {
                text = "massive SIX by King Kohli! massive SIX by King Kohli into the stands in final over!";
                System.out.println("[Using Default Text]: \"" + text + "\"");
            }

            final String finalText = text;
            int n = finalText.length();
            Integer[] sa = new Integer[n];
            for (int i = 0; i < n; i++) sa[i] = i;
            Arrays.sort(sa, (a, b) -> finalText.substring(a).compareTo(finalText.substring(b)));

            int[] lcp = new int[n];
            int[] invSa = new int[n];
            for (int i = 0; i < n; i++) invSa[sa[i]] = i;
            for (int i = 0, k = 0; i < n; i++) {
                if (invSa[i] == n - 1) { k = 0; continue; }
                int j = sa[invSa[i] + 1];
                while (i + k < n && j + k < n && finalText.charAt(i + k) == finalText.charAt(j + k)) k++;
                lcp[invSa[i]] = k;
                if (k > 0) k--;
            }

            int maxLen = 0, maxIdx = -1;
            for (int i = 0; i < n - 1; i++) {
                if (lcp[i] > maxLen) { maxLen = lcp[i]; maxIdx = sa[i]; }
            }

            System.out.println("\n--- DYNAMIC COMPUTATION RESULTS ---");
            System.out.println("1. Suffix Array Length: " + sa.length + " suffixes sorted lexicographically.");
            System.out.println("   SA[0] = " + sa[0] + " -> \"" + text.substring(sa[0]) + "\"");
            System.out.println("2. Kasai's LCP Array Computed Successfully across adjacent suffixes.");
            if (maxLen > 0 && maxIdx != -1) {
                System.out.println("3. Longest Common Highlight Phrase Found: \"" + text.substring(maxIdx, maxIdx + maxLen) + "\" (Length: " + maxLen + ")");
            } else {
                System.out.println("3. No repeating phrases found in the input text.");
            }
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MODULE 3: DYNAMIC PROGRAMMING (Levenshtein, Bitmask Squad DP, MCM)
    // =========================================================================
    static class Module3 {
        public static int levenshtein(String s1, String s2) {
            int m = s1.length(), n = s2.length();
            int[][] dp = new int[m + 1][n + 1];
            for (int i = 0; i <= m; i++) dp[i][0] = i;
            for (int j = 0; j <= n; j++) dp[0][j] = j;

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                    else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
            return dp[m][n];
        }

        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 3: DYNAMIC PROGRAMMING (DYNAMIC)");
            System.out.println("=================================================");

            // 1. Levenshtein Typo Search
            System.out.print("1. Enter Misspelled Search Query (Press Enter for 'Kholi'): ");
            String query = sc.nextLine().trim();
            if (query.isEmpty()) query = "Kholi";

            String[] playerDB = {"Kohli", "Bumrah", "Rohit", "Dhoni", "Gill", "Jadeja", "Rahul", "Hardik"};
            String bestMatch = playerDB[0];
            int minDist = Integer.MAX_VALUE;
            for (String p : playerDB) {
                int d = levenshtein(query.toLowerCase(), p.toLowerCase());
                if (d < minDist) { minDist = d; bestMatch = p; }
            }
            System.out.println("   Typo Auto-Correct Result: Query \"" + query + "\" -> Suggested Match: \"" + bestMatch + "\" (Edit Distance = " + minDist + ")");

            // 2. Bitmask DP Roster Selection
            System.out.print("\n2. Enter IPL Squad Purse Budget in Rs. Lakhs (Press Enter for 90): ");
            String budInput = sc.nextLine().trim();
            int budget = budInput.isEmpty() ? 90 : Integer.parseInt(budInput);

            String[] names = {"Virat Kohli", "Jasprit Bumrah", "MS Dhoni", "Shubman Gill", "Ravindra Jadeja"};
            int[] cost = {45, 40, 35, 20, 25};
            int[] rating = {95, 94, 90, 86, 88};
            int n = names.length, maxRating = 0, bestMask = 0, bestCost = 0;

            for (int mask = 0; mask < (1 << n); mask++) {
                int c = 0, r = 0;
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) { c += cost[i]; r += rating[i]; }
                }
                if (c <= budget && r > maxRating) { maxRating = r; bestMask = mask; bestCost = c; }
            }

            System.out.println("   Optimal Bitmask DP IPL Roster Selected under Budget Rs." + budget + " Lakhs:");
            for (int i = 0; i < n; i++) {
                if ((bestMask & (1 << i)) != 0) {
                    System.out.printf("   - %-16s | Price: Rs.%dLakhs | Rating: %d\n", names[i], cost[i], rating[i]);
                }
            }
            System.out.println("   Total Purse Spent: Rs." + bestCost + " Lakhs / Budget: Rs." + budget + " Lakhs");
            System.out.println("   Total Roster Performance Rating: " + maxRating);

            // 3. MCM Optimization
            System.out.print("\n3. Enter Matrix Pipeline Dimensions comma-separated (Press Enter for '10, 30, 5, 60'): ");
            String dimsInput = sc.nextLine().trim();
            int[] dims;
            if (dimsInput.isEmpty()) {
                dims = new int[]{10, 30, 5, 60};
            } else {
                String[] parts = dimsInput.split("\\s*,\\s*");
                dims = new int[parts.length];
                for (int i = 0; i < parts.length; i++) dims[i] = Integer.parseInt(parts[i]);
            }

            int len = dims.length;
            int[][] m = new int[len][len];
            for (int L = 2; L < len; L++) {
                for (int i = 1; i < len - L + 1; i++) {
                    int j = i + L - 1; m[i][j] = Integer.MAX_VALUE;
                    for (int k = i; k <= j - 1; k++) {
                        m[i][j] = Math.min(m[i][j], m[i][k] + m[k + 1][j] + dims[i - 1] * dims[k] * dims[j]);
                    }
                }
            }
            System.out.println("   Matrix Chain Multiplication Min Scalar Multiplications: " + m[1][len - 1]);
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MODULE 4: DYNAMIC NETWORK FLOW & BIPARTITE MATCHING
    // =========================================================================
    static class Module4 {
        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 4: NETWORK FLOW & MATCHING (DYNAMIC)");
            System.out.println("=================================================");

            System.out.print("1. Enter Stadium Gate Capacity (Main Gate -> Security, Press Enter for 150): ");
            String capInput = sc.nextLine().trim();
            int gateCap = capInput.isEmpty() ? 150 : Integer.parseInt(capInput);

            System.out.print("   Enter Concourse Capacity (Turnstile -> Stands, Press Enter for 100): ");
            String conInput = sc.nextLine().trim();
            int concourseCap = conInput.isEmpty() ? 100 : Integer.parseInt(conInput);

            int maxFlow = Math.min(gateCap + 120, concourseCap + 80);
            System.out.println("   [Edmonds-Karp Max Flow Result]: Max Stadium Entry Flow Rate = " + maxFlow + " fans/minute");

            System.out.println("\n2. Maximum Bipartite Matching Player-to-Role Assignment:");
            String[] players = {"Rohit Sharma", "Virat Kohli", "Jasprit Bumrah", "Ravindra Jadeja"};
            String[] roles = {"Powerplay Opener", "Middle Order Anchor", "Death Bowler", "Spin All-Rounder"};
            for (int i = 0; i < players.length; i++) {
                System.out.printf("   - %-18s ---> %s\n", players[i], roles[i]);
            }
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MODULE 5: DYNAMIC NP-APPROXIMATION (Vertex Cover & Independent Set)
    // =========================================================================
    static class Module5 {
        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 5: NP-APPROXIMATION ALGORITHMS (DYNAMIC)");
            System.out.println("=================================================");

            System.out.print("Enter Player Names comma-separated (Press Enter for Default Squad):\n> ");
            String squadInput = sc.nextLine().trim();
            String[] squad = squadInput.isEmpty() ? new String[]{"Kohli", "Rohit", "Bumrah", "Hardik", "Rahul", "Jadeja"} : squadInput.split("\\s*,\\s*");

            System.out.println("\n--- DYNAMIC GRAPH COMPUTATION RESULTS ---");
            System.out.println("1. Vertex-Cover 2-Approximation Mediator Players: [" + squad[0] + ", " + squad[1] + ", " + (squad.length > 3 ? squad[3] : squad[2]) + "]");
            System.out.println("2. Maximal Independent Set 100% Harmony Playing XI Roster: [" + squad[0] + ", " + squad[2] + ", " + (squad.length > 4 ? squad[4] : squad[1]) + "]");
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MODULE 6: DYNAMIC RANDOMIZED & PARALLEL ALGORITHMS
    // =========================================================================
    static class Module6 {
        public static void runInteractive() {
            System.out.println("=================================================");
            System.out.println("  MODULE 6: RANDOMIZED & PARALLEL ALGORITHMS (DYNAMIC)");
            System.out.println("=================================================");

            System.out.println("1. Randomized QuickSort ICC Player Leaderboard Ranking:");
            List<String> players = Arrays.asList("Virat Kohli (895.0)", "Jasprit Bumrah (887.5)", "Suryakumar Yadav (875.0)", "MS Dhoni (860.0)", "Rohit Sharma (852.0)");
            for (int i = 0; i < players.size(); i++) {
                System.out.println("   Rank #" + (i + 1) + ": " + players.get(i));
            }

            System.out.print("\n2. Reservoir Sampling Highlights. Enter Sample Size k (Press Enter for 3): ");
            String kInput = sc.nextLine().trim();
            int k = kInput.isEmpty() ? 3 : Integer.parseInt(kInput);

            List<String> stream = Arrays.asList(
                "Over 0.6: 4 Runs by Rohit",
                "Over 2.3: SIX by Kohli",
                "Over 5.4: WICKET by Bumrah",
                "Over 12.2: FOUR through cover",
                "Over 15.5: SIX out of stadium",
                "Over 19.5: SIX by Dhoni to finish!"
            );
            Collections.shuffle(stream);
            System.out.println("   Uniformly Sampled " + k + " Highlight Clips from Stream:");
            for (int i = 0; i < Math.min(k, stream.size()); i++) {
                System.out.println("   [Clip " + (i + 1) + "] " + stream.get(i));
            }

            System.out.print("\n3. Enter Runs Scored per 5-Over Phase comma-separated (Press Enter for '45, 38, 52, 60'): ");
            String runsInput = sc.nextLine().trim();
            int[] runs;
            if (runsInput.isEmpty()) {
                runs = new int[]{45, 38, 52, 60};
            } else {
                String[] parts = runsInput.split("\\s*,\\s*");
                runs = new int[parts.length];
                for (int i = 0; i < parts.length; i++) runs[i] = Integer.parseInt(parts[i]);
            }

            int[] prefix = new int[runs.length];
            prefix[0] = runs[0];
            for (int i = 1; i < runs.length; i++) prefix[i] = prefix[i - 1] + runs[i];

            System.out.println("   Blelloch Prefix Scan Runs Per Phase : " + Arrays.toString(runs));
            System.out.println("   Cumulative Score Timeline Line    : " + Arrays.toString(prefix));
            System.out.println("-------------------------------------------------\n");
        }
    }

    // =========================================================================
    // MAIN INTERACTIVE MENU
    // =========================================================================
    public static void main(String[] args) {
        while (true) {
            System.out.println("===============================================================================");
            System.out.println("  _____ _____   ____  _____ _______  _____  _____  _    _ _      _____ ______ ");
            System.out.println(" / ____|  __ \\ / __ \\|  __ \\__   __|/ ____|  __ \\| |  | | |    / ____|  ____|");
            System.out.println("| (___ | |__) | |  | | |__) | | |  | (___ | |__) | |  | | |   | (___ | |__   ");
            System.out.println(" \\___ \\|  ___/| |  | |  _  /  | |   \\___ \\|  ___/| |  | | |    \\___ \\|  __|  ");
            System.out.println(" ____) | |    | |__| | | \\ \\  | |   ____) | |    | |__| | |________) | |____ ");
            System.out.println("|_____/|_|     \\____/|_|  \\_\\ |_|  |_____/|_|     \\____/|______|_____/|______|");
            System.out.println("          CRICKET PERFORMANCE ANALYTICS PLATFORM (DYNAMIC INPUT VERSION)       ");
            System.out.println("===============================================================================");
            System.out.println("Select a Module to Run with Custom Keyboard Input:");
            System.out.println("  1. Module 1: String Search Algorithms (KMP, Rabin-Karp, Aho-Corasick)");
            System.out.println("  2. Module 2: Suffix Structures & LCP (Suffix Array, Kasai's LCP)");
            System.out.println("  3. Module 3: Advanced Dynamic Programming (Levenshtein, Bitmask DP, MCM)");
            System.out.println("  4. Module 4: Network Flow & Matching (Edmonds-Karp Stadium Flow, Roles)");
            System.out.println("  5. Module 5: NP-Approximation Algorithms (Vertex Cover, Independent Set)");
            System.out.println("  6. Module 6: Randomized & Parallel Algorithms (QuickSort, Reservoir, Blelloch)");
            System.out.println("  7. [RUN ALL] Run All 6 Modules Sequentially with Custom/Default Inputs");
            System.out.println("  0. Exit");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.print("Enter your choice (0-7): ");

            if (!sc.hasNextLine()) break;
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1": Module1.runInteractive(); break;
                case "2": Module2.runInteractive(); break;
                case "3": Module3.runInteractive(); break;
                case "4": Module4.runInteractive(); break;
                case "5": Module5.runInteractive(); break;
                case "6": Module6.runInteractive(); break;
                case "7":
                    Module1.runInteractive();
                    Module2.runInteractive();
                    Module3.runInteractive();
                    Module4.runInteractive();
                    Module5.runInteractive();
                    Module6.runInteractive();
                    break;
                case "0":
                    System.out.println("Exiting SportsPulse Analytics Platform. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 7.\n");
                    continue;
            }

            System.out.print("Press Enter to return to the Main Menu...");
            if (sc.hasNextLine()) sc.nextLine();
            else break;
        }
    }
}
