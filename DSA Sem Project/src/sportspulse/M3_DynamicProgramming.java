package sportspulse;

import java.util.*;

/**
 * MODULE 3: Advanced Dynamic Programming (M3 - KAGGLE DATASET USER INPUT DRIVEN)
 * Algorithms: Levenshtein Distance Typo Auto-Correction, Bitmask DP Roster Optimization, Matrix Chain Multiplication
 */
public class M3_DynamicProgramming {

    // 1. LEVENSHTEIN EDIT DISTANCE
    public static int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    public static String getBestPlayerMatch(String query, List<String> playerDatabase) {
        if (playerDatabase == null || playerDatabase.isEmpty()) return "No players in DB";

        String bestMatch = playerDatabase.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (String player : playerDatabase) {
            int distFull = levenshteinDistance(query.toLowerCase(), player.toLowerCase());
            if (distFull < minDistance) {
                minDistance = distFull;
                bestMatch = player;
            }
            String[] tokens = player.split("\\s+");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    int distToken = levenshteinDistance(query.toLowerCase(), token.toLowerCase());
                    if (distToken < minDistance) {
                        minDistance = distToken;
                        bestMatch = player;
                    }
                }
            }
        }
        return bestMatch + " (Edit Distance = " + minDistance + ")";
    }

    // 2. BITMASK DP / IPL SQUAD OPTIMIZATION
    public static class CricketPlayer {
        String name;
        int costLakhs;
        int rating;

        public CricketPlayer(String name, int costLakhs, int rating) {
            this.name = name;
            this.costLakhs = costLakhs;
            this.rating = rating;
        }
    }

    public static void optimizeSquadBitmask(List<CricketPlayer> players, int budgetLakhs) {
        int n = players.size();
        int totalCombinations = 1 << n;

        int maxRating = 0;
        int bestMask = 0;
        int bestCost = 0;

        for (int mask = 0; mask < totalCombinations; mask++) {
            int currentCost = 0;
            int currentRating = 0;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    currentCost += players.get(i).costLakhs;
                    currentRating += players.get(i).rating;
                }
            }

            if (currentCost <= budgetLakhs && currentRating > maxRating) {
                maxRating = currentRating;
                bestMask = mask;
                bestCost = currentCost;
            }
        }

        System.out.println("   Optimal IPL Roster Selected under Purse Budget Rs." + budgetLakhs + " Lakhs:");
        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                CricketPlayer p = players.get(i);
                System.out.printf("   - %-22s | Price: Rs.%2dLakhs | Kaggle Rating: %d\n", p.name, p.costLakhs, p.rating);
            }
        }
        System.out.println("   Total Purse Spent: Rs." + bestCost + " Lakhs / Purse Budget: Rs." + budgetLakhs + " Lakhs");
        System.out.println("   Total Roster Performance Rating: " + maxRating);
    }

    // 3. MATRIX CHAIN MULTIPLICATION (MCM)
    public static int matrixChainOrder(int[] dims) {
        int n = dims.length;
        int[][] m = new int[n][n];

        for (int len = 2; len < n; len++) {
            for (int i = 1; i < n - len + 1; i++) {
                int j = i + len - 1;
                m[i][j] = Integer.MAX_VALUE;

                for (int k = i; k <= j - 1; k++) {
                    int cost = m[i][k] + m[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                    }
                }
            }
        }
        return m[1][n - 1];
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 3: DYNAMIC PROGRAMMING (M3 - KAGGLE DATASET)");
        System.out.println("=================================================");

        String playerQuery = "Tendulker";
        int purseBudget = 90;
        int[] matrixDims = {10, 30, 5, 60};

        if (scanner != null) {
            System.out.print("Enter player name query to search (with typo, e.g. 'Tendulker', 'Kholi', 'Dhoni'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) playerQuery = line;
            }

            System.out.print("Enter purse budget in Lakhs (default: 90): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try { purseBudget = Integer.parseInt(line); } catch (Exception ignored) {}
                }
            }

            System.out.print("Enter comma-separated matrix pipeline dimensions (default: '10, 30, 5, 60'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        String[] parts = line.split(",");
                        int[] dims = new int[parts.length];
                        for (int i = 0; i < parts.length; i++) dims[i] = Integer.parseInt(parts[i].trim());
                        if (dims.length >= 3) matrixDims = dims;
                    } catch (Exception ignored) {}
                }
            }
        }

        // 1. Levenshtein Search in Kaggle Database
        List<String> playerDb = DatasetLoader.getAllUniquePlayers();
        System.out.println("\n1. Levenshtein Typo Auto-Correction against Kaggle Dataset (" + playerDb.size() + " players):");
        System.out.println("   Input Player Query : \"" + playerQuery + "\"");
        String bestMatch = getBestPlayerMatch(playerQuery, playerDb);
        System.out.println("   Suggested Match    : " + bestMatch);

        // Fetch matched player stats from bat.csv
        String cleanMatchName = bestMatch.replaceAll(" \\(Edit Distance.*", "").trim();
        List<DatasetLoader.BatRecord> batRecords = DatasetLoader.getBatRecords();
        int totalRuns = 0, totalMatches = 0;
        for (DatasetLoader.BatRecord br : batRecords) {
            if (br.playerName.equalsIgnoreCase(cleanMatchName)) {
                totalRuns += br.runs;
                totalMatches++;
            }
        }
        if (totalMatches > 0) {
            System.out.println("   [Kaggle Dataset Stats for " + cleanMatchName + "]: Total Runs = " + totalRuns + ", Matches Recorded = " + totalMatches);
        }

        // 2. Bitmask DP Roster Optimization using real Kaggle dataset
        System.out.println("\n2. Bitmask DP IPL Squad Optimization (Purse Budget Rs." + purseBudget + " Lakhs):");
        Map<String, Integer> playerRunsMap = new HashMap<>();
        for (DatasetLoader.BatRecord br : batRecords) {
            playerRunsMap.put(br.playerName, playerRunsMap.getOrDefault(br.playerName, 0) + br.runs);
        }

        List<CricketPlayer> squadPool = new ArrayList<>();
        int costIndex = 15;
        for (Map.Entry<String, Integer> entry : playerRunsMap.entrySet()) {
            if (squadPool.size() >= 8) break;
            int runs = entry.getValue();
            int rating = Math.min(99, 70 + (runs / 10));
            int cost = 15 + (costIndex % 30);
            costIndex += 7;
            squadPool.add(new CricketPlayer(entry.getKey(), cost, rating));
        }

        if (squadPool.isEmpty()) {
            squadPool = Arrays.asList(
                new CricketPlayer("Virat Kohli", 45, 95),
                new CricketPlayer("Jasprit Bumrah", 40, 94),
                new CricketPlayer("MS Dhoni", 35, 90),
                new CricketPlayer("Rohit Sharma", 40, 92)
            );
        }
        optimizeSquadBitmask(squadPool, purseBudget);

        // 3. Matrix Chain Multiplication
        System.out.println("\n3. Matrix Chain Multiplication (Run Rate Analytics Pipeline):");
        int minOps = matrixChainOrder(matrixDims);
        System.out.print("   Pipeline Dimensions : ");
        for (int i = 0; i < matrixDims.length - 1; i++) {
            System.out.print(matrixDims[i] + "x" + matrixDims[i + 1] + (i < matrixDims.length - 2 ? ", " : "\n"));
        }
        System.out.println("   Minimum Scalar Multiplications Required: " + minOps);
        System.out.println("-------------------------------------------------\n");
    }
}
