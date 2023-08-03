package uos.msc.project.documentation.coverage.comments.scanner.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * The {@code RestApiController} class contains the endpoints implementation for the application.
 */
@RestController
public class RestApiController {

    /**
     * Handles the POST request for a service code and use case.
     *
     * @param serviceCode the service code extracted from the path variable.
     * @param useCase the use case extracted from the path variable.
     * @param httpServletRequest the HTTP servlet request object.
     * @param <T> the type parameter extending RestApiResponse.
     * @return the response of type T, which extends RestApiResponse. Which is required by the frontend.
     * @throws IOException if an I/O exception occurs during the execution.
     */
    @PostMapping("/api/{serviceCode}/{useCase}")
    @CrossOrigin("*")
    public <T extends RestApiResponse> T post(
            @PathVariable("serviceCode") final String serviceCode,
            @PathVariable("useCase") final String useCase,
            final HttpServletRequest httpServletRequest) {
        IUseCaseImplementation serviceAdaptor = UseCasesAdaptorFactory
                .getAdaptor(Objects.requireNonNull(ServiceEnum.findByServiceName(serviceCode)),
                        UseCasesEnums.getEnumByString(useCase));
        return (T) serviceAdaptor.execute(httpServletRequest);
    }

    /**
     * Handles the GET request for a service code and use case.
     *
     * @param serviceCode the service code extracted from the path variable.
     * @param useCase the use case extracted from the path variable.
     * @param httpServletRequest the HTTP servlet request object.
     * @param <T> the type parameter extending RestApiResponse.
     * @return the response of type T, which extends RestApiResponse. Which is required by the frontend.
     * @throws IOException if an I/O exception occurs during the execution.
     */
    @GetMapping("/api/{serviceCode}/{useCase}")
    @CrossOrigin("*")
    public <T extends RestApiResponse> T get(
            @PathVariable("serviceCode") final String serviceCode,
            @PathVariable("useCase") final String useCase,
            final HttpServletRequest httpServletRequest) throws IOException {
        IUseCaseImplementation serviceAdaptor = UseCasesAdaptorFactory
                .getAdaptor(Objects.requireNonNull(ServiceEnum.findByServiceName(serviceCode)),
                        UseCasesEnums.getEnumByString(useCase));
        return (T) serviceAdaptor.execute(httpServletRequest);
    }
}
