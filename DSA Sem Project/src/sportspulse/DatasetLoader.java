package sportspulse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * DatasetLoader: CSV Loader for Kaggle Cricket Dataset
 * Reads DataSet/match.csv, DataSet/bat.csv, and DataSet/ball.csv
 */
public class DatasetLoader {

    public static class MatchRecord {
        public String matchName;
        public String startDate;
        public String matchType;
        public String title;
        public int runs;
        public double overs;
        public double runRate;

        public MatchRecord(String matchName, String startDate, String matchType, String title, int runs, double overs, double runRate) {
            this.matchName = matchName;
            this.startDate = startDate;
            this.matchType = matchType;
            this.title = title;
            this.runs = runs;
            this.overs = overs;
            this.runRate = runRate;
        }
    }

    public static class BatRecord {
        public String playerName;
        public String playingRole;
        public int runs;
        public int balls;
        public double strikeRate;
        public int fours;
        public int sixes;
        public String howOut;
        public String matchName;

        public BatRecord(String playerName, String playingRole, int runs, int balls, double strikeRate, int fours, int sixes, String howOut, String matchName) {
            this.playerName = playerName;
            this.playingRole = playingRole;
            this.runs = runs;
            this.balls = balls;
            this.strikeRate = strikeRate;
            this.fours = fours;
            this.sixes = sixes;
            this.howOut = howOut;
            this.matchName = matchName;
        }
    }

    public static class BallRecord {
        public String bowlerName;
        public String playingRole;
        public int runsConceded;
        public int wickets;
        public double overs;
        public double economy;
        public String matchName;

        public BallRecord(String bowlerName, String playingRole, int runsConceded, int wickets, double overs, double economy, String matchName) {
            this.bowlerName = bowlerName;
            this.playingRole = playingRole;
            this.runsConceded = runsConceded;
            this.wickets = wickets;
            this.overs = overs;
            this.economy = economy;
            this.matchName = matchName;
        }
    }

    private static List<MatchRecord> matchRecords = new ArrayList<>();
    private static List<BatRecord> batRecords = new ArrayList<>();
    private static List<BallRecord> ballRecords = new ArrayList<>();
    private static boolean isLoaded = false;

    public static synchronized void loadDataset() {
        if (isLoaded) return;

        loadMatches("DataSet/match.csv");
        loadBatting("DataSet/bat.csv");
        loadBowling("DataSet/ball.csv");

        isLoaded = true;
    }

    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        if (line == null) return tokens;

        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    private static void loadMatches(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("[DatasetLoader] Warning: " + filePath + " not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) return;

            // Map header columns
            List<String> cols = parseCsvLine(header);
            int nameIdx = cols.indexOf("name");
            int dateIdx = cols.indexOf("start_date");
            int typeIdx = cols.indexOf("matchtype");
            int titleIdx = cols.indexOf("title");
            int runsIdx = cols.indexOf("runs");
            int overIdx = cols.indexOf("over");
            int rrIdx = cols.indexOf("run_rate");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> row = parseCsvLine(line);
                if (row.size() <= Math.max(nameIdx, Math.max(titleIdx, runsIdx))) continue;

                String name = nameIdx >= 0 && nameIdx < row.size() ? row.get(nameIdx) : "";
                String date = dateIdx >= 0 && dateIdx < row.size() ? row.get(dateIdx) : "";
                String type = typeIdx >= 0 && typeIdx < row.size() ? row.get(typeIdx) : "";
                String title = titleIdx >= 0 && titleIdx < row.size() ? row.get(titleIdx) : "";
                int runs = parseSafeInt(row, runsIdx);
                double overs = parseSafeDouble(row, overIdx);
                double rr = parseSafeDouble(row, rrIdx);

                if (!name.isEmpty()) {
                    matchRecords.add(new MatchRecord(name, date, type, title, runs, overs, rr));
                }
            }
            System.out.println("[DatasetLoader] Successfully loaded " + matchRecords.size() + " records from match.csv");
        } catch (Exception e) {
            System.out.println("[DatasetLoader] Error reading match.csv: " + e.getMessage());
        }
    }

    private static void loadBatting(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("[DatasetLoader] Warning: " + filePath + " not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) return;

            List<String> cols = parseCsvLine(header);
            int nameIdx = cols.indexOf("name_x");
            int roleIdx = cols.indexOf("playing_role");
            int runsIdx = cols.indexOf("runs_x");
            int ballsIdx = cols.indexOf("balls");
            int srIdx = cols.indexOf("strike_rate");
            int foursIdx = cols.indexOf("fours");
            int sixesIdx = cols.indexOf("sixes");
            int howOutIdx = cols.indexOf("how_out");
            int matchIdx = cols.indexOf("name_y");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> row = parseCsvLine(line);
                if (row.size() <= nameIdx) continue;

                String pName = nameIdx >= 0 && nameIdx < row.size() ? row.get(nameIdx) : "";
                String role = roleIdx >= 0 && roleIdx < row.size() ? row.get(roleIdx) : "";
                int runs = parseSafeInt(row, runsIdx);
                int balls = parseSafeInt(row, ballsIdx);
                double sr = parseSafeDouble(row, srIdx);
                int fours = parseSafeInt(row, foursIdx);
                int sixes = parseSafeInt(row, sixesIdx);
                String howOut = howOutIdx >= 0 && howOutIdx < row.size() ? row.get(howOutIdx) : "";
                String matchName = matchIdx >= 0 && matchIdx < row.size() ? row.get(matchIdx) : "";

                if (!pName.isEmpty()) {
                    batRecords.add(new BatRecord(pName, role, runs, balls, sr, fours, sixes, howOut, matchName));
                }
            }
            System.out.println("[DatasetLoader] Successfully loaded " + batRecords.size() + " records from bat.csv");
        } catch (Exception e) {
            System.out.println("[DatasetLoader] Error reading bat.csv: " + e.getMessage());
        }
    }

    private static void loadBowling(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("[DatasetLoader] Warning: " + filePath + " not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) return;

            List<String> cols = parseCsvLine(header);
            int nameIdx = cols.indexOf("name_x");
            int roleIdx = cols.indexOf("playing_role");
            int rcIdx = cols.indexOf("run_conceded");
            int wktsIdx = cols.indexOf("wickets");
            int oversIdx = cols.indexOf("overs");
            int econIdx = cols.indexOf("economy");
            int matchIdx = cols.indexOf("name_y");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> row = parseCsvLine(line);
                if (row.size() <= nameIdx) continue;

                String bName = nameIdx >= 0 && nameIdx < row.size() ? row.get(nameIdx) : "";
                String role = roleIdx >= 0 && roleIdx < row.size() ? row.get(roleIdx) : "";
                int rc = parseSafeInt(row, rcIdx);
                int wkts = parseSafeInt(row, wktsIdx);
                double overs = parseSafeDouble(row, oversIdx);
                double econ = parseSafeDouble(row, econIdx);
                String matchName = matchIdx >= 0 && matchIdx < row.size() ? row.get(matchIdx) : "";

                if (!bName.isEmpty()) {
                    ballRecords.add(new BallRecord(bName, role, rc, wkts, overs, econ, matchName));
                }
            }
            System.out.println("[DatasetLoader] Successfully loaded " + ballRecords.size() + " records from ball.csv");
        } catch (Exception e) {
            System.out.println("[DatasetLoader] Error reading ball.csv: " + e.getMessage());
        }
    }

    private static int parseSafeInt(List<String> row, int idx) {
        if (idx < 0 || idx >= row.size()) return 0;
        try {
            String val = row.get(idx).trim();
            if (val.isEmpty()) return 0;
            return (int) Double.parseDouble(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private static double parseSafeDouble(List<String> row, int idx) {
        if (idx < 0 || idx >= row.size()) return 0.0;
        try {
            String val = row.get(idx).trim();
            if (val.isEmpty()) return 0.0;
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static List<MatchRecord> getMatchRecords() {
        loadDataset();
        return matchRecords;
    }

    public static List<BatRecord> getBatRecords() {
        loadDataset();
        return batRecords;
    }

    public static List<BallRecord> getBallRecords() {
        loadDataset();
        return ballRecords;
    }

    /**
     * Helper: Extract unique player names across batting and bowling CSVs
     */
    public static List<String> getAllUniquePlayers() {
        loadDataset();
        Set<String> set = new LinkedHashSet<>();
        for (BatRecord b : batRecords) {
            if (b.playerName != null && !b.playerName.isEmpty()) set.add(b.playerName);
        }
        for (BallRecord bl : ballRecords) {
            if (bl.bowlerName != null && !bl.bowlerName.isEmpty()) set.add(bl.bowlerName);
        }
        return new ArrayList<>(set);
    }
}
