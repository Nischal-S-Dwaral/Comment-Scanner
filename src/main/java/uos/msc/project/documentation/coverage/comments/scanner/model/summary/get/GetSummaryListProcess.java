package uos.msc.project.documentation.coverage.comments.scanner.model.summary.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;

import java.util.List;

@Data
@AllArgsConstructor
public class GetSummaryListProcess {

    private List<SummaryEntity> summaryEntities;
    private int qualityGate;
}
