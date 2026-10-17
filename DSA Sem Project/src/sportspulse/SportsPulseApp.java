package sportspulse;

import java.util.*;

/**
 * SportsPulse - Cricket Performance Analytics Platform
 * Main Terminal Application Entry Point (Kaggle Dataset & User Input Driven)
 */
public class SportsPulseApp {

    public static void printHeader() {
        System.out.println("===============================================================================");
        System.out.println("  _____ _____   ____  _____ _______  _____  _____  _    _ _      _____ ______ ");
        System.out.println(" / ____|  __ \\ / __ \\|  __ \\__   __|/ ____|  __ \\| |  | | |    / ____|  ____|");
        System.out.println("| (___ | |__) | |  | | |__) | | |  | (___ | |__) | |  | | |   | (___ | |__   ");
        System.out.println(" \\___ \\|  ___/| |  | |  _  /  | |   \\___ \\|  ___/| |  | | |    \\___ \\|  __|  ");
        System.out.println(" ____) | |    | |__| | | \\ \\  | |   ____) | |    | |__| | |________) | |____ ");
        System.out.println("|_____/|_|     \\____/|_|  \\_\\ |_|  |_____/|_|     \\____/|______|_____/|______|");
        System.out.println("            CRICKET PERFORMANCE ANALYTICS PLATFORM (DSA Project)              ");
        System.out.println("===============================================================================");
    }

    public static void runAllDemos() {
        printHeader();
        System.out.println("     RUNNING ALL 6 DSA MODULES ON KAGGLE DATASET\n");
        M1_StringAlgorithms.runDemo(null);
        M2_SuffixStructures.runDemo(null);
        M3_DynamicProgramming.runDemo(null);
        M4_NetworkFlow.runDemo(null);
        M5_NPApproximation.runDemo(null);
        M6_RandomizedParallel.runDemo(null);
        System.out.println("===============================================================================");
        System.out.println("  ALL 6 CRICKET MODULES EXECUTED SUCCESSFULLY ON KAGGLE DATASET!");
        System.out.println("===============================================================================");
    }

    public static void main(String[] args) {
        // Pre-load Kaggle dataset
        DatasetLoader.loadDataset();

        if (args.length > 0 && args[0].equalsIgnoreCase("--all")) {
            runAllDemos();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printHeader();
            System.out.println("Kaggle Dataset Status:");
            System.out.println("  - Matches : " + DatasetLoader.getMatchRecords().size() + " records (match.csv)");
            System.out.println("  - Batting : " + DatasetLoader.getBatRecords().size() + " records (bat.csv)");
            System.out.println("  - Bowling : " + DatasetLoader.getBallRecords().size() + " records (ball.csv)");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("Select a Cricket Module to Run with Custom User Input:");
            System.out.println("  1. Module 1: String Search Algorithms (KMP, Rabin-Karp, Aho-Corasick)");
            System.out.println("  2. Module 2: Suffix Structures & Text Similarity (Suffix Array, Kasai's LCP)");
            System.out.println("  3. Module 3: Advanced Dynamic Programming (Levenshtein, Bitmask DP IPL Squad, MCM)");
            System.out.println("  4. Module 4: Network Flow & Bipartite Matching (Edmonds-Karp Stadium Flow, Roles)");
            System.out.println("  5. Module 5: NP-Completeness & Approximation (Vertex Cover, Harmony XI Set)");
            System.out.println("  6. Module 6: Randomized & Parallel Algorithms (QuickSort Ranking, Reservoir, Blelloch)");
            System.out.println("  7. [RUN ALL] Run Complete Suite on Kaggle Dataset");
            System.out.println("  0. Exit");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.print("Enter your choice (0-7): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input) {
                case "1":
                    M1_StringAlgorithms.runDemo(scanner);
                    break;
                case "2":
                    M2_SuffixStructures.runDemo(scanner);
                    break;
                case "3":
                    M3_DynamicProgramming.runDemo(scanner);
                    break;
                case "4":
                    M4_NetworkFlow.runDemo(scanner);
                    break;
                case "5":
                    M5_NPApproximation.runDemo(scanner);
                    break;
                case "6":
                    M6_RandomizedParallel.runDemo(scanner);
                    break;
                case "7":
                    runAllDemos();
                    break;
                case "0":
                    System.out.println("Exiting Cricket Analytics Platform. Good luck with your DSA presentation!");
                    return;
                default:
                    System.out.println("Invalid choice. Please select 0-7.\n");
                    continue;
            }

            System.out.println("Press Enter to return to main menu...");
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            } else {
                break;
            }
        }
    }
}
