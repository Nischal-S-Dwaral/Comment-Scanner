package uos.msc.project.documentation.coverage.comments.scanner.service.impl.summary;

import com.google.cloud.Timestamp;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.add.AddSummaryResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.ProjectRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.SummaryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.service.impl.java.GetAndUpdateDirectoryCoverageImpl;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.Map;

/**
 * Service implementation for the adding summary of the project
 */
@Service
@Slf4j
public class AddSummaryImpl implements IUseCaseImplementation<
        String, String, AddSummaryResponse> {

    /**
     * Data/Repository layer implementation for the summary collection
     */
    private final SummaryRepository summaryRepository;

    /**
     * Data/Repository layer implementation for the project collection
     */
    private final ProjectRepository projectRepository;

    private final GetAndUpdateDirectoryCoverageImpl getAndUpdateDirectoryCoverage;

    public AddSummaryImpl(SummaryRepository summaryRepository, ProjectRepository projectRepository, GetAndUpdateDirectoryCoverageImpl getAndUpdateDirectoryCoverage) {
        this.summaryRepository = summaryRepository;
        this.projectRepository = projectRepository;
        this.getAndUpdateDirectoryCoverage = getAndUpdateDirectoryCoverage;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.SUMMARY, UseCasesEnums.ADD, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String projectId = queryParams.get("projectId");
        if (!CommonUtils.checkIfObjectIsNotNull(projectId)) {
            throw new BadRequest("Project ID is required");
        }
        return projectId;
    }

    @Override
    public String process(String projectId) {
        ProjectEntity project = projectRepository.findById(projectId);
        int coverage = getAndUpdateDirectoryCoverage.calculateDirectoryScore(projectId);

        SummaryEntity summaryEntity = new SummaryEntity(
                projectId, Timestamp.now(), coverage, project.getRepository(), project.getUserId());
        return summaryRepository.add(summaryEntity);
    }

    @Override
    public AddSummaryResponse postProcess(String id) {
        return new AddSummaryResponse(id);
    }
}
