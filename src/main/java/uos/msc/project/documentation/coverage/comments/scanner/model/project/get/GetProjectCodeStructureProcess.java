package uos.msc.project.documentation.coverage.comments.scanner.model.project.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;

import java.util.List;

@Data
@AllArgsConstructor
public class GetProjectCodeStructureProcess {

    private List<DirectoryEntity> directoryEntityList;
    private List<FileEntity> fileEntityList;
}
