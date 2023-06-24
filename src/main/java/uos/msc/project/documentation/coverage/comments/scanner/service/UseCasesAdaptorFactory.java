package uos.msc.project.documentation.coverage.comments.scanner.service;

import lombok.extern.slf4j.Slf4j;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.Forbidden;

import java.util.HashMap;
import java.util.Map;

/**
 * The UseCasesAdaptorFactory class is responsible for managing and providing use case adaptors
 * for handling specific use cases.
 */
@Slf4j
public final class UseCasesAdaptorFactory {

  private UseCasesAdaptorFactory() { }

  /**
   * A map that stores the registered use case implementations.
   */
  private static final Map<String, IUseCaseImplementation> useCaseImplementationMap = new HashMap<>();

  /**
   * Generates the map key for the specified service code and use case.
   *
   * @param serviceCode the service code.
   * @param useCase     the use case.
   * @return the generated map key.
   */
  private static String getMapKey(final String serviceCode, final String useCase) {
    return String.format("%s:%s", serviceCode.toLowerCase(), useCase.toLowerCase());
  }

  /**
   * Retrieves the specific adaptor key initialised for the specified service code and use case.
   *
   * @param serviceCode the service code.
   * @param useCase     the use case.
   * @return the use case implementation adaptor.
   * @throws BadRequest if the provided use case and service code are not supported by the system.
   */
  public static IUseCaseImplementation getAdaptor(final ServiceEnum serviceCode, final UseCasesEnums useCase) {
    String key = getMapKey(serviceCode.getName(), useCase.getUseCaseName());
    if (useCaseImplementationMap.containsKey(key)) {
      return useCaseImplementationMap.get(key);
    }
    throw new BadRequest(
            "Provided useCase and serviceCode is not supported by the system.");
  }

  /**
   * Registers an adaptor implementation class for handling a particular use case.
   * The use case code should be unique.
   *
   * @param serviceCode           the service code.
   * @param useCase               the use case.
   * @param useCaseImplementation the use case implementation class.
   * @throws Forbidden if the use case is already implemented.
   */
  public static void registerAdaptor(final ServiceEnum serviceCode, final UseCasesEnums useCase,
                                     final IUseCaseImplementation useCaseImplementation) {
    String key = getMapKey(serviceCode.getName(), useCase.getUseCaseName());
    log.debug("Registered key:{} ,{} ", key, useCaseImplementation.getClass().getName());

    if (useCaseImplementationMap.containsKey(key)) {
      throw new Forbidden(" use case already implemented...");
    }
    useCaseImplementationMap.put(key, useCaseImplementation);
  }
}
