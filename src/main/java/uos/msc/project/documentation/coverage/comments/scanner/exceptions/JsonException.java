package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

/**
 * The {@code JsonException} class represents an exception,
 * that indicates a json exception.
 * It is a subclass of {@link CustomException}
 * @see CustomException
 * */
public class JsonException extends CustomException {

  /**
   * Constructs a new {@code JsonException} object with the specified description.
   *
   * @param message A description explaining the cause of the json exception.
   */
  public JsonException(final String message) {
    super(message);
  }

}
