package uos.msc.project.documentation.coverage.comments.scanner.service.impl.project;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectCodeStructureProcess;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectCodeStructureResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.DirectoryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.FileRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.ProjectCodeStructureUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GetProjectCodeStructureImpl implements IUseCaseImplementation<
        String, GetProjectCodeStructureProcess, GetProjectCodeStructureResponse> {

    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    public GetProjectCodeStructureImpl(DirectoryRepository directoryRepository, FileRepository fileRepository) {
        this.directoryRepository = directoryRepository;
        this.fileRepository = fileRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.PROJECT, UseCasesEnums.GET_PROJECT_CODE_STRUCTURE, this);
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
    public GetProjectCodeStructureProcess process(String projectId) {

        List<DirectoryEntity> directoryEntityList = directoryRepository.getDirectoryEntityList(projectId);
        List<FileEntity> fileEntityList = fileRepository.getFileEntityList(projectId);

        return new GetProjectCodeStructureProcess(directoryEntityList, fileEntityList);
    }

    @Override
    public GetProjectCodeStructureResponse postProcess(GetProjectCodeStructureProcess getProjectCodeStructureProcess) {
        return ProjectCodeStructureUtils.getResponse(
                getProjectCodeStructureProcess.getDirectoryEntityList(),
                getProjectCodeStructureProcess.getFileEntityList()
        );
    }
}
