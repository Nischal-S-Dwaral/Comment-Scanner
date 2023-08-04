package uos.msc.project.documentation.coverage.comments.scanner.entity;

import lombok.Data;

@Data
public class FileEntity {

    private String id;
    private String name;
    private String directoryId;
    private String projectId;
    private String filePath;
    private int documentationScorePercentage;
}
