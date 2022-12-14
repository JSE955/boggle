import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

public class GameEngine implements WordSearchGame {
   private TreeSet<String> lexicon;
   
   private String[][] board;
   private boolean[][] visited;
   
   private int numRows;
   private int numCols;
   private int order;
   private final int MAX_NEIGHBORS = 8;

   public GameEngine() {
      this(new String[] {"E", "E", "C", "A", "A", "L", "E", "P", 
                           "H", "N", "B", "O", "Q", "T", "T", "Y"});
   }
   
   public GameEngine(String[] boardIn) {
      numRows = (int) Math.floor(Math.sqrt(boardIn.length));
      numCols = (int) Math.floor(Math.sqrt(boardIn.length));
      board = new String[numRows][numCols];
      int i = 0;
      for (int row = 0; row < board.length; row++) {
         for (int col = 0; col < board[row].length; col++) {
            board[row][col] = boardIn[i];
            i++;
         }
      }
      markAllUnvisited();
   }
   
   public void markAllUnvisited() {
      visited = new boolean[numRows][numCols];
      for (boolean[] row : visited) {
         Arrays.fill(row, false);
      }
   }
   
   public void loadLexicon(String fileName) {
      if (fileName == null) {
         throw new IllegalArgumentException();
      }
      lexicon = new TreeSet<String>();
      try {
         Scanner s = 
            new Scanner(new BufferedReader(new FileReader(new File(fileName))));
         while (s.hasNext()) {
            String str = s.next();
            boolean added = lexicon.add(str.toUpperCase());
            s.nextLine();
         }
      }
      catch (FileNotFoundException e) {
         throw new IllegalArgumentException();
      }
      catch (IllegalArgumentException e) {
         System.out.println("File cannot be opened.");
      }
      
      
   }
   
   public void setBoard(String[] letterArray) {
      if (letterArray == null) {
         throw new IllegalArgumentException();
      }
      numRows = (int) Math.floor(Math.sqrt(letterArray.length));
      numCols = (int) Math.floor(Math.sqrt(letterArray.length));
      if (numRows * numCols != letterArray.length) {
         throw new IllegalArgumentException();
      }
      board = new String[numRows][numCols];
      int i = 0;
      for (int row = 0; row < board.length; row++) {
         for (int col = 0; col < board[row].length; col++) {
            board[row][col] = letterArray[i];
            i++;
         }
      }
      markAllUnvisited();
   }
   
   public String getBoard() {
      int currentColumn = 0;
      String result = "";
      for (String[] row : board) {
         for (String letter : row) {
            if (currentColumn == numCols) {
               result += "\n";
               currentColumn = 0;
            }
            result += letter + "\t";
            currentColumn++;
         }
      }
      return result;
   }
   
   public SortedSet<String> getAllScorableWords(int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
      if (lexicon == null) {
         throw new IllegalStateException();
      }
      SortedSet<String> foundWords = new TreeSet<String>();
      for (String word : lexicon) {
         if (word.length() >= minimumWordLength &&
               isValidWord(word) && isOnBoard(word).size() > 0) {
            foundWords.add(word);
            markAllUnvisited();
         }
      }
      return foundWords;
   }
   
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
      if (lexicon.size() == 0) {
         throw new IllegalStateException();
      }
      int score = 0;
      for (String word : words) {
         score += 1 + (word.length() - minimumWordLength);
      }
      return score;
   }
   
   public boolean isValidWord(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      if (lexicon.size() == 0) {
         throw new IllegalStateException();
      }
      String word = wordToCheck.toUpperCase();
      return lexicon.contains(word);
   }
   
   public boolean isValidPrefix(String prefixToCheck) {
      if (prefixToCheck == null) {
         throw new IllegalArgumentException();
      }
      if (lexicon.size() == 0) {
         throw new IllegalStateException();
      }
      String prefix = prefixToCheck.toUpperCase();
      String test = lexicon.ceiling(prefix);
      if (test == null) {
         return false;
      }
      if (test.startsWith(prefix)) {
         return true;
      }
      return false;
   }
   
   public List<Integer> isOnBoard(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      if (lexicon.size() == 0) {
         throw new IllegalStateException();
      }
      String word = wordToCheck.toUpperCase();
      for (int row = 0; row < board.length; row++) {
         for (int col = 0; col < board[row].length; col++) {
            if (word.startsWith(board[row][col])) {
               Position position = new Position(row, col);
               String wordSoFar = "";
               List<Integer> path = new ArrayList<Integer>();
               if (dfs(position, wordSoFar, word, path)) {
                  return path;
               }
            }
         }
      }
      markAllUnvisited();
      return new ArrayList<Integer>();
   }
   
   private boolean dfs(Position position, 
                        String wordSoFar,
                        String wordToCheck,
                        List<Integer> path) {
      if (!isValid(position)) {
         return false;
      }
      if (isVisited(position)) { 
         return false;
      }
      if (!wordToCheck.startsWith(wordSoFar)) {
         return false;
      }
      visit(position);
      wordSoFar += board[position.x][position.y];
      path.add((position.x * numRows) + position.y);
      if (wordSoFar.equals(wordToCheck)) {
         return true;
      }
      for (Position neighbor : position.neighbors()) {
         if (dfs(neighbor, wordSoFar, wordToCheck, path)) {
            return true;
         }
      }
      notVisited(position);
      wordSoFar = wordSoFar.substring(0, wordSoFar.length() - 1);
      path.remove(path.size() - 1);
      return false;
   }
   
   // Position class with methods
   
   private class Position {
      int x;
      int y;
      
      public Position(int x, int y) {
         this.x = x;
         this.y = y;
      }
      
      @Override
      public String toString() {
         return "(" + x + ", " + y + ")";
      }
      
      public Position[] neighbors() {
         Position[] nbrs = new Position[MAX_NEIGHBORS];
         int count = 0;
         Position p;
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (!((i == 0) && (j == 0))) {
                  p = new Position(i + x, y + j);
                  if (isValid(p)) {
                     nbrs[count++] = p;
                  }
               }
            }
         }
         return Arrays.copyOf(nbrs, count);
      }
   }
   
   private boolean isValid(Position p) {
      return (p.x >= 0) && (p.x < numRows) &&
               (p.y >= 0) && (p.y < numCols) &&
               visited[p.x][p.y] != true;
   }
      
   private boolean isVisited(Position p) {
      return visited[p.x][p.y];
   }
      
   private void visit(Position p) {
      visited[p.x][p.y] = true;
   }
      
   private void notVisited(Position p) {
      visited[p.x][p.y] = false;
   }
}