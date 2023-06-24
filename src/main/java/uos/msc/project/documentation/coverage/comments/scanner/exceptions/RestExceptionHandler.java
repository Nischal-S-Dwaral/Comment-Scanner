package uos.msc.project.documentation.coverage.comments.scanner.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import uos.msc.project.documentation.coverage.comments.scanner.model.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.*;

/**
 * Custom exception handler Controller Advice which handles all type of exception
 * and returns custom error messages.
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Logger instance which is used to handle exceptions log for RestExceptionHandler class.
     * RestExceptionHandler that handles exceptions in a RESTful API and provides logging functionality.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * Represents the error type for client errors.
     */
    private static final String CLIENT_ERROR = "CLIENT_ERROR";

    /**
     * Represents the error type for sever errors.
     */
    private static final String SERVER_ERROR = "SERVER_ERROR";

    /**
     * Represents the error type for general errors whose cause is not known.
     */
    private static final String VAGUE_ERROR_MESSAGE = "Sorry, something failed.";

    /**
     * Handles the Unauthorized exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The Unauthorized exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(Unauthorized.class)
    @ResponseBody
    public ErrorResponse handleUnauthorized(final HttpServletRequest request, final Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(UNAUTHORIZED.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    /**
     * Handles the Forbidden exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception  The Forbidden exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(Forbidden.class)
    @ResponseBody
    public ErrorResponse handleForbidden(final HttpServletRequest request, final Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(FORBIDDEN.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    /**
     * Handles the MultipartException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param httpServletRequest The HttpServletRequest object representing the current request.
     * @param exception  The MultipartException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ErrorResponse handleMultipartException(final HttpServletRequest httpServletRequest, final MultipartException exception) {
        LOG.error("Multipart resolution failed with message : {}", exception.getMessage());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    /**
     * Handles the BadRequest exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The BadRequest exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequest.class)
    @ResponseBody
    public ErrorResponse handleBadRequest(final HttpServletRequest request, final BadRequest exception) {
        LOG.error("Bad request error ", exception);
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    /**
     * Handles the HttpClientErrorException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param httpServletRequest The HttpServletRequest object representing the current request.
     * @param e The HttpClientErrorException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ErrorResponse handleHttpClientErrorException(final HttpServletRequest httpServletRequest,
                                                        final HttpClientErrorException e) {
        LOG.error("Downstream call failed with status: {} and response: {} with the error message as: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    /**
     * Handles the DateTimeParseException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param httpServletRequest The HttpServletRequest object representing the current request.
     * @param e The DateTimeParseException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseBody
    public ErrorResponse handleInvalidDateTime(final HttpServletRequest httpServletRequest,
                                               final DateTimeParseException e) {
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(e.getParsedString())
                .build();
    }

    /**
     * Handles the HttpRequestMethodNotSupportedException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The HttpRequestMethodNotSupportedException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ErrorResponse handleMethodNotAllowed(final HttpServletRequest request, final Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(METHOD_NOT_ALLOWED.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    /**
     * Handles the HttpMediaTypeNotSupportedException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The HttpMediaTypeNotSupportedException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ErrorResponse handleMediaTypeNotSupported(final HttpServletRequest request,
                                                     final Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(UNSUPPORTED_MEDIA_TYPE.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    /**
     * Handles the MethodArgumentNotValidException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The MethodArgumentNotValidException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValid(final HttpServletRequest request,
                                                      final MethodArgumentNotValidException exception) {

        String description = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> format("{0} {1}", error.getField(), error.getDefaultMessage())).collect(
                        Collectors.joining(", "));
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(description)
                .build();
    }

    /**
     * Handles the HttpMessageNotReadableException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorResponse handleMessageNotReadable(final HttpServletRequest request) {
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription("Http message was not readable")
                .build();
    }

    /**
     * Handles the MethodArgumentTypeMismatchException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The MethodArgumentTypeMismatchException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ErrorResponse handleMethodTypeNotValid(final HttpServletRequest request,
                                                  final MethodArgumentTypeMismatchException exception) {

        String description = String.format("Parameter value '%s' is not valid for request parameter '%s'",
                exception.getValue(), exception.getName());
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(description)
                .build();
    }

    /**
     * Handles the HttpServerErrorException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param httpServletRequest The HttpServletRequest object representing the current request.
     * @param e The HttpServerErrorException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseBody
    public ErrorResponse handleHttpServerErrorException(final HttpServletRequest httpServletRequest,
                                                        final HttpServerErrorException e) {
        LOG.error("Request failed with status: {} and response: {}, with error message: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    /**
     * Handles the RestClientException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param httpServletRequest The HttpServletRequest object representing the current request.
     * @param e The RestClientException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RestClientException.class)
    @ResponseBody
    public ErrorResponse handleRestClientException(final HttpServletRequest httpServletRequest, final RestClientException e) {
        LOG.error("RestClient call failed with message : {}", e.getMessage());
        return ErrorResponse.Builder.anError().withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(e.getMessage())
                .build();
    }

    /**
     * Handles the UnsatisfiedServletRequestParameterException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param exception The UnsatisfiedServletRequestParameterException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    @ResponseBody
    public ErrorResponse handleUnsatisfiedParameter(final HttpServletRequest request,
                                                    final UnsatisfiedServletRequestParameterException exception) {

        String unsatisfiedConditions = String.join(",", exception.getParamConditions());
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(
                        format("Parameter conditions not met for request: {0}", unsatisfiedConditions))
                .build();
    }

    /**
     * Handles the Throwable exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param ex The Throwable exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorResponse catchAllHandler(final HttpServletRequest request, final Throwable ex) {
        LOG.error("Unexpected error handled", ex);
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    /**
     * Handles the JsonException exception by returning an ErrorResponse,
     * with the appropriate status, URL, message, and description.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @param ex The JsonException exception that was thrown.
     * @return An ErrorResponse object containing the error details.
     */
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JsonException.class)
    @ResponseBody
    public ErrorResponse handleJsonException(final HttpServletRequest request, final Throwable ex) {
        LOG.error("Unexpected error handled", ex);
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }
}
