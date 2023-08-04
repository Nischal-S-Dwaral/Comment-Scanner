package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

import java.io.Serial;
import java.util.function.Supplier;

/**
 * The {@code InternalServerError} class represents an exception,
 * that indicates an internal server error exception.
 * It is a subclass of {@link CustomException}
 * @see CustomException
 * */
public class InternalServerError extends CustomException implements Supplier {

    @Serial
    private static final long serialVersionUID = 788618025995649201L;

    /**
     * Constructs a new {@code InternalServerError} object with the specified description.
     *
     * @param description A description explaining the cause of the internal server exception.
     */
    public InternalServerError(final String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns {@code null} in this implementation.
     */
    @Override
    public Object get() {
        return null;
    }
}
