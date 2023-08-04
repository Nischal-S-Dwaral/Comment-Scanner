package uos.msc.project.documentation.coverage.comments.scanner.model.qualitygate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetQualityGateResponse extends RestApiResponse  {

    private String qualityGate;
}
