package uos.msc.project.documentation.coverage.comments.scanner.service.impl.qualitygate;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate.UpdateQualityGateRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate.UpdateQualityGateResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.QualityGateRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.Map;

@Service
public class UpdateQualityGateImpl implements IUseCaseImplementation<
        UpdateQualityGateRequest, String, UpdateQualityGateResponse> {

    private final QualityGateRepository qualityGateRepository;

    public UpdateQualityGateImpl(QualityGateRepository qualityGateRepository) {
        this.qualityGateRepository = qualityGateRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.QUALITY_GATE, UseCasesEnums.UPDATE, this);
    }

    @Override
    public UpdateQualityGateRequest preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String userId = queryParams.get("userId");
        String qualityGate = queryParams.get("qualityGate");
        if (!CommonUtils.checkIfObjectIsNotNull(userId)) {
            throw new BadRequest("User ID is required");
        }
        if (!CommonUtils.checkIfObjectIsNotNull(qualityGate)) {
            throw new BadRequest("Quality Gate is required");
        }
        return new UpdateQualityGateRequest(userId, qualityGate);
    }

    @Override
    public String process(UpdateQualityGateRequest updateQualityGateRequest) {
        return qualityGateRepository.update(updateQualityGateRequest);
    }

    @Override
    public UpdateQualityGateResponse postProcess(String qualityGate) {
        return new UpdateQualityGateResponse(qualityGate);
    }
}
