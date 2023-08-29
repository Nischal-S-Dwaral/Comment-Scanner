package uos.msc.project.documentation.coverage.comments.scanner.model.java.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uos.msc.project.documentation.coverage.comments.scanner.model.RestApiResponse;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UpdateSpringBootResponse extends RestApiResponse {

    private Boolean isComplete;
}
