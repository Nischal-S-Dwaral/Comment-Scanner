package uos.msc.project.documentation.coverage.comments.scanner.model.java.parse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IntermediateValidationResult {

    private double score;
    private List<String> reviewComment;
}
