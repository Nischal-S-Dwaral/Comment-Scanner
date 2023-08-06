package uos.msc.project.documentation.coverage.comments.scanner.model.project.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetProjectCodeResponse extends RestApiResponse {

    private int coverage;
    private String path;
    private List<CodeLineEntity> lineOfCodes;
}
