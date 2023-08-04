package uos.msc.project.documentation.coverage.comments.scanner.model.java.parse;

import lombok.Data;

@Data
public class JavadocInfo {

    private String description;
    private int startLine;
    private int endLine;
}
