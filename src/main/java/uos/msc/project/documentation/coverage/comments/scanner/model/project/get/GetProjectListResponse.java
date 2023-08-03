package uos.msc.project.documentation.coverage.comments.scanner.model.project.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.entity.ProjectEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetProjectListResponse extends RestApiResponse {

    private List<ProjectEntity> projectEntityList;
}
