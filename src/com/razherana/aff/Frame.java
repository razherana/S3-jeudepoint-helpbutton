package aff;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import game.Game;
import game.save.SaveProperties;

public class Frame extends javax.swing.JFrame {
  private Game game;

  public Game getGame() { return game; }

  public void setGame(Game game) { this.game = game; }

  public Frame(Game game) {
    super("Andriamanitra Fitiavana ianao!");
    setSize(800, 600);
    setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

    setVisible(true);
    setGame(game);

    Panel panel = game.getPanel();

    getContentPane().add(panel);

    // Add menubar here

    JMenuBar menubar = new JMenuBar();

    JMenu menu = new JMenu("File");

    JMenuItem save = new JMenuItem("Save");
    JMenuItem load = new JMenuItem("Load");
    JMenuItem exit = new JMenuItem("Exit");

    exit.addActionListener((e) -> System.exit(0));

    load.addActionListener((e) -> {
      JFileChooser jFileChooser = new JFileChooser(".");
      if (jFileChooser.showOpenDialog(Frame.this) != JFileChooser.APPROVE_OPTION)
        return;

      File file = jFileChooser.getSelectedFile();
      SaveProperties.load(game, file);
    });

    save.addActionListener((e) -> {
      JFileChooser jFileChooser = new JFileChooser(".");
      if (jFileChooser.showSaveDialog(Frame.this) != JFileChooser.APPROVE_OPTION)
        return;

      File file = jFileChooser.getSelectedFile();
      SaveProperties.save(game, file);
    });

    menu.add(save);
    menu.add(load);
    menu.add(exit);

    menubar.add(menu);

    JMenu gameMenu = new JMenu("Game");

    JMenuItem help = new JMenuItem("Help");
    help.addActionListener((e) -> game.getShowHelpPointListener().showHelp());

    JMenuItem reset = new JMenuItem("Reset");
    reset.addActionListener((e) -> {
      if (JOptionPane.showConfirmDialog(Frame.this, "Are you sure you wanna reset ?", "Confirm",
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        game.reset();
      }
    });

    gameMenu.add(help);
    gameMenu.add(reset);

    menubar.add(gameMenu);

    add(menubar, BorderLayout.NORTH);
  }
}
