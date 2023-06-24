package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * CustomException is a runtime exception class that is used as a base class for creating custom exceptions.
 * It extends the RuntimeException class, allowing it to be thrown without requiring explicit exception handling.
 */
@Slf4j
public class CustomException extends RuntimeException {

  /**
   * The serial version UID for serialization and deserialization.
   */
  @Serial
  private static final long serialVersionUID = 8722516953922232439L;

  /**
   * Constructs a new CustomException with the specified error message.
   *
   * @param message the error message associated with the exception.
   */
  public CustomException(final String message) {
    super(message);
  }

  /**
   * Constructs a new CustomException with the specified error message and cause.
   *
   * @param message   the error message associated with the exception.
   * @param throwable the cause of the exception.
   */
  public CustomException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
