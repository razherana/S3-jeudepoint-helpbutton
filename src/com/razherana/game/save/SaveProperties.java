package game.save;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import game.Game;
import game.Player;
import game.elements.Point;
import game.elements.Terrain;

public class SaveProperties {
  private int turn = 0;
  private ArrayList<Player> players = new ArrayList<>();
  private ArrayList<Point> clickables = new ArrayList<>();
  private Properties properties;
  private Terrain terrain;
  private boolean oneScoreGame;

  public static void save(Game game, File file) {
    SaveProperties s = new SaveProperties();
    s.clickables = game.getClickablePoints();
    s.players = game.getPlayers();
    s.turn = game.getTurn();
    s.terrain = game.getTerrain();
    s.oneScoreGame = game.isOneScoreGame();

    s.save(file);
  }

  public static void load(Game game, File file) {
    SaveProperties s = new SaveProperties();
    s.load(file, game);

    game.setOneScoreGame(s.oneScoreGame);
    game.setTurn(s.turn);
    game.getClickablePoints().clear();
    game.getClickablePoints().addAll(s.clickables);
    game.setPlayers(s.players);
    game.setTerrain(s.terrain);

    game.updateWinner();
  }

  private void save(File file) {
    properties = new Properties();

    properties.setProperty("oneScoreGame", oneScoreGame + "");

    properties.setProperty("turn", turn + "");

    properties.setProperty("terrain", terrain.getLengthX() + "," + terrain.getLengthY());

    properties.setProperty("players",
        String.join(",", new ArrayList<>(players).stream().map(e -> e.getName()).toList()));

    int playerId = 0;

    for (Player player : new ArrayList<>(players)) {
      properties.setProperty("color_" + playerId, player.getColor().getRGB() + "");
      properties.setProperty("points_" + playerId, String.join(";",
          new ArrayList<>(player.getPoints()).stream().map(e -> e.getGameX() + "," + e.getGameY()).toList()));
      playerId++;
    }

    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      properties.storeToXML(fileOutputStream, "Save of game : " + LocalDateTime.now());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void load(File file, Game game) {
    properties = new Properties();

    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      properties.loadFromXML(fileInputStream);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Properties invalid");
      return;
    }

    int[] lengthsTerrain = Arrays.stream(properties.get("terrain").toString().split(",")).mapToInt(Integer::parseInt)
        .toArray();

    oneScoreGame = Boolean.parseBoolean(properties.get("oneScoreGame").toString());

    terrain = new Terrain(lengthsTerrain[0], lengthsTerrain[1]);

    turn = Integer.parseInt(properties.get("turn").toString());

    players = new ArrayList<>(
        Arrays.stream(properties.get("players").toString().split(",")).map(e -> new Player(game, e, null)).toList());

    int playerId = 0;
    for (Player player : players) {
      player.setColor(new Color(Integer.parseInt(properties.get("color_" + playerId).toString())));
      player.setPoints(
          new ArrayList<>(Arrays.stream(properties.get("points_" + playerId).toString().split(";")).map(e -> {
            Integer[] ints = Arrays.stream(e.split(",")).map(Integer::parseInt).toList().toArray(new Integer[] {});
            return new Point(player, ints[0], ints[1]);
          }).toList()));
      player.setUsablePoints(new ArrayList<>(player.getPoints()));
      playerId++;
    }

    clickables = terrain.getClickablePoints();
    players.forEach(p -> clickables.removeAll(p.getPoints()));
  }
}
