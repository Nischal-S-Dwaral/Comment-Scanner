package uos.msc.project.documentation.coverage.comments.scanner.model.summary.get;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSummaryListByUserIDRequest {

    private String userID;
    private String qualityGate;
}
