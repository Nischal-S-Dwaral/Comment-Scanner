package uos.msc.project.documentation.coverage.comments.scanner.service.impl.project;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.add.AddProjectResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.ProjectRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.EncryptionUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

/**
 * Service implementation for the adding a project
 */
@Service
@Slf4j
public class AddProjectImpl implements IUseCaseImplementation<
        ProjectEntity, String, AddProjectResponse> {

    /**
     * Data/Repository layer implementation for the project collection
     */
    private final ProjectRepository projectRepository;

    /**
     * Constructor to inject the repository
     * @param projectRepository repository for the project collection
     */
    public AddProjectImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Initialise the endpoint for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.PROJECT, UseCasesEnums.ADD, this);
    }

    @Override
    public ProjectEntity preProcess(HttpServletRequest request) {
        ProjectEntity projectEntity = RequestUtils.getRequestData(
                request,
                ProjectEntity.class
        );
        if (projectEntity.getUserId().isEmpty()) {
            throw new BadRequest("User ID cannot be empty");
        }
        return projectEntity;
    }

    @Override
    public String process(ProjectEntity projectEntity) {
        try {
            String encryptedKey = EncryptionUtils.encrypt(projectEntity.getEncodedAccessToken(), "123456789");
            projectEntity.setEncodedAccessToken(encryptedKey);

            return projectRepository.add(projectEntity);

        } catch (Exception exception) {
            throw new InternalServerError("Error while adding project entity: " +exception.getMessage());
        }
    }

    @Override
    public AddProjectResponse postProcess(String projectId) {
        return new AddProjectResponse(projectId);
    }
}
