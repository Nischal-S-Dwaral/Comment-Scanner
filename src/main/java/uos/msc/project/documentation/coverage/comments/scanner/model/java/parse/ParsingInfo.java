package uos.msc.project.documentation.coverage.comments.scanner.model.java.parse;

import lombok.Data;

import java.util.List;

@Data
public class ParsingInfo {

    private String type;
    private String name;
    private String javadocDescription;
    private int javadocStartLine;
    private int javadocEndLine;
    private int startLine;
    private int endLine;
    private List<String> documentationReviewComments;
    private double javadocScore;
}
