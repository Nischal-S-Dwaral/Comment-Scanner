package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

/**
 * The {@code Forbidden} class represents an exception,
 * that indicates a forbidden access.
 * It is a subclass of {@link CustomException}
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * try {
 *     // Code that may throw a Forbidden exception
 * } catch (Forbidden ex) {
 *     System.err.println("Bad request: " + ex.getMessage());
 * }
 * }</pre>
 *
 * <p>The class provides a constructor to create a new instance of the {@code Forbidden} exception, accepting a description as a parameter.</p>
 *
 * @see CustomException
 * */
public class Forbidden extends CustomException {

    /**
     * Constructs a new {@code Forbidden} object with the specified description.
     *
     * @param description A description explaining the cause of the forbidden access.
     */
    public Forbidden(final String description) {
        super(description);
    }
}
