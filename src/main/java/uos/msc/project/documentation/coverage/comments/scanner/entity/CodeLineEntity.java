package uos.msc.project.documentation.coverage.comments.scanner.entity;

import lombok.Data;

@Data
public class CodeLineEntity {

    private String id;
    private int lineNumber;
    private String code;
    private boolean isHighlight;
    private String highlightColor;
    private String commentText;
    private String fileId;
    private String projectId;

    public CodeLineEntity(int lineNumber, String code, String projectId, String fileId) {
        this.lineNumber = lineNumber;
        this.code = code;
        this.projectId = projectId;
        this.fileId = fileId;
    }

    public CodeLineEntity(int lineNumber, String code, boolean isHighlight, String highlightColor, String commentText, String projectId, String fileId) {
        this.lineNumber = lineNumber;
        this.code = code;
        this.isHighlight = isHighlight;
        this.highlightColor = highlightColor;
        this.commentText = commentText;
        this.projectId = projectId;
        this.fileId = fileId;
    }
}
