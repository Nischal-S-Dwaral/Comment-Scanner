package uos.msc.project.documentation.coverage.comments.scanner.service.impl.java;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.github.DirectoryGHContent;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.add.AddSpringBootResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.ProjectRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.service.impl.github.GetGithubCodeImpl;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.github.JavaGithubUtils;

import java.util.List;
import java.util.Map;

/**
 * Service implementation for the adding of the GitHub Codebase configuration
 */
@Service
@Slf4j
public class AddSpringBootImpl implements IUseCaseImplementation<
        String, Boolean, AddSpringBootResponse> {

    /**
     * Data/Repository layer implementation for the project collection
     */
    private final ProjectRepository projectRepository;

    /**
     * Implementation class to get the data from GitHub repository
     */
    private final GetGithubCodeImpl getGithubCode;

    /**
     * Implementation class to parse the GitHub Content
     */
    private final ParseGHContentImpl parseGHContent;

    /**
     * Constructor to inject the repository
     *
     * @param projectRepository repository for the project collection
     * @param getGithubCode implementation to get the data from GitHub
     * @param parseGHContent Implementation class to parse the GitHub Content
     */
    public AddSpringBootImpl(ProjectRepository projectRepository, GetGithubCodeImpl getGithubCode, ParseGHContentImpl parseGHContent) {
        this.projectRepository = projectRepository;
        this.getGithubCode = getGithubCode;
        this.parseGHContent = parseGHContent;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.JAVA, UseCasesEnums.ADD_SPRING_BOOT, this);
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
    public Boolean process(String projectId) {
        try {
            ProjectEntity project = projectRepository.findById(projectId);
            GHRepository ghRepository = getGithubCode.getGHRepository(
                    project.getOwner(),
                    project.getRepository(),
                    project.getEncodedAccessToken()
            );
            List<DirectoryGHContent> directoryGHContentList = JavaGithubUtils.getDirectoryGHContentList(ghRepository);
            List<String> savedDirectoryIds = parseGHContent.parseGHContentList(projectId, directoryGHContentList);

            return savedDirectoryIds.size() == directoryGHContentList.size();
        } catch (Exception exception) {
            throw new InternalServerError("Error processing adding spring boot project: " + exception.getMessage());
        }
    }

    @Override
    public AddSpringBootResponse postProcess(Boolean isComplete) {
        return new AddSpringBootResponse(isComplete);
    }
}
