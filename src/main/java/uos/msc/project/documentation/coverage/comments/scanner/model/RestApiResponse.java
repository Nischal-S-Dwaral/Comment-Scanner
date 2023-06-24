package uos.msc.project.documentation.coverage.comments.scanner.model;

import lombok.Data;

@Data
public class RestApiResponse {

  /**
   * If the response is successful then the returnCode is 0.
   * Useful for the front end while checking API responses
   */
  private String returnCode = "0";
}
