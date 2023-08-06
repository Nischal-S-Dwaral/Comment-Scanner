package uos.msc.project.documentation.coverage.comments.scanner.model.summary.get;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.ProjectSummary;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSummaryListResponse extends RestApiResponse {

    private List<ProjectSummary> projectSummaryList;
    private boolean hasChange;
    private boolean isIncrease;
    private int changePercentage;
    private int latestCoverage;
    private String qualityGateResult;

    public GetSummaryListResponse(List<ProjectSummary> projectSummaryList) {
        this.projectSummaryList = projectSummaryList;
    }

    public GetSummaryListResponse() {
    }
}
