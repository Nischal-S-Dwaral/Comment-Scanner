package uos.msc.project.documentation.coverage.comments.scanner.model.summary;

import lombok.Data;

@Data
public class ProjectSummary {

    private String id;
    private String projectId;
    private String userId;
    private int coveragePercentage;
    private String repository;
    private String date;
    private String time;
    private String qualityGatePass;
}