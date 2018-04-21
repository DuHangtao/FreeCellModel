package cs3500.hw04;

import cs3500.hw02.FreecellModel;
import cs3500.hw02.PileType;
import cs3500.hw02.Pocker;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is the extension of the freecell model, but it offers player to move multiple cards at
 * one time.
 */
public class FreecellMultipleMoveModel extends FreecellModel {

  /**
   * the default constructor of the FreecellMultipleMoveModel.
   */
  public FreecellMultipleMoveModel() {

    /**
     * this is the default constructor of the FreecellMultipleMoveModel which takes noting.
     */
  }

  /**
   * a method to get the maximum number of cards which are allowed to move.
   */
  private double maxNumberCards() {
    double maxNumCards;

    /**
     * to calculate the number of the free cascade piles.
     */
    double numberFreeCascade = 0;
    for (int i = 0; i < cascadepile.size(); i++) {
      if (cascadepile.get(i).size() == 0) {
        numberFreeCascade++;
      }
    }

    /**
     * to calculate the number of the free open piles.
     */
    double numberFreeOpen = 0;
    for (int j = 0; j < openpile.size(); j++) {
      if (openpile.get(j).size() == 0) {
        numberFreeOpen++;
      }
    }

    /**
     * the formula (N+1)*2K.
     * N stands for the number of free Open piles.
     * K stands for the number of free Cascade piles.
     */
    maxNumCards = ((numberFreeOpen + 1) * Math.pow(2, numberFreeCascade));
    return maxNumCards;
  }

  /**
   * Override the move method form freecell model to make sure it allows player to move multiple
   * cards if the moving is valid.
   *
   * <p>Multiple cards can only be moved between cascade pile and cascade pile, if the destination
   * pile are open or foundation pile, the move is invalid.
   *
   * <p>Also multiple cards only can be move if it satisfies three conditions. 1. multiple cards
   * should form a valid build. 2. these cards should form a build with the last card in the
   * destination cascade pile. 3. the size of list of cards which is going to move should be less
   * than maximum number of card which are allowed to move by the formula (N+1)*2K.
   *
   * @param source         the pile type of the source pile.
   * @param pileNumber     the pile number of the source pile, starting at 0.
   * @param cardIndex      the source pile card index, starting at 0.
   * @param destination    the pile type of the destination pile.
   * @param destPileNumber the pile number of the destination pile, starting at 0.
   */
  @Override
  public void move(PileType source, int pileNumber, int cardIndex,
                   PileType destination, int destPileNumber) {
    List<Pocker> multipleCards;

    /**
     * check the condition when source pile is either equal to open or foundation
     * to pass all the parameters into the move method in the free cell model.
     */
    if (source == PileType.OPEN || source == PileType.FOUNDATION) {
      super.move(source, pileNumber, cardIndex, destination, destPileNumber);
      return;
    }

    if (pileNumber < 0 || destPileNumber < 0) {
      throw new IllegalArgumentException("the pile number cannot be negative");
    }

    /**
     * checking if the given cascade and destination pile is valid.
     * if they are not valid, the function will throw exception and terminate.
     */
    isValidCascadeSource(pileNumber, cardIndex);
    isValidDest(destination, destPileNumber);

    /**
     * the get the sublist form the cascade pile that user want to move,
     * and it either contain one card or more cards.
     */
    multipleCards = getMultipleCards(source, pileNumber, cardIndex);

    /**
     * check if the number of cards is smaller that the cards able to move.
     */
    if (multipleCards.size() > maxNumberCards()) {
      throw new IllegalArgumentException("too many cards cannot be moved");
    }

    /**
     * when the size of the sublist is equal to 1 which means that we only have
     * one card to move, then pass it into the move function in free cell model.
     */
    if (multipleCards.size() == 1) {
      super.move(source, pileNumber, cardIndex, destination, destPileNumber);
    }

    /**
     * only in this case, the sublist contain more than one card, so this is a multi move.
     * a multi move is valid only when it satisfy two conditions: 1. the sublist is form a
     * valid build. 2. the top card form the sublist should exactly 1 smaller in value than the
     * bottom card from the destination pile, and they must have the different colors.
     *
     * <p> also, the multi move only happens between the cascade pile.
     */
    else if (isAbleToAdd(source, destination, destPileNumber, multipleCards)
            && isFormValidBuild(multipleCards)) {
      cascadepile.get(destPileNumber).addAll(multipleCards);
      cascadepile.get(pileNumber).removeAll(multipleCards);
    } else {
      throw new IllegalArgumentException("Invalid multi-move!");
    }
  }

  /**
   * a helper method to check if the destination pile is valid in all cases.
   *
   * @param destination    the pile type of the destination pile.
   * @param destPileNumber the pile number of the destination pile.
   */
  private void isValidDest(PileType destination, int destPileNumber) {

    switch (destination) {
      case CASCADE:
        if (cascadepile.size() <= destPileNumber) {
          throw new IllegalArgumentException("invalid cascade destination pile");
        }
        break;

      case OPEN:
        if (openpile.size() <= destPileNumber) {
          throw new IllegalArgumentException("invalid open destination pile");
        }
        break;

      case FOUNDATION:
        if (foundationpile.size() <= destPileNumber) {
          throw new IllegalArgumentException("invalid foundation destination pile");
        }
        break;

      default:
        break;
    }
  }

  /**
   * a helper method checking if a cascade pile is valid.
   *
   * @param pileNumber the pile number of a cascade pile.
   * @param cardIndex  the card index of the cascade pile.
   */
  private void isValidCascadeSource(int pileNumber, int cardIndex) {
    if (cascadepile.size() <= pileNumber
            || cascadepile.get(pileNumber).size() <= cardIndex
            || cascadepile.get(pileNumber).size() == 0) {
      throw new IllegalArgumentException("invalid source pile");
    }
  }

  /**
   * a helper method to check if the cards can be added to the destination pile when cards is moving
   * form cascade to cascade .
   *
   * @param source         the pile type of the source pile.
   * @param destination    the pile type of the destination pile.
   * @param destPileNumber the pile number of the destination pile that we want add cards to.
   * @param multipleCards  the list of cards itself.
   */
  private boolean isAbleToAdd(PileType source, PileType destination,
                              int destPileNumber, List<Pocker> multipleCards) {

    if (source == PileType.CASCADE && destination == PileType.CASCADE) {
      if (cascadepile.get(destPileNumber).size() == 0) {
        return true;
      } else {
        int sizeOfDestPile = cascadepile.get(destPileNumber).size() - 1;
        Pocker bottomDestPileCard = cascadepile.get(destPileNumber).get(sizeOfDestPile);

        return ((bottomDestPileCard.getFace().ordinal()
                == multipleCards.get(0).getFace().ordinal() + 1)
                &&
                (multipleCards.get(0).getSuit().ordinal() % 2)
                        != (bottomDestPileCard.getSuit().ordinal() % 2));
      }
    } else {
      return false;
    }
  }

  /**
   * checking if a list of card is form a valid build.
   *
   * @param multipleCards a list of cards waiting for moving.
   */
  private boolean isFormValidBuild(List<Pocker> multipleCards) {
    boolean result = true;
    for (int i = 0; i < multipleCards.size() - 1; i++) {
      if ((multipleCards.get(i).getFace().ordinal()
              == multipleCards.get(i + 1).getFace().ordinal() + 1)
              &&
              (multipleCards.get(i).getSuit().ordinal() % 2)
                      !=
                      (multipleCards.get(i + 1).getSuit().ordinal()) % 2) {
        result = true;
      } else {
        result = false;
      }
    }
    return result;
  }

  /**
   * a helper to get a list of cards from a source pile.
   *
   * @param source     the pile type of the source pile.
   * @param pileNumber the pile number of the source pile.
   * @param cardIndex  the card index in source pile.
   */
  private List<Pocker> getMultipleCards(PileType source, int pileNumber, int cardIndex) {
    List<Pocker> sublist = new ArrayList<Pocker>();
    switch (source) {
      case FOUNDATION:
        int pileSize1 = foundationpile.get(pileNumber).size();
        sublist = foundationpile.get(pileNumber).subList(cardIndex, pileSize1);
        break;

      case OPEN:
        int pileSize2 = openpile.get(pileNumber).size();
        sublist = openpile.get(pileNumber).subList(cardIndex, pileSize2);
        break;

      case CASCADE:
        int pileSize3 = cascadepile.get(pileNumber).size();
        sublist = cascadepile.get(pileNumber).subList(cardIndex, pileSize3);
        break;

      default:
        break;
    }
    return sublist;
  }
}
