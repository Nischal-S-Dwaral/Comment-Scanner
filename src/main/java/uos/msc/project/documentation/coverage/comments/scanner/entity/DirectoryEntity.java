package uos.msc.project.documentation.coverage.comments.scanner.entity;

import lombok.Data;

import java.util.List;

@Data
public class DirectoryEntity {

    private String id;
    private String path;
    private String name;
    private String projectId;
    private List<String> subdirectories;
    private List<String> files;
    private int coverage;
    private boolean baseDirectory;
}
