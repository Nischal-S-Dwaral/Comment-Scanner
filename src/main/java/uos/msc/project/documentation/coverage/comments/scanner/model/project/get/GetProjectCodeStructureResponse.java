package uos.msc.project.documentation.coverage.comments.scanner.model.project.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.ProjectCodeDirectoryStructure;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetProjectCodeStructureResponse extends RestApiResponse {

    private List<ProjectCodeDirectoryStructure> projectCodeDirectoryStructureList;
}
