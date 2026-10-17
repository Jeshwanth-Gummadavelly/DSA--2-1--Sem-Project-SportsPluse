package sportspulse;

import java.util.*;

/**
 * MODULE 5: NP-Completeness & Approximation (M5 - KAGGLE DATASET USER INPUT DRIVEN)
 * Algorithms: Vertex Cover 2-Approximation, Maximal Independent Set (Harmony XI Roster)
 */
public class M5_NPApproximation {

    public static class Graph {
        int V;
        List<int[]> edges;

        public Graph(int V) {
            this.V = V;
            this.edges = new ArrayList<>();
        }

        public void addEdge(int u, int v) {
            edges.add(new int[]{u, v});
        }
    }

    public static Set<Integer> vertexCover2Approx(Graph graph, String[] playerNames) {
        Set<Integer> cover = new TreeSet<>();
        boolean[] visitedEdges = new boolean[graph.edges.size()];

        for (int i = 0; i < graph.edges.size(); i++) {
            if (!visitedEdges[i]) {
                int u = graph.edges.get(i)[0];
                int v = graph.edges.get(i)[1];

                cover.add(u);
                cover.add(v);

                for (int j = 0; j < graph.edges.size(); j++) {
                    int edgeU = graph.edges.get(j)[0];
                    int edgeV = graph.edges.get(j)[1];
                    if (edgeU == u || edgeU == v || edgeV == u || edgeV == v) {
                        visitedEdges[j] = true;
                    }
                }
            }
        }

        System.out.println("   Players Selected as Mediators for Conflict Resolution (2-Approx):");
        for (int playerIdx : cover) {
            System.out.println("   - Mediator Player: " + playerNames[playerIdx]);
        }
        return cover;
    }

    public static List<String> findCompatibleGroup(Graph conflictGraph, String[] playerNames) {
        int n = conflictGraph.V;
        boolean[][] conflictMatrix = new boolean[n][n];

        for (int[] edge : conflictGraph.edges) {
            conflictMatrix[edge[0]][edge[1]] = true;
            conflictMatrix[edge[1]][edge[0]] = true;
        }

        List<Integer> compatibleIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean canAdd = true;
            for (int member : compatibleIndices) {
                if (conflictMatrix[i][member]) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                compatibleIndices.add(i);
            }
        }

        List<String> compatibleGroup = new ArrayList<>();
        System.out.println("   Maximal Harmony Cricket XI Roster (Zero Conflicts):");
        for (int idx : compatibleIndices) {
            compatibleGroup.add(playerNames[idx]);
            System.out.println("   - Compatible Player: " + playerNames[idx]);
        }
        return compatibleGroup;
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 5: NP-APPROXIMATION ALGORITHMS (M5 - KAGGLE DATASET)");
        System.out.println("=================================================");

        String playersInput = "";
        if (scanner != null) {
            System.out.print("Enter comma-separated player names to evaluate conflicts (press Enter to use Kaggle dataset players): ");
            if (scanner.hasNextLine()) {
                playersInput = scanner.nextLine().trim();
            }
        }

        List<String> selectedList = new ArrayList<>();
        if (!playersInput.isEmpty()) {
            String[] parts = playersInput.split(",");
            for (String p : parts) {
                if (!p.trim().isEmpty()) selectedList.add(p.trim());
            }
        }

        if (selectedList.isEmpty()) {
            List<String> datasetPlayers = DatasetLoader.getAllUniquePlayers();
            for (String p : datasetPlayers) {
                if (selectedList.size() < 6 && !p.isEmpty()) {
                    selectedList.add(p);
                }
            }
        }

        if (selectedList.size() < 6) {
            selectedList = Arrays.asList("Kohli", "Rohit", "Bumrah", "Hardik", "Rahul", "Jadeja");
        }

        String[] players = selectedList.toArray(new String[0]);
        int n = players.length;
        Graph conflictGraph = new Graph(n);

        conflictGraph.addEdge(0, 1);
        conflictGraph.addEdge(1, 3);
        if (n > 4) conflictGraph.addEdge(3, 4);
        conflictGraph.addEdge(0, 3);

        System.out.println("\n[USER EVALUATED ROSTER POOL]: " + Arrays.toString(players));
        System.out.println("   Registered Conflicts: (" + players[0] + "-" + players[1] + "), (" 
                           + players[1] + "-" + players[3] + "), (" 
                           + players[0] + "-" + players[3] + ")\n");

        System.out.println("1. Vertex-Cover 2-Approximation for Conflict Resolution:");
        vertexCover2Approx(conflictGraph, players);

        System.out.println("\n2. Maximal Compatible Roster Search (Independent Set):");
        findCompatibleGroup(conflictGraph, players);
        System.out.println("-------------------------------------------------\n");
    }
}
