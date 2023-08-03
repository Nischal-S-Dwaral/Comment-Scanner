package uos.msc.project.documentation.coverage.comments.scanner.service;

import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This interface represents a generic use case implementation.
 * It defines methods for preprocessing, processing,
 * and post-processing for a use case.
 *
 * @param <R> The type of the pre-processed data.
 * @param <S> The type of the processed data.
 * @param <T> The type of the response returned by the post-processing.
 */
public interface IUseCaseImplementation<R, S, T extends RestApiResponse> {

  /**
   * Pre-processes the request and returns the pre-processed data.
   *
   * @param request The HTTP request to be pre-processed.
   * @return The pre-processed data.
   */
  R preProcess(HttpServletRequest request);

  /**
   * Processes the pre-processed data and returns the processed data.
   *
   * @param r The pre-processed data to be processed.
   * @return The processed data.
   */
  S process(R r);

  /**
   * Post-processes the processed data and returns the response.
   *
   * @param s The processed data to be post-processed.
   * @return The response generated after post-processing.
   */
  T postProcess(S s);

  /**
   * Executes the use case by invoking the pre-processing, processing,
   * and post-processing steps.
   * It returns the response generated after post-processing.
   *
   * @param request The HTTP request to execute the use case.
   * @return The response generated after post-processing.
   */
  default T execute(HttpServletRequest request) {
    return postProcess(process(preProcess(request)));
  }
}
