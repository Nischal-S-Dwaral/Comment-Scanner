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
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.SummaryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.ProjectSummaryUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for the getting list of summaries of the project
 */
@Service
@Slf4j
public class GetSummaryListByProjectIDImpl implements IUseCaseImplementation<
        GetSummaryListRequest, GetSummaryListProcess, GetSummaryListResponse> {

    /**
     * Data/Repository layer implementation for the summary collection
     */
    private final SummaryRepository summaryRepository;

    public GetSummaryListByProjectIDImpl(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SUMMARY, UseCasesEnums.GET_BY_PROJECT_ID, this);
    }

    @Override
    public GetSummaryListRequest preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String projectId = queryParams.get("projectId");
        String qualityGate = queryParams.get("qualityGate");
        if (!CommonUtils.checkIfObjectIsNotNull(projectId)) {
            throw new BadRequest("Project ID is required");
        }
        if (!CommonUtils.checkIfObjectIsNotNull(qualityGate)) {
            throw new BadRequest("Quality Gate is required");
        }
        return new GetSummaryListRequest(projectId, qualityGate);
    }

    @Override
    public GetSummaryListProcess process(GetSummaryListRequest summaryListRequest) {
        List<SummaryEntity> summaryEntityList = summaryRepository.getByProjectId(summaryListRequest.getId());

        return new GetSummaryListProcess(summaryEntityList,
                Integer.parseInt(summaryListRequest.getQualityGate()));
    }

    @Override
    public GetSummaryListResponse postProcess(GetSummaryListProcess getSummaryListProcess) {

        List<SummaryEntity> summaryList = new ArrayList<>(getSummaryListProcess.getSummaryEntities());
        int latestDifference = 0;
        boolean hasChange = false;
        if (summaryList.size() > 1) {
            ProjectSummaryUtils.sortEntitiesByTimestampDescending(summaryList);
            latestDifference = summaryList.get(0).getPercentage() - summaryList.get(1).getPercentage();
            hasChange = true;
        }

        return ProjectSummaryUtils.getSummaryListResponse(
                summaryList, getSummaryListProcess.getQualityGate(), latestDifference, hasChange
        );
    }
}
