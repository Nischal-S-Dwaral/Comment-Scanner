package uos.msc.project.documentation.coverage.comments.scanner.enums;

/**
 * Enum representing different services.
 */
public enum ServiceEnum {

  /**
   * Represents the JAVA service.
   */
  JAVA("java"),
  /**
   * Represents the PROJECT service.
   */
  PROJECT("project"),
  /**
   * Represents the SUMMARY service.
   */
  SUMMARY("summary");

  /**
   * The name of the service.
   */
  private final String name;

  /**
   * Constructs a ServiceEnum object with the specified service name.
   *
   * @param name the name of the service.
   */
  ServiceEnum(final String name) {
    this.name = name;
  }

  /**
   * Returns the Service enum value corresponding to the given string.
   *
   * @param serviceName the string value to match.
   * @return the corresponding Service enum value, or null if not found.
   */
  public static ServiceEnum findByServiceName(final String serviceName) {

    for (ServiceEnum serviceEnum : values()) {
      if (serviceEnum.getName().equals(serviceName)) {
        return serviceEnum;
      }
    }
    return null;
  }

  /**
   * @return the name of the service.
   */
  public String getName() {
    return name;
  }

}
