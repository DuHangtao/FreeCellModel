package cs3500.hw02;

public enum Faces {
  ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"),
  TEN("10"), JACK("J"), QUEEN("Q"), KING("K");

  private String faces;

  Faces(String faces) {
    this.faces = faces;
  }

  @Override
  public String toString() {
    return this.faces;
  }
}


