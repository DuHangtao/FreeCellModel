package cs3500.hw03;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs3500.hw02.FreecellOperations;
import cs3500.hw02.PileType;
import cs3500.hw02.Pocker;

/**
 * represent a controller of the free cell game. This class controls the inputs given by the user,
 * and output that is then transmitted to the user.
 */
public class FreecellController implements IFreecellController<Pocker> {
  private Appendable ap;
  private Scanner sc;

  /**
   * Constructs a free cell controller of the free cell game with a readable object to read user's
   * inputs, and an appendable object to transmit the message/game state to the user.
   *
   * @param rd readable object for reading user's inputs.
   * @param ap appendable object for transmitting message/game state.
   */
  public FreecellController(Readable rd, Appendable ap) {
    /**
     * the input and output can not be null
     * if they are null, throw the IllegalStateException.
     */
    if (rd == null || ap == null) {
      throw new IllegalStateException("null input or output");
    }
    this.ap = ap;
    this.sc = new Scanner(rd);
  }

  /**
   * the helper method to append the given message/game state to the current game state.
   *
   * @param s String is a given message.
   */
  private void addMessage(String s) {
    try {
      ap.append(s);
    } catch (IOException e) {
      return;
    }
  }

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

  @Override
  public void playGame(List<Pocker> deck, FreecellOperations<Pocker> model,
                       int numCascades, int numOpens, boolean shuffle) {
    if (deck == null || model == null) {
      throw new IllegalArgumentException("the game did not start yet");
    }

    /**
     *  to call the start method to start the game by the given model.
     *  if it catches any IllegalArgumentException, it means the format of the given parameters
     *  are not correct, thus append "Could not start game." the the current game state.
     */
    try {
      model.startGame(deck, numCascades, numOpens, shuffle);
    } catch (IllegalArgumentException e) {
      addMessage("Could not start game.");
      return;
    }

    /**
     * to transmit the initial game state to the user.
     */
    addMessage(model.getGameState());


    Integer counter;

    /**
     * to initialize the counter and make it to 0;
     * counter is to record do we have a valid move which only happen when counter is 3
     * also, to initialize all the values which are be useful when calling move method.
     */
    counter = 0;
    PileType sourcePileType = null;
    int sourcePileNumber = 0;
    int sourceCardIndex = 0;
    PileType destPile = null;
    int destPileNumber = 0;

    /**
     * the while loop will be terminated only when the scanner does not have the next element.
     */
    while (sc.hasNext()) {

      String s = sc.next();

      /**
       * to check if the input is a Q or q, if so quit the game prematurely.
       */
      if (s.equals("q") || s.equals("Q")) {
        addMessage("\nGame quit prematurely.");
        return;
      }

      /**
       * when the counter is 0,
       * to search the valid source pile.
       * if the current input is not a valid source pile
       * keep looking for the source pile until you find it, and counter + 1.
       * otherwise the counter will always be 0.
       *
       * when the counter is 1,
       * which means that we already have the valid source pile.
       * to search the valid source card index, counter++.
       *
       *
       * when the counter is 2,
       * which means that we already have the valid source pile and source card index.
       * to search the valid source dest pile, counter++.
       */
      if (counter == 0) {
        try {
          if (checkValidPile(s)) {
            sourcePileType = returnPileType(s);
            sourcePileNumber = returnPileNumber(s) - 1;
            counter++;
          }
        } catch (IllegalArgumentException e) {
          addMessage("\nthe source pile is invalid, please try again");
        }
      } else if (counter == 1) {
        try {
          if (checkValidCardIndex(s)) {
            sourceCardIndex = returnCardIndex(s) - 1;
            counter++;
          }
        } catch (IllegalArgumentException e) {
          addMessage("\nthe source card index is invalid, please try again");
        }
      } else if (counter == 2) {
        try {
          if (checkValidPile(s)) {
            destPile = returnPileType(s);
            destPileNumber = returnPileNumber(s) - 1;
            counter++;
          }
        } catch (IllegalArgumentException e) {
          addMessage("\nthe dest pile is invalid, please try again");
        }
      }

      /**
       * when counter equal to 3,
       * which means that we already have a valid move only at this point.
       * then, call the move method to change the the game state
       * append the current game state to the game state before.
       *
       * to initialize the counter to 0 after moving.
       */
      if (counter == 3) {
        try {
          model.move(sourcePileType, sourcePileNumber, sourceCardIndex, destPile, destPileNumber);
        } catch (IllegalArgumentException e) {
          addMessage("\nInvalid move. Try again.");
        }

        addMessage("\n" + model.getGameState());
        counter = 0;
      }
    }

    /**
     * to check if the game if over.
     */
    if (model.isGameOver()) {
      addMessage("\nGame over.");
      return;
    }
  }

  /**
   * to get the correct pile type by transferring the given string.
   *
   * @param s String which may a valid pile.
   */
  private PileType returnPileType(String s) {
    PileType type;
    switch (s.charAt(0)) {
      case 'C':
        type = PileType.CASCADE;
        break;
      case 'O':
        type = PileType.OPEN;
        break;
      case 'F':
        type = PileType.FOUNDATION;
        break;

      default:
        throw new IllegalArgumentException("no such pile type");
    }
    return type;
  }

  /**
   * the helper method to check if the given String is a valid source pile. a valid source pile
   * start with a string "C" "F" or "O" following with an integer.
   *
   * @param s String which may a valid pile or not.
   */
  private boolean checkValidPile(String s) {
    Character pileType = s.charAt(0);
    String pileNumber = s.substring(1);
    if (pileType == 'C' || pileType == 'F' || pileType == 'O') {
      try {
        Integer.parseInt(pileNumber);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("the pile number should be an integer, try again");
      } catch (NullPointerException e) {
        throw new IllegalArgumentException("\nthe pile number can not be a null, try again");
      }
    } else {
      throw new IllegalArgumentException("the pile type is not valid, try again");
    }
    return true;
  }

  /**
   * the helper method to check if the given String is a valid card index a valid card index is a
   * integer bigger than or equal to 1, else is false.
   *
   * @param s String which may a number inside of the string.
   */
  private boolean checkValidCardIndex(String s) {
    try {
      return Integer.parseInt(s) >= 1;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("\nthe card index should be a number, please try again");
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("\nthe card index should not be a null, please try again");
    }
  }

  /**
   * a helper method simply transfer a string to an integer.
   *
   * @param s a valid card index.
   */
  private int returnCardIndex(String s) {
    return Integer.parseInt(s);
  }

  /**
   * a helper method simply transfer a string to an integer.
   *
   * @param s a valid pile.
   */
  private int returnPileNumber(String s) {
    return Integer.parseInt(s.substring(1));
  }
}


