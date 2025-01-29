package listeners;

import game.elements.Point;
import java.awt.geom.Point2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import game.Game;
import game.Player;

public class PointClickedListener extends MouseAdapter {
  private Game game;

  public PointClickedListener(Game game) { this.game = game; }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() != MouseEvent.BUTTON1)
      return;

    Point2D.Double point = new Point2D.Double(e.getX(), e.getY());

    Point clicked = game.getClickablePoints().stream()
        .filter(p -> point.distance(p.getCenterX(), p.getCenterY()) <= Point.RADIUS).findFirst().orElse(null);

    if (clicked != null) {
      game.getCurrentPlayer().addPoint(clicked);
      game.getPanel().repaint();
      game.updateWinner();
      game.nextTurn();
    }

    for (Player player : game.getPlayers()) {
      System.out.println(player.getName() + " : " + player.getPoints().size());
    }
  }

}
