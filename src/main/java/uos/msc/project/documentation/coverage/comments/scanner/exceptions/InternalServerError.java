package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

/**
 * The {@code InternalServerError} class represents an exception,
 * that indicates an internal server error exception.
 * It is a subclass of {@link CustomException}
 * @see CustomException
 * */
public class InternalServerError extends CustomException {

    /**
     * Constructs a new {@code InternalServerError} object with the specified description.
     *
     * @param description A description explaining the cause of the internal server exception.
     */
    public InternalServerError(final String description) {
        super(description);
    }
}
