package game.elements;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import game.Player;

public class WinLine {
  private Player player;
  private ArrayList<Point> points;
  private ArrayList<Line2D.Double> lines = new ArrayList<>();

  public WinLine(ArrayList<Point> points, Player player) {
    setPlayer(player);
    setPoints(points);
    setLines(points);
  }

  private void setLines(ArrayList<Point> points) {
    if (points.size() < 2)
      return;

    lines.clear();

    for (int i = 0; i < points.size() - 1; i++) {
      Line2D.Double line = new Line2D.Double(points.get(i).getCenterX(), points.get(i).getCenterY(),
          points.get(i + 1).getCenterX(), points.get(i + 1).getCenterY());
      lines.add(line);
    }
  }

  public void draw(Graphics2D g2d) {
    g2d.setColor(player.getColor());
    g2d.setStroke(new BasicStroke(2));
    for (Line2D.Double line : lines)
      g2d.draw(line);
  }

  public Player getPlayer() { return player; }

  public void setPlayer(Player player) { this.player = player; }

  public ArrayList<Point> getPoints() { return points; }

  public void setPoints(ArrayList<Point> points) { this.points = points; }
}
