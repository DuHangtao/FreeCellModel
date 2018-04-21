package cs3500.hw04;

import cs3500.hw02.FreecellModel;

/**
 * A Factory Class to generate object of concrete class based on given information. This returns
 * either single move or multiple moves.
 */

public class FreecellModelCreator {

  /**
   * An enumeration of the game types which include two types. The first is SingleMove, and the
   * second is a MultiMove.
   */
  public enum GameType {
    SINGLEMOVE, MULTIMOVE
  }

  /**
   * A static factory method returns the correct game model by the given GameType.
   */
  public static FreecellModel create(GameType type) {
    FreecellModel model;
    switch (type) {
      case SINGLEMOVE:
        model = new FreecellModel();
        break;
      case MULTIMOVE:
        model = new FreecellMultipleMoveModel();
        break;
      default:
        throw new NullPointerException("no such GameType");
    }
    return model;
  }
}
