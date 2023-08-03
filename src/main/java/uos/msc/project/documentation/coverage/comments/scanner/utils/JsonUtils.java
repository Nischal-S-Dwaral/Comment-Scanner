package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.JsonException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JsonUtils is basically a singleton ObjectMapper that wraps all JSON Error into
 * {@code JsonException}. These are a bunch of helper method for parsing collections.
 */
public class JsonUtils {

  private static final ObjectMapper mObjectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

  private JsonUtils() { /**/ }

  public static String toString(Object object) {
    try {
      return mObjectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new JsonException(e.getMessage());
    }
  }

  public static <T> T toObject(String value, Class<T> clazz) {
    try {
      return mObjectMapper.readValue(value, clazz);
    } catch (IOException e) {
      throw new JsonException(e.getMessage());
    }
  }

  public static <T> T toObject(Map<String, Object> value, Class<T> clazz) {
    return mObjectMapper.convertValue(value, clazz);
  }

  public static <T extends R, R> List<R> toList(String json, Class<T> clazz) {
    try {
      CollectionType type = mObjectMapper.getTypeFactory()
          .constructCollectionType(List.class, clazz);
      return mObjectMapper.readValue(json, type);
    } catch (IOException e) {
      throw new JsonException(e.getMessage());
    }
  }

  public static <T extends R, R> Set<R> toSet(String json, Class<T> clazz) {
    try {
      CollectionType type = mObjectMapper.getTypeFactory()
          .constructCollectionType(Set.class, clazz);
      return mObjectMapper.readValue(json, type);
    } catch (IOException e) {
      throw new JsonException(e.getMessage());
    }
  }

  public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
    try {
      if (null == json || json.isEmpty()) {
        return new HashMap<>();
      }
      MapType type = mObjectMapper.getTypeFactory()
          .constructMapType(Map.class, keyClass, valueClass);
      return mObjectMapper.readValue(json, type);
    } catch (IOException e) {
      throw new JsonException(e.getMessage());
    }
  }
}
