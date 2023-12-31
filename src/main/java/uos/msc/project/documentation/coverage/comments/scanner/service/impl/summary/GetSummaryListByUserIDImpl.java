package uos.msc.project.documentation.coverage.comments.scanner.service.impl.summary;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListProcess;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListResponse;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListRequest;
import uos.msc.project.documentation.coverage.comments.scanner.repository.SummaryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.ProjectSummaryUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for the getting list of summaries of the user
 */
@Service
@Slf4j
public class GetSummaryListByUserIDImpl implements IUseCaseImplementation<
        GetSummaryListRequest, GetSummaryListProcess, GetSummaryListResponse> {

    /**
     * Data/Repository layer implementation for the summary collection
     */
    private final SummaryRepository summaryRepository;

    public GetSummaryListByUserIDImpl(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SUMMARY, UseCasesEnums.GET_BY_USER_ID, this);
    }

    @Override
    public GetSummaryListRequest preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String userId = queryParams.get("userId");
        String qualityGate = queryParams.get("qualityGate");
        if (!CommonUtils.checkIfObjectIsNotNull(userId)) {
            throw new BadRequest("User ID is required");
        }
        if (!CommonUtils.checkIfObjectIsNotNull(qualityGate)) {
            throw new BadRequest("Quality Gate is required");
        }
        return new GetSummaryListRequest(userId, qualityGate);
    }

    @Override
    public GetSummaryListProcess process(GetSummaryListRequest summaryListRequest) {
        List<SummaryEntity> summaryEntityList = summaryRepository.getByUserId(summaryListRequest.getId());

        return new GetSummaryListProcess(summaryEntityList,
                Integer.parseInt(summaryListRequest.getQualityGate()));
    }

    @Override
    public GetSummaryListResponse postProcess(GetSummaryListProcess process) {
        Map<String, SummaryEntity> latestEntriesMap = new HashMap<>();

        for (SummaryEntity entity : process.getSummaryEntities()) {
            String repository = entity.getRepository();

            if (!latestEntriesMap.containsKey(repository) ||
                    entity.getTimestamp().compareTo(latestEntriesMap.get(repository).getTimestamp()) > 0) {
                latestEntriesMap.put(repository, entity);
            }
        }

        List<SummaryEntity> filteredSummaryList = latestEntriesMap.values().stream().toList();

        return new GetSummaryListResponse(ProjectSummaryUtils.convertSummaryEntities(
                filteredSummaryList, process.getQualityGate()
        ));
    }
}
