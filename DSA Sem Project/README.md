# 🏏 SportsPulse – Cricket Performance Analytics Platform (Java DSA Project)

A clean, modular, and easy-to-understand **Java terminal application** implementing all 6 required DSA modules with **Cricket Datasets** (Virat Kohli, Jasprit Bumrah, MS Dhoni, Rohit Sharma, IPL Squad Auction, Stadium Flow):

---

## 📌 Project Overview & Module Mapping (Cricket Edition)

| Module | Topic | Algorithms Implemented | Cricket Real-World Application |
| :--- | :--- | :--- | :--- |
| **M1** | **String Algorithms** | **KMP**, **Rabin-Karp**, **Aho-Corasick** | Ball-by-ball commentary search & multi-keyword event detection (*SIX*, *FOUR*, *WICKET*, *NO BALL*). |
| **M2** | **Suffix Structures** | **Suffix Array**, **Kasai's LCP Array** | Document similarity & detecting common commentary highlights (*"massive SIX by King Kohli"*). |
| **M3** | **Advanced Dynamic Programming** | **Levenshtein Distance**, **Bitmask DP**, **Matrix Chain Multiplication** | Typo search auto-correction (*"Kholi"* $\rightarrow$ *"Kohli"*), IPL auction squad selection under purse budget, run-rate analytics matrix pipeline. |
| **M4** | **Network Flow & Matching** | **Edmonds-Karp Max Flow**, **Maximum Bipartite Matching** | Wankhede stadium turnstile fan entry throughput & assigning players to match roles (*Powerplay*, *Death Bowler*). |
| **M5** | **NP-Completeness & Approximation** | **Vertex Cover 2-Approximation**, **Maximal Independent Set** | Resolving team chemistry conflicts & selecting a maximal harmony zero-conflict Playing XI roster. |
| **M6** | **Randomized & Parallel Algorithms** | **Randomized QuickSort**, **Reservoir Sampling**, **Blelloch Prefix Scan** | ICC player rating leaderboard, T20 live feed highlights sampling, cumulative 5-over runs scan line. |

---

## 🚀 How to Run

### Option 1: Quick Launcher (Windows)
Double-click `run.bat` or run in terminal:
```cmd
.\run.bat
```

### Option 2: Command Line (javac & java)
```bash
# 1. Compile into bin folder
javac -d bin src/sportspulse/*.java

# 2. Run Interactive Menu
java -cp bin sportspulse.SportsPulseApp

# 3. Run Full Automated Demo directly
java -cp bin sportspulse.SportsPulseApp --all
```
