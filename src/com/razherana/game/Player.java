package game;

import java.awt.Color;
import java.util.ArrayList;

import game.elements.HelpPoint;
import game.elements.Point;
import game.elements.WinLine;

public class Player {
  private String name;
  private int score = 0;
  private ArrayList<Point> points = new ArrayList<>();
  private ArrayList<WinLine> winLines = new ArrayList<>();

  public int getScore() { return score; }

  public void setScore(int score) { this.score = score; }

  public void incrementScore() { score++; }

  private ArrayList<Point> usablePoints = new ArrayList<>();

  public ArrayList<Point> getUsablePoints() { return usablePoints; }

  public void setUsablePoints(ArrayList<Point> usablePoints) { this.usablePoints = usablePoints; }

  private Game game;
  private Color color;

  public Color getColor() { return color; }

  public void setColor(Color color) { this.color = color; }

  public Player(Game game, String name, Color color) {
    this.name = name;
    this.game = game;
    this.color = color;
  }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

  public ArrayList<Point> getPoints() { return points; }

  public void setPoints(ArrayList<Point> points) { this.points = points; }

  public Game getGame() { return game; }

  public void setGame(Game game) { this.game = game; }

  public void addPoint(Point point) {
    point.setPlayer(this);
    getPoints().add(point);
    getUsablePoints().add(point);
    game.getClickablePoints().removeIf(p -> p.getX() == point.getX() && p.getY() == point.getY());
  }

  public ArrayList<Point> checkWin() { return getContinuousPoints(Game.COUNT_WIN); }

  public void reset() {
    points.clear();
    usablePoints.clear();
    winLines.clear();
    score = 0;
  }

  private HelpPoint getInstantWin() { return getAttackHelpPoint(Game.COUNT_WIN - 1); }

  private ArrayList<Point> getContinuousPoints(int countNeeded) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (getUsablePoints().size() < countNeeded)
      return null;

    for (Point point : new ArrayList<>(getUsablePoints())) {
      int x = point.getGameX();
      int y = point.getGameY();

      final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } };

      // Check all directions
      for (int check = 0, count = 1; check < checks.length; check++) {
        ArrayList<Point> resultPoints = new ArrayList<>();
        resultPoints.add(point);
        count = 1;
        for (int i = 1; i < countNeeded; i++) {
          final int checkIndex = check, index = i;
          if (points.stream().anyMatch(p -> p.getGameX() == x + checks[checkIndex][0] * index
              && p.getGameY() == y + checks[checkIndex][1] * index)) {
            resultPoints.add(new Point(this, x + checks[checkIndex][0] * index, y + checks[checkIndex][1] * index));
            count++;
          } else {
            resultPoints.clear();
            break;
          }
        }

        final int checkIndex = check;

        if (count == countNeeded && getGame().getClickablePoints().stream()
            .anyMatch((e) -> e.getGameX() == x + checks[checkIndex][0] * countNeeded
                && e.getGameY() == y + checks[checkIndex][1] * countNeeded)) {
          return resultPoints;
        }
      }
    }

    return null;
  }

  private HelpPoint getAttackHelpPoint(int countNeeded) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (getUsablePoints().size() < countNeeded)
      return null;

    System.out.println("Check point start");
    for (Point point : new ArrayList<>(getUsablePoints())) {
      System.out.println();
      int x = point.getGameX();
      int y = point.getGameY();
      System.out.println("Depart = x=" + x + ",y=" + y);

      final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } };

      // Check all directions
      for (int check = 0, count = 1; check < checks.length; check++) {
        count = 1;
        System.out.println();
        System.out.println("Check " + check);
        for (int i = 1; i < countNeeded; i++) {
          final int checkIndex = check, index = i;
          if (points.stream().anyMatch(p -> p.getGameX() == x + checks[checkIndex][0] * index
              && p.getGameY() == y + checks[checkIndex][1] * index)) {
            System.out.println("Point mitohy : x=" + (x + checks[checkIndex][0] * index) + ",y="
                + (y + checks[checkIndex][1] * index));
            count++;
          } else {
            System.out.println(
                "Tapaka eto : x=" + (x + checks[checkIndex][0] * index) + ",y=" + (y + checks[checkIndex][1] * index));
            break;
          }
        }

        final int checkIndex = check;

        if (count == countNeeded) {
          if (getGame().getClickablePoints().stream()
              .anyMatch((e) -> e.getGameX() == x + checks[checkIndex][0] * countNeeded
                  && e.getGameY() == y + checks[checkIndex][1] * countNeeded)) {
            System.out.println("Eto help : x = " + (x + checks[checkIndex][0] * countNeeded) + ", y = "
                + (y + checks[checkIndex][1] * countNeeded));
            return new HelpPoint(x + checks[check][0] * countNeeded, y + checks[check][1] * countNeeded, this);
          }
          System.out.println("Tsy clickable intsony iny point iny : x = " + (x + checks[checkIndex][0] * countNeeded)
              + ", y = " + (y + checks[checkIndex][1] * countNeeded));
        }
      }
    }
    System.out.println("Check point end");
    return null;
  }

  public class NotYourTurnException extends Exception {
    public NotYourTurnException() {
      super("It's not your turn '" + getName() + "'! " + getGame().getCurrentPlayer().getName() + " is playing.");
    }
  }

  public class CountPointsException extends Exception {
    public CountPointsException() {
      super("You can't get helpPoint anymore, you have reached the maximum number of points.");
    }
  }

  public class NoHelpPointException extends Exception {
    public NoHelpPointException() { super("There are no helpPoints available."); }
  }

  public HelpPoint getHelp() throws NotYourTurnException, CountPointsException, NoHelpPointException {
    // Check if not the player's turn
    // This shouldn't happen, but just in case
    if (game.getTurn() != game.getPlayers().indexOf(this))
      throw new NotYourTurnException();

    // Check if there are no clickable points
    if (game.getClickablePoints().isEmpty())
      throw new CountPointsException();

    // Check if we miss points to get help
    if (getUsablePoints().size() < Game.COUNT_WIN - 2)
      throw new NoHelpPointException();

    // Check for instant win
    HelpPoint instantWin = getInstantWin();
    if (instantWin != null)
      return instantWin;

    // Check if there are instantWins for other players
    // Then we need to block them
    for (Player player : game.getPlayers().stream().filter(p -> !p.equals(this)).toList()) {
      HelpPoint helpPoint = player.getInstantWin();
      if (helpPoint != null)
        return helpPoint;
    }

    // Check for attack
    HelpPoint attackHelpPoint = getAttackHelpPoint(Game.COUNT_WIN - 2);
    if (attackHelpPoint != null)
      return attackHelpPoint;

    throw new NoHelpPointException();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  public ArrayList<WinLine> getWinLines() { return winLines; }

  public void setWinLines(ArrayList<WinLine> winLines) { this.winLines = winLines; }

}
