package uos.msc.project.documentation.coverage.comments.scanner.service.impl.java;

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
import uos.msc.project.documentation.coverage.comments.scanner.model.java.update.UpdateSpringBootResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.CodeLineRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.DirectoryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.FileRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.ProjectRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.service.impl.github.GetGithubCodeImpl;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.github.JavaGithubUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Service implementation for the updating the documentation comments based of the GitHub Codebase configuration
 */
@Service
@Slf4j
public class UpdateSpringBootImpl implements IUseCaseImplementation<
        String, Boolean, UpdateSpringBootResponse> {

    private final CodeLineRepository codeLineRepository;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;
    private final ProjectRepository projectRepository;
    /**
     * Implementation class to get the data from GitHub repository
     */
    private final GetGithubCodeImpl getGithubCode;

    /**
     * Implementation class to parse the GitHub Content
     */
    private final ParseGHContentImpl parseGHContent;

    public UpdateSpringBootImpl(CodeLineRepository codeLineRepository, DirectoryRepository directoryRepository, FileRepository fileRepository, ProjectRepository projectRepository, GetGithubCodeImpl getGithubCode, ParseGHContentImpl parseGHContent) {
        this.codeLineRepository = codeLineRepository;
        this.directoryRepository = directoryRepository;
        this.fileRepository = fileRepository;
        this.projectRepository = projectRepository;
        this.getGithubCode = getGithubCode;
        this.parseGHContent = parseGHContent;
    }

    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.JAVA, UseCasesEnums.UPDATE_SPRING_BOOT, this);
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

        List<CompletableFuture<Boolean>> deleteFutures = Stream.of(
                directoryRepository.deleteDocumentsByProjectIdInBatch(projectId),
                fileRepository.deleteDocumentsByProjectIdInBatch(projectId),
                codeLineRepository.deleteDocumentsByProjectIdInBatch(projectId)
        ).toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
        allOf.join();
        boolean allDeletionsSuccessful = deleteFutures.stream().allMatch(CompletableFuture::join);

        if (allDeletionsSuccessful) {
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
        } else {
            return null;
        }
    }

    @Override
    public UpdateSpringBootResponse postProcess(Boolean isComplete) {
        return new UpdateSpringBootResponse(isComplete);
    }
}
