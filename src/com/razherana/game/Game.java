package game;

import java.util.ArrayList;

import aff.Frame;
import aff.Panel;
import game.elements.HelpPoint;
import game.elements.Point;
import game.elements.Terrain;
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

  public HelpPoint getHelpPoint() { return helpPoint; }

  public void setHelpPoint(HelpPoint helpPoint) { this.helpPoint = helpPoint; }

  private final Terrain terrain;
  private final ArrayList<Point> clickablePoints = new ArrayList<>();
  private final PointClickedListener pointClickedListener = new PointClickedListener(this);
  private final ShowHelpPointListener showHelpPointListener = new ShowHelpPointListener(this);

  public ShowHelpPointListener getShowHelpPointListener() { return showHelpPointListener; }

  public PointClickedListener getPointClickedListener() { return pointClickedListener; }

  public void reset() {
    turn = 0;
    players.forEach(p -> p.reset());
    clickablePoints.clear();
    clickablePoints.addAll(terrain.getClickablePoints());
    panel.repaint();
  }

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
}
