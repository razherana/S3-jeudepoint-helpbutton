package game.elements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import game.Player;

public class HelpPoint extends Ellipse2D.Double {
  private final Player player;

  public Player getPlayer() { return player; }

  public HelpPoint(double x, double y, Player player) {
    super(x * game.Game.SQUARE_WIDTH - Point.RADIUS, y * game.Game.SQUARE_WIDTH - Point.RADIUS, Point.RADIUS * 2, Point.RADIUS * 2);
    this.player = player;
  }

  public void draw(Graphics2D g) {
    g.setColor(player.getColor());
    g.setStroke(new BasicStroke(2));
    g.draw(this);
    g.dispose();
  }
}
