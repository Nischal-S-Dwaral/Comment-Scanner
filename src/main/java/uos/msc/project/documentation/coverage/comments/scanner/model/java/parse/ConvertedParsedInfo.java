package uos.msc.project.documentation.coverage.comments.scanner.model.java.parse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertedParsedInfo {

    private int lineNumber;
    private String colour;
    private String comments;
}
