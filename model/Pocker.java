package cs3500.hw02;

import java.util.Objects;

/**
 * the class Pocker contains two fields face and suit. face is a enum of 13 values of cards from
 * ACE("A") to KING("K"). suit is a enum of 4 suits of cards which include HEARTS("♥"),
 * DIAMONDS("♦"),SPADES("♠"), CLUBS("♣").
 */
public class Pocker {
  private Faces face;
  private Suits suit;

  public Pocker(Faces cardface, Suits cardsuit) {
    face = cardface;
    suit = cardsuit;
  }

  @Override
  public String toString() {
    return face.toString() + suit.toString();
  }

  public Faces getFace() {
    return this.face;
  }

  public Suits getSuit() {
    return this.suit;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof Pocker)) {
      return false;
    }

    Pocker that = (Pocker) other;
    return this.getFace() == that.getFace() && this.getSuit() == that.getSuit();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getFace(), this.getSuit());
  }
}
