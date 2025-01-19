package listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import game.Game;
import game.Player.CountPointsException;
import game.Player.NoHelpPointException;
import game.Player.NotYourTurnException;
import game.elements.HelpPoint;

public class ShowHelpPointListener extends KeyAdapter {
  Game game;

  public ShowHelpPointListener(Game game) { this.game = game; }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() != KeyEvent.VK_H)
      return;

    if (game.getHelpPoint() != null)
      return;

    HelpPoint helpPoint = null;

    try {
      helpPoint = game.getCurrentPlayer().getHelp();
    } catch (NotYourTurnException e1) {
      JOptionPane.showMessageDialog(game.getPanel(), e1.getMessage());
      System.out.println(e1.getMessage());
      return;
    } catch (CountPointsException e1) {
      JOptionPane.showMessageDialog(game.getPanel(), e1.getMessage());
      System.out.println(e1.getMessage());
      return;
    } catch (NoHelpPointException e1) {
      JOptionPane.showMessageDialog(game.getPanel(), e1.getMessage());
      System.out.println(e1.getMessage());
      return;
    }

    game.setHelpPoint(helpPoint);
  }
}
