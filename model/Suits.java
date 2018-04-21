package cs3500.hw02;

public enum Suits {
  HEARTS("♥"),
  SPADES("♠"),
  DIAMONDS("♦"),
  CLUBS("♣");
  private String suits;

  Suits(String suits) {
    this.suits = suits;
  }

  @Override
  public String toString() {
    return this.suits;
  }
}


