package aff;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.Game;
import game.Player;

public class Panel extends JPanel implements Runnable {
  private Game game;
  private Font font = new Font("arial", Font.BOLD, 18);

  public Game getGame() { return game; }

  public void setGame(Game game) { this.game = game; }

  public Panel(Game game) {
    super(true);
    setGame(game);
    setFocusable(true);

    addMouseListener(game.getPointClickedListener());
    addKeyListener(game.getShowHelpPointListener());
  }

  @Override
  public void run() {
    long curr;
    double fpsDiv = (double) 1E9 / (double) Game.FPS;
    while (true) {
      curr = System.nanoTime();

      update();
      repaint();

      while (System.nanoTime() - curr < fpsDiv) {
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    drawTerrain((Graphics2D) g.create());

    drawPoints((Graphics2D) g.create());

    drawHelpPoint((Graphics2D) g.create());

    drawScore((Graphics2D) g.create());

    drawWinLines((Graphics2D) g.create());

    g.dispose();
  }

  private void drawWinLines(Graphics2D graphics2d) {
    getGame().getPlayers().forEach((e) -> e.getWinLines().forEach(l -> l.draw(graphics2d)));
    graphics2d.dispose();
  }

  private void drawScore(Graphics2D graphics2d) {
    graphics2d.setFont(font);
    int i = 0;
    for (Player player : getGame().getPlayers())
      graphics2d.drawString(player.getName() + " = " + player.getScore(), 20, 20 + 25 * i++);
    graphics2d.dispose();
  }

  private void drawHelpPoint(Graphics2D graphics2d) {
    if (game.getHelpPoint() != null)
      game.getHelpPoint().draw(graphics2d);
    graphics2d.dispose();
  }

  private void drawPoints(Graphics2D graphics2d) {
    for (Player player : game.getPlayers())
      player.getPoints().forEach(point -> point.draw(graphics2d));
    graphics2d.dispose();
  }

  private void drawTerrain(Graphics2D g) { game.getTerrain().draw(g); }

  private void update() {}
}
