import javax.swing.SwingUtilities;

import game.Game;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      Game game = new Game();
      game.setOneScoreGame(true);
      game.start();
    });
  }
}
