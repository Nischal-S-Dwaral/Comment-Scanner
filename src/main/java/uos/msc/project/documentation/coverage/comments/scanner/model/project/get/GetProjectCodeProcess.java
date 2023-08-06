package uos.msc.project.documentation.coverage.comments.scanner.model.project.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;

import java.util.List;

@Data
@AllArgsConstructor
public class GetProjectCodeProcess {

    private FileEntity fileEntity;
    private List<CodeLineEntity> codeLineEntityList;
}
