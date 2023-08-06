package uos.msc.project.documentation.coverage.comments.scanner.model.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectCodeDirectoryStructure {

    private String id;
    private String name;
    private String path;
    private int coverage;
    private boolean isBaseDirectory;
    private List<ProjectCodeDirectoryStructure> directories;
    private List<ProjectCodeFileStructure> files;
}
