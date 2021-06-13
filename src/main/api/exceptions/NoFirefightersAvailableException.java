package main.api.exceptions;

public class NoFirefightersAvailableException extends Exception {
  public NoFirefightersAvailableException() {
    super("There are no firefighters available!");
  }
}
