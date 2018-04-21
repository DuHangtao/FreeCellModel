package cs3500.hw02;


import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * the model of the free cell game which contains the methods that  play the game. it allows the
 * player to start game and move the card from piles to piles.
 */
public class FreecellModel implements FreecellOperations<Pocker> {
  protected List<List<Pocker>> openpile;
  protected List<List<Pocker>> cascadepile;
  protected List<List<Pocker>> foundationpile;

  /**
   * getDeck() return a valid deck Poker.
   */
  public List<Pocker> getDeck() {
    List<Pocker> cards = new ArrayList<Pocker>();
    for (int i = 0; i < Suits.values().length; i++) {
      for (int k = 0; k < Faces.values().length; k++) {
        cards.add(new Pocker(Faces.values()[k], Suits.values()[i]));
      }
    }
    return cards;
  }

  @Override
  public void startGame(List<Pocker> deck, int numCascadePiles, int numOpenPiles, boolean shuffle) {
    Set<Pocker> set = new HashSet<Pocker>(deck);
    openpile = new ArrayList<List<Pocker>>();
    cascadepile = new ArrayList<List<Pocker>>();
    foundationpile = new ArrayList<List<Pocker>>();

    if ((deck.size() != 52)) {
      throw new IllegalArgumentException("the deck is not completed");
    }
    if (numCascadePiles < 4 || numCascadePiles > 52) {
      throw new IllegalArgumentException("invalid number of CascadePiles");
    }
    if (numOpenPiles < 1 || numOpenPiles > 52) {
      throw new IllegalArgumentException("invalid number of OpenPiles");
    }
    if (set.size() < deck.size()) {
      throw new IllegalArgumentException("the deck contains duplication");
    }
    if (shuffle) {
      Collections.shuffle(deck);
    }
    for (int i = 0; i < 4; i++) {
      List<Pocker> emptyPile = new ArrayList<Pocker>();
      foundationpile.add(emptyPile);
    }
    for (int j = 0; j < numCascadePiles; j++) {
      List<Pocker> emptyPile = new ArrayList<Pocker>();
      cascadepile.add(emptyPile);
    }
    for (int x = 0; x < deck.size(); x++) {
      cascadepile.get(x % numCascadePiles).add(deck.get(x));
    }
    for (int k = 0; k < numOpenPiles; k++) {
      List<Pocker> emptyPile = new ArrayList<Pocker>();
      openpile.add(emptyPile);
    }
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex,
                   PileType destination, int destPileNumber) {
    if (this.isGameOver()) {
      throw new IllegalArgumentException("game over");
    }
    if (pileNumber < 0 || destPileNumber < 0) {
      throw new IllegalArgumentException("the pile number cannot be negative");
    }
    if (openpile == null || cascadepile == null || foundationpile == null) {
      throw new IllegalArgumentException("piles were not generated");
    }

    /*
    dealing the card from cascadepile
     */
    if (source.equals(PileType.OPEN)) {
      if (pileNumber > openpile.size() - 1) {
        throw new IllegalArgumentException("the pile does not exist");
      }
      Pocker card = this.lookupCard(source, pileNumber, cardIndex);

      this.updatePile(card, destination, destPileNumber);
      openpile.get(pileNumber).remove(card);
    }

    /*
    dealing the card from openpile
     */
    if (source.equals(PileType.CASCADE)) {
      if (pileNumber > cascadepile.size() - 1) {
        throw new IllegalArgumentException("the pile does not exist");
      }
      Pocker card = this.lookupCard(source, pileNumber, cardIndex);

      this.updatePile(card, destination, destPileNumber);
      cascadepile.get(pileNumber).remove(card);
    }

    /*
    dealing the card from foundationpilec
     */
    if (source.equals(PileType.FOUNDATION)) {
      if (pileNumber > foundationpile.size() - 1) {
        throw new IllegalArgumentException("the pile does not exist");
      }
      Pocker card = this.lookupCard(source, pileNumber, cardIndex);

      this.updatePile(card, destination, destPileNumber);
      foundationpile.get(pileNumber).remove(card);
    }
  }

  /**
   * to get the card in a given position.
   *
   * @param source     the piletype of the source pile
   * @param pileNumber the pile number of the source card
   * @param cardIndex  the index number of the source card in the give pile
   */
  private Pocker lookupCard(PileType source, int pileNumber, int cardIndex) {
    Pocker card = null;

    switch (source) {

      case OPEN:
        if (!openpile.isEmpty() && openpile.get(pileNumber).size() - 1 == cardIndex) {
          card = openpile.get(pileNumber).get(cardIndex);
        } else {
          throw new IllegalArgumentException("the card is not accessible ");
        }
        break;

      case FOUNDATION:
        if (!foundationpile.isEmpty() && foundationpile.get(pileNumber).size() - 1 == cardIndex) {
          card = foundationpile.get(pileNumber).get(cardIndex);
        } else {
          throw new IllegalArgumentException("the card is not accessible ");
        }
        break;

      case CASCADE:
        if (!cascadepile.isEmpty() && cascadepile.get(pileNumber).size() - 1 == cardIndex) {
          card = cascadepile.get(pileNumber).get(cardIndex);
        } else {
          throw new IllegalArgumentException("the card is not accessible ");
        }
        break;

      default:
        break;
    }
    return card;
  }

  /**
   * to update the piles' states by dealing with the moving card.
   *
   * @param card           the card which is being moved
   * @param destination    the piletye that the card is moved to
   * @param destPileNumber the pilenumber of the destination pile
   */

  private void updatePile(Pocker card, PileType destination, int destPileNumber) {
    switch (destination) {

      case OPEN:
        List<Pocker> destPile1;
        try {
          destPile1 = openpile.get(destPileNumber);
        } catch (IndexOutOfBoundsException e) {
          throw new IllegalArgumentException("no such pile exist");
        }
        if (!destPile1.isEmpty()) {
          throw new IllegalArgumentException("the open pile is full ");
        } else {
          destPile1.add(card);
        }
        break;

      case FOUNDATION:
        List<Pocker> desPile;
        try {
          desPile = foundationpile.get(destPileNumber);
        } catch (IndexOutOfBoundsException e) {
          throw new IllegalArgumentException("no such pile exist");
        }
        if (!desPile.isEmpty() && card.getFace().equals(Faces.ACE)) {
          throw new IllegalArgumentException("you can not put an ACE here");
        }
        if (desPile.isEmpty() && !card.getFace().equals(Faces.ACE)) {
          throw new IllegalArgumentException("only an ACE can be put here");
        }
        if (desPile.isEmpty() && card.getFace().equals(Faces.ACE)) {
          desPile.add(card);
        }
        if (!desPile.isEmpty() && !card.getFace().equals(Faces.ACE)) {
          if (this.lookupCard(destination, destPileNumber, desPile.size() - 1)
                  .getSuit().equals(card.getSuit())
                  &&
                  this.lookupCard(destination, destPileNumber, desPile.size() - 1)
                          .getFace().ordinal() + 1 == card.getFace().ordinal()) {
            desPile.add(card);
          } else {
            throw new IllegalArgumentException("the card cannot be placed here");
          }
        }
        break;

      case CASCADE:
        List<Pocker> destPile;
        try {
          destPile = cascadepile.get(destPileNumber);
        } catch (IndexOutOfBoundsException e) {
          throw new IllegalArgumentException("no such pile exist");
        }
        if (cascadepile.get(destPileNumber).size() == 0) {
          destPile.add(card);
        } else if ((card.getSuit().ordinal() % 2) ==
                ((this.lookupCard(destination, destPileNumber, destPile.size() - 1)
                        .getSuit().ordinal()) % 2)
                ||
                (!destPile.isEmpty()
                        &&
                        (card.getFace().ordinal()
                                != this.lookupCard(destination, destPileNumber,
                                destPile.size() - 1).getFace().ordinal() - 1))) {

          throw new IllegalArgumentException("You can't put a card over here!");
        } else {
          destPile.add(card);
        }
        break;

      default:
        break;
    }
  }

  @Override
  public boolean isGameOver() {
    return foundationpile.get(0).size() == 13 &&
            foundationpile.get(1).size() == 13 &&
            foundationpile.get(2).size() == 13 &&
            foundationpile.get(3).size() == 13;

  }

  @Override
  public String getGameState() {
    if (foundationpile == null) {
      return "";
    }
    String state = "";
    state = state +
            this.getEachPileState(PileType.FOUNDATION) +
            this.getEachPileState(PileType.OPEN) +
            this.getEachPileState(PileType.CASCADE);

    if (!state.equals("")) {
      state = state.substring(0, state.length() - 1);
    }

    return state;
  }

  /**
   * to get the state of each piletype.
   *
   * @param piletype the piletype that we want to check
   */
  private String getEachPileState(PileType piletype) {
    String state = "";

    switch (piletype) {
      case FOUNDATION:
        for (int x = 0; x < foundationpile.size(); x++) {
          state = state + "F" + (x + 1) + ":";
          for (int y = 0; y < foundationpile.get(x).size(); y++) {
            state = state + " " + foundationpile.get(x).get(y).toString();
            if (foundationpile.get(x).size() - 1 == y) {
              state = state + "\n";
            } else {
              state = state + ",";
            }
          }
          if (foundationpile.get(x).isEmpty()) {
            state = state + "\n";
          }
        }
        break;

      case OPEN:
        for (int x = 0; x < openpile.size(); x++) {
          state = state + "O" + (x + 1) + ":";
          if (openpile.get(x).isEmpty()) {
            state = state + "\n";
          } else {
            state = state + " " + openpile.get(x).get(0).toString() + "\n";
          }
        }
        break;

      case CASCADE:
        for (int x = 0; x < cascadepile.size(); x++) {
          state = state + "C" + (x + 1) + ":";
          for (int y = 0; y < cascadepile.get(x).size(); y++) {
            state = state + " " + (cascadepile.get(x).get(y).toString());
            if (cascadepile.get(x).size() - 1 != y) {
              state = state + ",";
            } else {
              state = state + "\n";
            }
          }
          if (cascadepile.get(x).isEmpty()) {
            state = state + "\n";
          }
        }
        break;

      default:
        break;
    }
    return state;
  }
}


