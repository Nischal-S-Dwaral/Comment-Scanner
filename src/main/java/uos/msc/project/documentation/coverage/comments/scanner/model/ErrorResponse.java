package uos.msc.project.documentation.coverage.comments.scanner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uos.msc.project.documentation.coverage.comments.scanner.constants.Constants;

/**
 * ErrorResponse body for all exceptions.
 * Used in the ControllerAdvice Handler
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

  private int status;
  private String url;
  private String message;
  private String description;
  private String returnCode;

  /**
   * Builder is a static nested class that facilitates the construction of ErrorResponse objects.
   */
  public static class Builder {

    private int status;
    private String url;
    private String message;
    private String description;

    /**
     * Creates a new instance of the Builder.
     *
     * @return A new Builder instance.
     */
    public static Builder anError() {
      return new Builder();
    }

    /**
     * Sets the status of the ErrorResponse.
     *
     * @param status The status code of the error.
     * @return The Builder instance.
     */
    public Builder withStatus(final int status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the URL of the request associated with the ErrorResponse.
     *
     * @param url The URL of the request.
     * @return The Builder instance.
     */
    public Builder withUrl(final String url) {
      this.url = url;
      return this;
    }

    /**
     * Sets the message of the ErrorResponse.
     *
     * @param message The error message.
     * @return The Builder instance.
     */
    public Builder withMessage(final String message) {
      this.message = message;
      return this;
    }

    /**
     * Sets the description of the ErrorResponse.
     *
     * @param description The error description.
     * @return The Builder instance.
     */
    public Builder withDescription(final String description) {
      this.description = description;
      return this;
    }

    /**
     * Builds and returns an instance of the ErrorResponse using the provided values.
     *
     * @return An ErrorResponse object.
     */
    public ErrorResponse build() {
      return new ErrorResponse(status, url, message, description, Constants.RETURN_CODE_ERROR);
    }
  }

}
