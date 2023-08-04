package uos.msc.project.documentation.coverage.comments.scanner.service.impl.qualitygate;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.QualityGateEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate.GetQualityGateResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.QualityGateRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.Map;

@Service
@Slf4j
public class GetQualityGateImpl implements IUseCaseImplementation<
        String, QualityGateEntity, GetQualityGateResponse> {

    private final QualityGateRepository qualityGateRepository;

    public GetQualityGateImpl(QualityGateRepository qualityGateRepository) {
        this.qualityGateRepository = qualityGateRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.QUALITY_GATE, UseCasesEnums.GET_BY_USER_ID, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String userId = queryParams.get("userId");
        if (!CommonUtils.checkIfObjectIsNotNull(userId)) {
            throw new BadRequest("User ID is required");
        }
        return userId;
    }

    @Override
    public QualityGateEntity process(String userId) {
        return qualityGateRepository.getQualityGateEntity(userId);
    }

    @Override
    public GetQualityGateResponse postProcess(QualityGateEntity qualityGateEntity) {
        return new GetQualityGateResponse(qualityGateEntity.getQualityGate());
    }
}
