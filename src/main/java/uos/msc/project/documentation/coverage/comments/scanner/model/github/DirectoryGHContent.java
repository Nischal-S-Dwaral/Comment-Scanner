package uos.msc.project.documentation.coverage.comments.scanner.model.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kohsuke.github.GHContent;

import java.util.List;

@Data
@AllArgsConstructor
public class DirectoryGHContent {

    private String path;
    private String name;
    private List<GHContent> subDirectories;
    private List<GHContent> directoryFiles;
}
