package uos.msc.project.documentation.coverage.comments.scanner.service.impl.project;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.enums.ServiceEnum;
import uos.msc.project.documentation.coverage.comments.scanner.enums.UseCasesEnums;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectCodeProcess;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectCodeResponse;
import uos.msc.project.documentation.coverage.comments.scanner.repository.CodeLineRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.FileRepository;
import uos.msc.project.documentation.coverage.comments.scanner.service.IUseCaseImplementation;
import uos.msc.project.documentation.coverage.comments.scanner.service.UseCasesAdaptorFactory;
import uos.msc.project.documentation.coverage.comments.scanner.utils.CommonUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.RequestUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GetProjectCodeImpl implements IUseCaseImplementation<
        String, GetProjectCodeProcess, GetProjectCodeResponse> {

    private final CodeLineRepository codeLineRepository;
    private final FileRepository fileRepository;

    public GetProjectCodeImpl(CodeLineRepository codeLineRepository, FileRepository fileRepository) {
        this.codeLineRepository = codeLineRepository;
        this.fileRepository = fileRepository;
    }

    /**
     * Initialise the endpoints for the Rest API Controller
     */
    @PostConstruct
    public void init() {
        UseCasesAdaptorFactory.
                registerAdaptor(ServiceEnum.PROJECT, UseCasesEnums.GET_PROJECT_CODE, this);
    }

    @Override
    public String preProcess(HttpServletRequest request) {
        Map<String, String> queryParams = RequestUtils.getQueryParams(request);
        String fileId = queryParams.get("fileId");
        if (!CommonUtils.checkIfObjectIsNotNull(fileId)) {
            throw new BadRequest("File ID is required");
        }
        return fileId;
    }

    @Override
    public GetProjectCodeProcess process(String fileId) {

        List<CodeLineEntity> codeLineEntities = codeLineRepository.getCodeLines(fileId);
        FileEntity fileEntity = fileRepository.getFileEntity(fileId);

        return new GetProjectCodeProcess(fileEntity, codeLineEntities);
    }

    @Override
    public GetProjectCodeResponse postProcess(GetProjectCodeProcess process) {

        FileEntity file = process.getFileEntity();
        List<CodeLineEntity> codeLineEntities = new ArrayList<>(process.getCodeLineEntityList());
        codeLineEntities.sort(Comparator.comparingInt(CodeLineEntity::getLineNumber));

        return new GetProjectCodeResponse(
               file.getDocumentationScorePercentage(), file.getFilePath(), codeLineEntities
        );
    }
}
