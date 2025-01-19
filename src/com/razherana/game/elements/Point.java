package game.elements;

import java.awt.geom.Ellipse2D;

import game.Player;

public class Point extends Ellipse2D.Double {
  public static final int RADIUS = 10;

  private Player player;
  private int gameX, gameY;

  public Point(Player player, int gameX, int gameY) {
    this.gameX = gameX;
    this.gameY = gameY;
    this.player = player;

    width = RADIUS;
    height = RADIUS;

    x = gameX * game.Game.SQUARE_WIDTH - RADIUS / 2;
    y = gameY * game.Game.SQUARE_WIDTH - RADIUS / 2;
  }

  public void draw(java.awt.Graphics2D g) {
    g.setColor(player.getColor());
    g.fill(this);
  }

  public Player getPlayer() { return player; }

  public void setPlayer(Player player) { this.player = player; }

  public int getGameY() { return gameY; }

  public void setGameY(int gameY) { this.gameY = gameY; }

  public int getGameX() { return gameX; }

  public void setGameX(int gameX) { this.gameX = gameX; }
}
