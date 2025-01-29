package game;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import game.elements.HelpPoint;
import game.elements.Point;
import game.elements.WinLine;

public class Player {
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
    public NoHelpPointException() {
      super("There are no helpPoints available.");
    }
  }

  private String name;
  private int score = 0;
  private ArrayList<Point> points = new ArrayList<>();
  private ArrayList<WinLine> winLines = new ArrayList<>();
  private ArrayList<Point> usablePoints = new ArrayList<>();
  private Game game;
  private Color color;
  private int limit;
  private boolean hasLimit = false;

  public boolean hasLimit() {
    return hasLimit;
  }

  public void setHasLimit(boolean hasLimit) {
    this.hasLimit = hasLimit;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public Player(Game game, String name, Color color) {
    this.name = name;
    this.game = game;
    this.color = color;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void incrementScore() {
    score++;
  }

  public ArrayList<Point> getUsablePoints() {
    return usablePoints;
  }

  public void setUsablePoints(ArrayList<Point> usablePoints) {
    this.usablePoints = usablePoints;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Point> getPoints() {
    return points;
  }

  public void setPoints(ArrayList<Point> points) {
    this.points = points;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  private void removeFirst() {
    Point point = getPoints().remove(0);
    getUsablePoints().remove(point);
    point.setPlayer(null);
    game.getClickablePoints().add(point);
  }

  public void addPoint(Point point) {
    point.setPlayer(this);
    getPoints().add(point);
    getUsablePoints().add(point);
    game.getClickablePoints().removeIf(p -> p.getX() == point.getX() && p.getY() == point.getY());

    if (hasLimit() && getPoints().size() > getLimit())
      removeFirst();
  }

  public ArrayList<Point> checkWin() {
    if (getPoints().size() < Game.COUNT_WIN)
      return null;

    int[] points = { Game.COUNT_WIN - 2, Game.COUNT_WIN - 1 };
    HashMap<Integer, HashSet<Map.Entry<Point, List<Integer>>>> listUsed = new HashMap<>();
    ArrayList<Point> pointList = new ArrayList<>(getUsablePoints());
    ArrayList<HashSet<Point>> lineList = new ArrayList<>();

    for (int point : points)
      listUsed.put(point, new HashSet<>());

    for (int point : points) {
      var list1 = getContinuousPoints(point, pointList, listUsed.get(point));
      while (list1 != null) {
        if (!lineList.contains(new HashSet<>(list1))) {
          var direction = getDirectionOfPoints(list1);
          var list2 = getContinuousPointsFromPoint(direction, list1.get(0), Game.COUNT_WIN - point + 1);

          if (list2 != null) {
            var listFinal = new ArrayList<>(list1);
            listFinal.addAll(list2);
            return listFinal;
          }

          list2 = getContinuousPointsFromPoint(direction, list1.get(list1.size() - 1), Game.COUNT_WIN - point + 1);

          if (list2 != null) {
            var listFinal = new ArrayList<>(list1);
            listFinal.addAll(list2);
            return listFinal;
          }

          lineList.add(new HashSet<>(list1));
        }
        list1 = getContinuousPoints(point, pointList, listUsed.get(point));
      }
    }

    return null;
  }

  private HelpPoint getAttackHelpPoint(int point) {
    HashSet<Map.Entry<Point, List<Integer>>> listUsed = new HashSet<>();
    ArrayList<Point> pointList = new ArrayList<>(getUsablePoints());
    ArrayList<HashSet<Point>> lineList = new ArrayList<>();

    var list1 = getContinuousPoints(point, pointList, listUsed);
    while (list1 != null) {
      if (!lineList.contains(new HashSet<>(list1))) {
        var direction = getDirectionOfPoints(list1);
        var list2 = getContinuousPointsHelpFromPoint(direction, list1.get(0), Game.COUNT_WIN - point);

        if (list2 != null) {
          var listFinal = new ArrayList<>(list1);
          listFinal.addAll(list2);
          var helpPoint = listFinal.get(listFinal.size() - 1);
          return new HelpPoint(helpPoint.getGameX(), helpPoint.getGameY(), this);
        }

        list2 = getContinuousPointsHelpFromPoint(direction, list1.get(list1.size() - 1), Game.COUNT_WIN - point);

        if (list2 != null) {
          var listFinal = new ArrayList<>(list1);
          listFinal.addAll(list2);
          var helpPoint = listFinal.get(listFinal.size() - 1);
          return new HelpPoint(helpPoint.getGameX(), helpPoint.getGameY(), this);
        }

        lineList.add(new HashSet<>(list1));
      }
      list1 = getContinuousPoints(point, pointList, listUsed);
    }

    return null;
  }

  private int[] getDirectionOfPoints(ArrayList<Point> points) {
    var pointFin = points.get(points.size() - 1);
    var pointAvantFin = points.get(points.size() - 2);
    return new int[] {
        pointAvantFin.getGameX() - pointFin.getGameX(),
        pointAvantFin.getGameY() - pointFin.getGameY()
    };
  }

  public void reset() {
    points.clear();
    usablePoints.clear();
    winLines.clear();
    score = 0;
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
    HelpPoint attackHelpPoint = getSimpleAttack();
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

  public ArrayList<WinLine> getWinLines() {
    return winLines;
  }

  public void setWinLines(ArrayList<WinLine> winLines) {
    this.winLines = winLines;
  }

  private HelpPoint getInstantWin() {
    var res = getAttackHelpPoint(Game.COUNT_WIN - 1);
    if (res != null)
      return res;
    return getAttackHelpPoint(Game.COUNT_WIN - 2);
  }

  private HelpPoint getSimpleAttack() {
    var res = getContinuousPointsHelp(Game.COUNT_WIN - 2, new ArrayList<>(getUsablePoints()), null);

    if (res != null) {
      var point = res.get(res.size() - 1);
      var helpPoint = new HelpPoint(point.getGameX(), point.getGameY(), this);
      return helpPoint;
    }

    // Three then turn
    res = getContinuousPoints(Game.COUNT_WIN - 2, new ArrayList<>(getUsablePoints()), null);
    if (res != null) {
      var direction = getDirectionOfPoints(res);
      var crossed = getContinuousPointsHelpFromPoint(direction, res.get(0), 1);
      if (crossed != null) {
        var point = crossed.get(crossed.size() - 1);
        var helpPoint = new HelpPoint(point.getGameX(), point.getGameY(), this);
        return helpPoint;
      }

      direction = getDirectionOfPoints(res);
      crossed = getContinuousPointsHelpFromPoint(direction, res.get(res.size() - 1), 1);
      if (crossed != null) {
        var point = crossed.get(crossed.size() - 1);
        var helpPoint = new HelpPoint(point.getGameX(), point.getGameY(), this);
        return helpPoint;
      }
    }

    return null;
  }

  private ArrayList<Point> getContinuousPoints(int countNeeded, ArrayList<Point> points,
      HashSet<Entry<Point, List<Integer>>> listUsed) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (points.size() < countNeeded)
      return null;

    for (Point point : points) {
      int x = point.getGameX();
      int y = point.getGameY();

      final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

      // Check all directions
      for (int check = 0, count = 1; check < checks.length; check++) {
        final int checkFinaled = check;
        if (listUsed != null
            && listUsed.stream()
                .anyMatch((e) -> e.getKey().equals(point) && e.getValue().get(0) == checks[checkFinaled][0]
                    && e.getValue().get(1) == checks[checkFinaled][1]))
          continue;

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
            if (listUsed != null)
              listUsed.add(Map.entry(point, List.of(checks[check][0], checks[check][1])));
            break;
          }
        }

        if (count == countNeeded) {
          if (listUsed != null) {
            System.out.println("Eto ee");
            listUsed.add(Map.entry(point, List.of(checks[check][0], checks[check][1])));
          }
          return resultPoints;
        }
      }
    }

    return null;
  }

  private ArrayList<Point> getContinuousPointsHelp(int countNeeded, ArrayList<Point> points,
      HashSet<Entry<Point, List<Integer>>> listUsed) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (points.size() < countNeeded)
      return null;

    for (Point point : points) {
      int x = point.getGameX();
      int y = point.getGameY();

      final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

      // Check all directions
      for (int check = 0, count = 1; check < checks.length; check++) {
        final int checkFinaled = check;
        if (listUsed != null
            && listUsed.stream()
                .anyMatch((e) -> e.getKey().equals(point) && e.getValue().get(0) == checks[checkFinaled][0]
                    && e.getValue().get(1) == checks[checkFinaled][1]))
          continue;

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
            if (listUsed != null)
              listUsed.add(Map.entry(point, List.of(checks[check][0], checks[check][1])));
            break;
          }
        }

        if (count == countNeeded) {
          if (listUsed != null)
            listUsed.add(Map.entry(point, List.of(checks[check][0], checks[check][1])));

          Point point2 = new Point(this, x + checks[check][0] * count, y + checks[check][1] * count);
          if (getGame().getClickablePoints().contains(point2)) {
            resultPoints.add(point2);
            return resultPoints;
          }
        }
      }
    }

    return null;
  }

  private ArrayList<Point> getContinuousPointsFromPoint(int[] ignoreDirection, Point point, int countNeeded) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (getUsablePoints().size() < countNeeded)
      return null;

    var points = new ArrayList<>(getUsablePoints());

    int x = point.getGameX();
    int y = point.getGameY();

    final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    // Check all directions
    for (int check = 0, count = 1; check < checks.length; check++) {
      if (checks[check][0] == ignoreDirection[0] || checks[check][1] == ignoreDirection[1])
        continue;

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

      if (count == countNeeded)
        return resultPoints;
    }

    return null;
  }

  private ArrayList<Point> getContinuousPointsHelpFromPoint(int[] ignoreDirection, Point point, int countNeeded) {
    if (game.getClickablePoints().isEmpty())
      return null;

    if (getUsablePoints().size() < countNeeded)
      return null;

    var points = new ArrayList<>(getUsablePoints());

    int x = point.getGameX();
    int y = point.getGameY();

    final int[][] checks = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    // Check all directions
    for (int check = 0, count = 1; check < checks.length; check++) {
      if (checks[check][0] == ignoreDirection[0] || checks[check][1] == ignoreDirection[1])
        continue;

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

      if (count == countNeeded) {
        var helpPoint = new Point(this, x + checks[check][0] * count, y + checks[check][1] * count);
        if (getGame().getClickablePoints().contains(helpPoint)) {
          resultPoints.add(helpPoint);
          return resultPoints;
        }
      }
    }

    return null;
  }
}
