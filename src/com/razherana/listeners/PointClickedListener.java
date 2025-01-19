package listeners;

import game.elements.Point;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

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
      game.nextTurn();
      game.getPanel().repaint();
    }

    // Check winner
    for (Player player : game.getPlayers())
      if (player.checkWin()) {
        System.out.println(player.getName() + " wins!");
        JOptionPane.showMessageDialog(game.getFrame(), player.getName() + " wins!");
        game.reset();
        break;
      }
  }

}
