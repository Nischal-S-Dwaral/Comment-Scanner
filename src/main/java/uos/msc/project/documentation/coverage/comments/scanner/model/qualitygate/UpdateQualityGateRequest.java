package uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateQualityGateRequest {

    private String userId;
    private String updatedQualityGate;
}
