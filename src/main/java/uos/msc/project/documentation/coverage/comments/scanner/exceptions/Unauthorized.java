package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

/**
 * The {@code Unauthorized} class represents an exception,
 * that indicates an unauthorized access.
 * It is a subclass of {@link CustomException}
 * @see CustomException
 * */
public class Unauthorized extends CustomException {

  /**
   * Constructs a new {@code Unauthorized} object with the specified description.
   *
   * @param description A description explaining the cause of the unauthorized access exception message.
   */
  public Unauthorized(final String description) {
    super(description);
  }

}
