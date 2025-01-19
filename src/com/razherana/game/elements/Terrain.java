package game.elements;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Terrain extends Rectangle2D.Double {
  private int lengthX, lengthY;

  public Terrain(int lengthX, int lengthY) {
    x = 0;
    y = 0;

    width = game.Game.SQUARE_WIDTH * lengthX;
    height = game.Game.SQUARE_WIDTH * lengthY;

    this.lengthX = lengthX;
    this.lengthY = lengthY;
  }

  public ArrayList<Point> getClickablePoints() {
    ArrayList<Point> points = new ArrayList<>();
    for (int i = 0; i < lengthX; i++)
      for (int j = 0; j < lengthY; j++)
        points.add(new Point(null, i, j));
    return points;
  }

  public void draw(java.awt.Graphics2D g) {
    g.setColor(java.awt.Color.LIGHT_GRAY);
    g.fill(this);

    g.setColor(java.awt.Color.BLUE);
    for (int i = 0; i <= width; i += game.Game.SQUARE_WIDTH) {
      g.drawLine(i, 0, i, (int) height);
    }
    for (int j = 0; j <= height; j += game.Game.SQUARE_WIDTH) {
      g.drawLine(0, j, (int) width, j);
    }

    g.dispose();
  }

  public int getLengthX() { return lengthX; }

  public void setLengthX(int lengthX) {
    this.lengthX = lengthX;
    width = game.Game.SQUARE_WIDTH * lengthX;
  }

  public int getLengthY() { return lengthY; }

  public void setLengthY(int lengthY) {
    this.lengthY = lengthY;
    height = game.Game.SQUARE_WIDTH * lengthY;
  }
}
