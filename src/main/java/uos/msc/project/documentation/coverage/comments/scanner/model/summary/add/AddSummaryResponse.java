package uos.msc.project.documentation.coverage.comments.scanner.model.summary.add;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AddSummaryResponse extends RestApiResponse {

    private String summaryId;
}
