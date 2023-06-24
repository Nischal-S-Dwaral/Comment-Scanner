package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

import java.io.Serial;
import java.util.function.Supplier;

/**
 * The {@code BadRequest} class represents an exception that indicates a bad request.
 * It is a subclass of {@link CustomException} and implements the {@link Supplier} interface.
 * This exception can be thrown when a request is malformed or contains invalid data
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * try {
 *     // Code that may throw a BadRequest exception
 * } catch (BadRequest ex) {
 *     System.err.println("Bad request: " + ex.getMessage());
 * }
 * }</pre>
 *
 * <p>The class provides a constructor to create a new instance of the {@code BadRequest} exception,
 * accepting a description as a parameter.</p>
 *
 * <p>The class also overrides the {@link Supplier#get()} method, which returns {@code null} in this case.</p>
 *
 * @see CustomException
 * @see Supplier
 */
public class BadRequest extends CustomException implements Supplier {

    @Serial
    private static final long serialVersionUID = 788618025995649201L;

    /**
     * Constructs a new {@code BadRequest} object with the specified description.
     *
     * @param description A description explaining the cause of the bad request.
     */
    public BadRequest(final String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code null} in this implementation.
     */
    @Override
    public Object get() {
        return null;
    }
}
