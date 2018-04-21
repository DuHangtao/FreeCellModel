package cs3500.hw03;

import cs3500.hw02.FreecellOperations;
import java.util.List;

/**
 * This is the interface of the Freecell controller. It is parameterized over the card type.
 */
public interface IFreecellController<K> {
  /**
   * Plays the free cell game based on user's inputs. It asks the provided model to start a game
   * with the provided inputs, and then “run” the game in the following sequence until the game is
   * over. However, if the user wants the quit the game at any point by giving a "Q" or "q", it will
   * just end the game.
   *
   * @param deck        deck of cards which is a list of 52 cards provided by user;
   * @param model       model of free cell provided by the user.
   * @param numCascades number of cascade piles provided by the user.
   * @param numOpens    number of open piles provided by the user.
   * @param shuffle     true if one wants the deck to be shuffled, false otherwise.
   */
  void playGame(List<K> deck, FreecellOperations<K> model, int numCascades,
                int numOpens, boolean shuffle);
}





