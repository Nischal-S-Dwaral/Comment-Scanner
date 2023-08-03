package uos.msc.project.documentation.coverage.comments.scanner.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.JsonException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestUtils {

  private RequestUtils() { }

  public static String toInputStreamToJson(InputStream inputStream, Charset charset) {
    try {
      return IOUtils.toString(inputStream, charset);
    } catch (IOException e) {
      throw new JsonException(e.getMessage());
    }
  }

  public static <T> T getRequestData(HttpServletRequest request, Class<T> t) {
    try {
      String json = toInputStreamToJson(request.getInputStream(), StandardCharsets.UTF_8);
      return JsonUtils.toObject(json, t);
    } catch (IOException e) {
      log.error("error in parsing the request: ", e);
      throw new BadRequest("Error in parsing the error " + request);
    }
  }

  public static Map<String, String> getHeaders(HttpServletRequest httpServletRequest) {
    Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
    Map<String, String> headers = new HashMap<>();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String name = headerNames.nextElement();
        headers.put(name, httpServletRequest.getHeader(name));
      }
    }

    return headers;
  }

  public static Map<String, String> getQueryParams(HttpServletRequest httpServletRequest) {
    Map<String, String> queryParams = new HashMap<>();
    if (null == httpServletRequest.getQueryString() ||
            httpServletRequest.getQueryString().isEmpty()) {
      return queryParams;
    }
    for (String tuple : httpServletRequest.getQueryString().split("&")) {
      String[] param = tuple.split("=");
      queryParams.put(param[0].trim(), param[1].trim());
    }

    return queryParams;
  }
}
