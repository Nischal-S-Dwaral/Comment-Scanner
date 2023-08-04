package uos.msc.project.documentation.coverage.comments.scanner.service.impl.project;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectListResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.ProjectRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Service implementation for the get project list by userID
 */
@Service
@Slf4j
public class GetProjectListImpl implements IUseCaseImplementation<
        String, List<ProjectEntity>, GetProjectListResponse> {

    /**
     * Data/Repository layer implementation for the project collection
     */
    private final ProjectRepository projectRepository;

    /**
     * Constructor to inject the repository
     * @param projectRepository repository for the project collection
     */
    public GetProjectListImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Initialise the endpoint for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.PROJECT, UseCasesEnums.GET_LIST, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String userId = queryParams.get("userId");
        if (!CommonUtils.checkIfObjectIsNotNull(userId)) {
            throw new BadRequest("User ID is required get find");
        }
        return userId;
    }

    @Override
    public List<ProjectEntity> process(String userId) {
        try {
            ApiFuture<QuerySnapshot> querySnapshotApiFuture = projectRepository.getByUserId(userId);
            List<QueryDocumentSnapshot> queryDocumentSnapshotList = querySnapshotApiFuture.get().getDocuments();
            return queryDocumentSnapshotList.stream()
                    .map(data -> data.toObject(ProjectEntity.class))
                    .toList();
        } catch (ExecutionException | InterruptedException exception) {
            throw new InternalServerError("Failed to get project list from Firestore for user id- "+userId+ " :"+exception.getMessage());
        }
    }

    @Override
    public GetProjectListResponse postProcess(List<ProjectEntity> projectEntities) {
        return new GetProjectListResponse(projectEntities);
    }
}
