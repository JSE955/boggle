import java.util.SortedSet;


public class GameEngineClient {
   public static void main(String[] args) {
      WordSearchGame game = WordSearchGameFactory.createGame();
      game.loadLexicon("words_medium.txt");
      String[] myArray = {"H","E","B","E","Z","K","T","S","T",};
      game.setBoard(myArray);
      System.out.println(game.getBoard());
      String word = "ZEKS";
      System.out.println(word.length() >= 3);
      System.out.println(game.isOnBoard(word).size() > 0);
      System.out.println(game.isValidWord(word));
      System.out.println(game.getAllScorableWords(3));
      //System.out.println(game.getScoreForWords(testSet, 5));
      
      
   }
}