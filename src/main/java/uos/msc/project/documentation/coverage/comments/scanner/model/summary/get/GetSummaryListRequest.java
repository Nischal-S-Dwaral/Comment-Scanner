package uos.msc.project.documentation.coverage.comments.scanner.model.summary.get;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSummaryListRequest {

    private String id;
    private String qualityGate;
}
