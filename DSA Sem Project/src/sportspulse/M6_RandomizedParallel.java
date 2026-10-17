package sportspulse;

import java.util.*;

/**
 * MODULE 6: Randomized & Parallel Algorithms (M6 - KAGGLE DATASET USER INPUT DRIVEN)
 * Algorithms: Randomized QuickSort, Reservoir Sampling, Blelloch Parallel Prefix Scan
 */
public class M6_RandomizedParallel {

    public static class CricketPlayerScore {
        String name;
        double rating;

        public CricketPlayerScore(String name, double rating) {
            this.name = name;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return String.format("%-24s : %.1f ICC Rating", name, rating);
        }
    }

    // 1. RANDOMIZED QUICKSORT
    private static final Random rand = new Random();

    public static void randomizedQuickSort(List<CricketPlayerScore> list, int low, int high) {
        if (low < high) {
            int pivotIdx = randomizedPartition(list, low, high);
            randomizedQuickSort(list, low, pivotIdx - 1);
            randomizedQuickSort(list, pivotIdx + 1, high);
        }
    }

    private static int randomizedPartition(List<CricketPlayerScore> list, int low, int high) {
        int randomPivot = low + rand.nextInt(high - low + 1);
        Collections.swap(list, randomPivot, high);
        return partition(list, low, high);
    }

    private static int partition(List<CricketPlayerScore> list, int low, int high) {
        double pivot = list.get(high).rating;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).rating >= pivot) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // 2. RESERVOIR SAMPLING
    public static List<String> reservoirSample(List<String> eventStream, int k) {
        List<String> reservoir = new ArrayList<>();
        for (int i = 0; i < Math.min(k, eventStream.size()); i++) {
            reservoir.add(eventStream.get(i));
        }
        for (int i = k; i < eventStream.size(); i++) {
            int j = rand.nextInt(i + 1);
            if (j < k) {
                reservoir.set(j, eventStream.get(i));
            }
        }
        return reservoir;
    }

    // 3. BLELLOCH PARALLEL PREFIX SCAN PATTERN
    public static int[] prefixScanCumulativeRuns(int[] runsPerOver) {
        int n = runsPerOver.length;
        int[] prefixSum = new int[n];
        if (n == 0) return prefixSum;
        prefixSum[0] = runsPerOver[0];
        for (int i = 1; i < n; i++) {
            prefixSum[i] = prefixSum[i - 1] + runsPerOver[i];
        }
        return prefixSum;
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 6: RANDOMIZED & PARALLEL ALGORITHMS (M6 - KAGGLE DATASET)");
        System.out.println("=================================================");

        int topN = 8;
        int kSample = 3;
        int[] runsPerPhase = {45, 38, 52, 60};

        if (scanner != null) {
            System.out.print("Enter top N players to rank in Kaggle Leaderboard (default: 8): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try { topN = Integer.parseInt(line); } catch (Exception ignored) {}
                }
            }

            System.out.print("Enter number of highlight clips to sample from Kaggle stream (default: 3): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try { kSample = Integer.parseInt(line); } catch (Exception ignored) {}
                }
            }

            System.out.print("Enter comma-separated runs per 5-over phase (default: '45, 38, 52, 60'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        String[] parts = line.split(",");
                        int[] parsed = new int[parts.length];
                        for (int i = 0; i < parts.length; i++) parsed[i] = Integer.parseInt(parts[i].trim());
                        if (parsed.length > 0) runsPerPhase = parsed;
                    } catch (Exception ignored) {}
                }
            }
        }

        System.out.println("\n[USER INPUT PARAMETERS]");
        System.out.println("   Leaderboard Display Count (N) : " + topN);
        System.out.println("   Reservoir Replay Samples (k)  : " + kSample);
        System.out.println("   Over Phase Runs Array         : " + Arrays.toString(runsPerPhase));

        // 1. Randomized QuickSort Leaderboard Ranking
        List<DatasetLoader.BatRecord> batRecords = DatasetLoader.getBatRecords();
        Map<String, Double> playerRatingMap = new HashMap<>();
        for (DatasetLoader.BatRecord br : batRecords) {
            if (!br.playerName.isEmpty()) {
                double currentRating = playerRatingMap.getOrDefault(br.playerName, 500.0);
                double bonus = (br.runs * 1.5) + (br.sixes * 10) + (br.fours * 4);
                playerRatingMap.put(br.playerName, Math.min(950.0, currentRating + bonus / 10.0));
            }
        }

        List<CricketPlayerScore> leaderboard = new ArrayList<>();
        for (Map.Entry<String, Double> entry : playerRatingMap.entrySet()) {
            leaderboard.add(new CricketPlayerScore(entry.getKey(), entry.getValue()));
        }

        System.out.println("\n1. Randomized QuickSort Cricket ICC Leaderboard (" + leaderboard.size() + " Kaggle Players):");
        randomizedQuickSort(leaderboard, 0, leaderboard.size() - 1);
        for (int i = 0; i < Math.min(topN, leaderboard.size()); i++) {
            System.out.printf("   Rank #%2d: %s\n", (i + 1), leaderboard.get(i));
        }

        // 2. Reservoir Sampling
        List<String> eventStream = new ArrayList<>();
        for (DatasetLoader.BatRecord br : batRecords) {
            if (!br.howOut.isEmpty() && !br.howOut.equalsIgnoreCase("not out")) {
                eventStream.add("Match: " + br.matchName + " | " + br.playerName + " (" + br.runs + " runs) -> " + br.howOut);
            }
        }

        System.out.println("\n2. Reservoir Sampling Kaggle Stream (" + eventStream.size() + " Total Dismissal Events):");
        List<String> sampledEvents = reservoirSample(eventStream, kSample);
        System.out.println("   Uniformly Sampled " + kSample + " Highlight Replays:");
        for (String ev : sampledEvents) {
            System.out.println("   [Replay Clip] " + ev);
        }

        // 3. Blelloch Parallel Prefix Scan
        System.out.println("\n3. Blelloch Prefix Scan Cumulative Total Score Calculation:");
        int[] cumulativeRuns = prefixScanCumulativeRuns(runsPerPhase);
        System.out.println("   Input Phase Runs           : " + Arrays.toString(runsPerPhase));
        System.out.println("   Cumulative Score Scan Line : " + Arrays.toString(cumulativeRuns));
        System.out.println("-------------------------------------------------\n");
    }
}
