package game;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import aff.Frame;
import aff.Panel;
import game.elements.HelpPoint;
import game.elements.Point;
import game.elements.Terrain;
import game.elements.WinLine;
import listeners.PointClickedListener;
import listeners.ShowHelpPointListener;

public class Game {
  public static final int FPS = 60;
  public static final int SQUARE_WIDTH = 50;
  public static final int COUNT_WIN = 5;

  private int turn = 0;
  private ArrayList<Player> players = new ArrayList<>();
  private int width, height;
  private Frame frame;
  private Panel panel;
  private HelpPoint helpPoint = null;
  private boolean oneScoreGame = false;

  private Terrain terrain;

  private final ArrayList<Point> clickablePoints = new ArrayList<>();

  private final PointClickedListener pointClickedListener = new PointClickedListener(this);

  private final ShowHelpPointListener showHelpPointListener = new ShowHelpPointListener(this);

  public Game() {
    width = 800;
    height = 600;

    // Test players
    addPlayer(new Player(this, "Player 1", java.awt.Color.RED));
    addPlayer(new Player(this, "Player 2", java.awt.Color.GREEN));

    panel = new Panel(this);
    frame = new Frame(this);

    terrain = new Terrain(16, 12);
    clickablePoints.addAll(terrain.getClickablePoints());
  }

  public boolean isOneScoreGame() { return oneScoreGame; }

  public void setOneScoreGame(boolean oneScoreGame) { this.oneScoreGame = oneScoreGame; }

  public HelpPoint getHelpPoint() { return helpPoint; }

  public void setHelpPoint(HelpPoint helpPoint) { this.helpPoint = helpPoint; }

  public void setTerrain(Terrain terrain) { this.terrain = terrain; }

  public ShowHelpPointListener getShowHelpPointListener() { return showHelpPointListener; }

  public PointClickedListener getPointClickedListener() { return pointClickedListener; }

  public void reset() {
    turn = 0;
    players.forEach(p -> p.reset());
    clickablePoints.clear();
    clickablePoints.addAll(terrain.getClickablePoints());
    panel.repaint();
  }

  public int getTurn() { return turn; }

  public void setTurn(int turn) { this.turn = turn; }

  public ArrayList<Player> getPlayers() { return players; }

  public void setPlayers(ArrayList<Player> players) { this.players = players; }

  public void addPlayer(Player player) { players.add(player); }

  public void nextTurn() {
    turn = (turn + 1) % players.size();
    helpPoint = null;
    panel.repaint();
  }

  public Player getCurrentPlayer() { return players.get(turn); }

  public ArrayList<Point> getClickablePoints() { return clickablePoints; }

  public Terrain getTerrain() { return terrain; }

  public Frame getFrame() { return frame; }

  public void setFrame(Frame frame) { this.frame = frame; }

  public Panel getPanel() { return panel; }

  public void setPanel(Panel panel) { this.panel = panel; }

  public void start() { new Thread(panel).start(); }

  public int getWidth() { return width; }

  public int getHeight() { return height; }

  public void updateWinner() {
    for (Player player : getPlayers()) {
      boolean breakAgain = false;
      ArrayList<Point> line;

      while ((line = player.checkWin()) != null) {
        System.out.println(player.getName() + " wins!");

        player.getUsablePoints().removeAll(line);
        player.incrementScore();
        player.getWinLines().add(new WinLine(line, player));

        if (isOneScoreGame()) {
          int choice = JOptionPane.showConfirmDialog(getFrame(), player.getName() + " wins! Wanna continue?",
              "Game Over", JOptionPane.YES_NO_OPTION);
          if (choice != JOptionPane.YES_OPTION) {
            reset();
            breakAgain = true;
            break;
          }
          setOneScoreGame(false);
        }
      }

      if (breakAgain)
        break;
    }
  }
}
