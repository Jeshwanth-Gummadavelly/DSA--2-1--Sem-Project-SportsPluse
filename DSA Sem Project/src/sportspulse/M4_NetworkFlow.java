package sportspulse;

import java.util.*;

/**
 * MODULE 4: Network Flow & Matching (M4 - KAGGLE DATASET USER INPUT DRIVEN)
 * Algorithms: Edmonds-Karp Max Flow, Maximum Bipartite Matching
 */
public class M4_NetworkFlow {

    // 1. EDMONDS-KARP MAX FLOW
    public static class FlowNetwork {
        int V;
        int[][] capacity;
        int[][] flow;

        public FlowNetwork(int V) {
            this.V = V;
            capacity = new int[V][V];
            flow = new int[V][V];
        }

        public void addEdge(int u, int v, int cap) {
            capacity[u][v] = cap;
        }

        public int edmondsKarp(int source, int sink) {
            int maxFlow = 0;
            int[] parent = new int[V];

            while (bfs(source, sink, parent)) {
                int pathFlow = Integer.MAX_VALUE;
                for (int v = sink; v != source; v = parent[v]) {
                    int u = parent[v];
                    pathFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
                }
                for (int v = sink; v != source; v = parent[v]) {
                    int u = parent[v];
                    flow[u][v] += pathFlow;
                    flow[v][u] -= pathFlow;
                }
                maxFlow += pathFlow;
            }
            return maxFlow;
        }

        private boolean bfs(int source, int sink, int[] parent) {
            boolean[] visited = new boolean[V];
            Queue<Integer> queue = new LinkedList<>();

            queue.add(source);
            visited[source] = true;
            parent[source] = -1;

            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v = 0; v < V; v++) {
                    if (!visited[v] && capacity[u][v] - flow[u][v] > 0) {
                        queue.add(v);
                        parent[v] = u;
                        visited[v] = true;
                        if (v == sink) return true;
                    }
                }
            }
            return false;
        }
    }

    // 2. MAXIMUM BIPARTITE MATCHING
    public static class BipartiteMatcher {
        private final int numPlayers;
        private final int numRoles;
        private final boolean[][] eligibilityMatrix;
        private final int[] matchRole;

        public BipartiteMatcher(int numPlayers, int numRoles, boolean[][] eligibility) {
            this.numPlayers = numPlayers;
            this.numRoles = numRoles;
            this.eligibilityMatrix = eligibility;
            this.matchRole = new int[numRoles];
            Arrays.fill(matchRole, -1);
        }

        public int maxBipartiteMatch(String[] players, String[] roles) {
            int result = 0;
            for (int p = 0; p < numPlayers; p++) {
                boolean[] visited = new boolean[numRoles];
                if (bpm(p, visited)) {
                    result++;
                }
            }

            System.out.println("   Optimal Player-to-Role Assignments:");
            for (int r = 0; r < numRoles; r++) {
                if (matchRole[r] != -1) {
                    System.out.printf("   - %-28s ---> %s\n", players[matchRole[r]], roles[r]);
                }
            }
            return result;
        }

        private boolean bpm(int p, boolean[] visited) {
            for (int r = 0; r < numRoles; r++) {
                if (eligibilityMatrix[p][r] && !visited[r]) {
                    visited[r] = true;
                    if (matchRole[r] < 0 || bpm(matchRole[r], visited)) {
                        matchRole[r] = p;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static void runDemo(Scanner scanner) {
        System.out.println("=================================================");
        System.out.println("  MODULE 4: NETWORK FLOW & MATCHING (M4 - KAGGLE DATASET)");
        System.out.println("=================================================");

        String teamFilter = "India";
        if (scanner != null) {
            System.out.print("Enter team or player keyword to filter dataset for role matching (default: 'India'): ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) teamFilter = line;
            }
        }

        System.out.println("\n[USER INPUT PARAMETER]");
        System.out.println("   Team/Player Filter : \"" + teamFilter + "\"");

        // 1. Stadium Entry Gates Max Flow
        System.out.println("\n1. Edmonds-Karp Stadium Spectator Entry Max Flow:");
        FlowNetwork net = new FlowNetwork(5);
        net.addEdge(0, 1, 150);
        net.addEdge(0, 2, 120);
        net.addEdge(1, 3, 100);
        net.addEdge(2, 3, 80);
        net.addEdge(1, 4, 40);
        net.addEdge(3, 4, 140);
        int maxFlow = net.edmondsKarp(0, 4);
        System.out.println("   Max Fan Entry Throughput Rate: " + maxFlow + " fans/minute");

        // 2. Maximum Bipartite Matching on Kaggle dataset
        System.out.println("\n2. Maximum Bipartite Matching (Kaggle Dataset Players for '" + teamFilter + "'):");
        List<DatasetLoader.BatRecord> batRecords = DatasetLoader.getBatRecords();
        Map<String, String> playerRoleMap = new LinkedHashMap<>();

        for (DatasetLoader.BatRecord br : batRecords) {
            if (br.matchName.toLowerCase().contains(teamFilter.toLowerCase()) || br.playerName.toLowerCase().contains(teamFilter.toLowerCase())) {
                if (playerRoleMap.size() < 6) {
                    String role = br.playingRole.isEmpty() ? "BAT" : br.playingRole;
                    playerRoleMap.put(br.playerName, role);
                }
            }
        }

        if (playerRoleMap.isEmpty()) {
            for (DatasetLoader.BatRecord br : batRecords) {
                if (playerRoleMap.size() < 6 && !br.playerName.isEmpty()) {
                    String role = br.playingRole.isEmpty() ? "BAT" : br.playingRole;
                    playerRoleMap.put(br.playerName, role);
                }
            }
        }

        String[] playerNames = new String[playerRoleMap.size()];
        String[] playerRoles = new String[playerRoleMap.size()];
        int idx = 0;
        for (Map.Entry<String, String> entry : playerRoleMap.entrySet()) {
            playerNames[idx] = entry.getKey() + " (" + entry.getValue() + ")";
            playerRoles[idx] = entry.getValue();
            idx++;
        }

        String[] tacticalRoles = {
            "Powerplay Opener",
            "Middle Order Anchor",
            "Death Overs Specialist",
            "Spin All-Rounder",
            "Wicketkeeper Finisher",
            "Pace Strike Bowler"
        };

        int numP = playerNames.length;
        int numR = tacticalRoles.length;
        boolean[][] eligibility = new boolean[numP][numR];

        for (int p = 0; p < numP; p++) {
            String role = playerRoles[p];
            if (role.contains("BAT")) {
                eligibility[p][0] = true;
                eligibility[p][1] = true;
            } else if (role.contains("BOWL")) {
                eligibility[p][2] = true;
                eligibility[p][5] = true;
            } else if (role.contains("AR")) {
                eligibility[p][1] = true;
                eligibility[p][3] = true;
            } else if (role.contains("WK")) {
                eligibility[p][1] = true;
                eligibility[p][4] = true;
            } else {
                eligibility[p][0] = true;
                eligibility[p][1] = true;
            }
        }

        BipartiteMatcher matcher = new BipartiteMatcher(numP, numR, eligibility);
        int totalMatched = matcher.maxBipartiteMatch(playerNames, tacticalRoles);
        System.out.println("   Total Successful Role Assignments: " + totalMatched + " / " + numP);
        System.out.println("-------------------------------------------------\n");
    }
}
