package uos.msc.project.documentation.coverage.comments.scanner.model.summary.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.ProjectSummary;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetSummaryListByUserIDResponse extends RestApiResponse {

    private List<ProjectSummary> projectSummaryList;
}
