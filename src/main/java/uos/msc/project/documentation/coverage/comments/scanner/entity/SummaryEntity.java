package uos.msc.project.documentation.coverage.comments.scanner.entity;

import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class SummaryEntity {

    private String id;
    private String projectId;
    private Timestamp timestamp;
    private int percentage;
    private String repository;

    public SummaryEntity(String projectId, Timestamp timestamp, int percentage, String repository) {
        this.projectId = projectId;
        this.timestamp = timestamp;
        this.percentage = percentage;
        this.repository = repository;
    }
}
