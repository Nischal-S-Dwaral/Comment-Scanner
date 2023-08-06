package uos.msc.project.documentation.coverage.comments.scanner.utils;

import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.ProjectSummary;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.get.GetSummaryListResponse;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectSummaryUtils {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private static String getQualityGatePass(int percentage, int qualityGate) {
        return percentage >= qualityGate ? "PASSED" : "FAILED";
    }

    public static List<ProjectSummary> convertSummaryEntities(List<SummaryEntity> summaryEntities, int qualityGate) {

        List<ProjectSummary> projectSummaries = new ArrayList<>();

        for (SummaryEntity summaryEntity : summaryEntities) {
            ProjectSummary projectSummary = new ProjectSummary();
            projectSummary.setId(summaryEntity.getId());
            projectSummary.setProjectId(summaryEntity.getProjectId());
            projectSummary.setUserId(summaryEntity.getUserId());
            projectSummary.setCoveragePercentage(summaryEntity.getPercentage());
            projectSummary.setRepository(summaryEntity.getRepository());
            projectSummary.setDate(formatDate(summaryEntity.getTimestamp().toDate()));
            projectSummary.setTime(formatTime(summaryEntity.getTimestamp().toSqlTimestamp().toString()));
            projectSummary.setQualityGatePass(getQualityGatePass(summaryEntity.getPercentage(), qualityGate));

            projectSummaries.add(projectSummary);
        }

        return projectSummaries;
    }

    private static String formatDate(Date date) {
        return dateFormatter.format(date);
    }

    private static String formatTime(String timestampString) {
        try {
            Date timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timestampString);
            return timeFormatter.format(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return timestampString;
        }
    }

    public static void sortEntitiesByTimestampDescending(List<SummaryEntity> entities) {
        entities.sort((entity1, entity2) -> entity2.getTimestamp().compareTo(entity1.getTimestamp()));
    }

    public static GetSummaryListResponse getSummaryListResponse(List<SummaryEntity> summaryEntities, int qualityGate,
                                                                int latestDifference, boolean hasChange) {

        GetSummaryListResponse response = new GetSummaryListResponse();
        response.setProjectSummaryList(convertSummaryEntities(summaryEntities, qualityGate));
        response.setLatestCoverage(summaryEntities.get(0).getPercentage());
        response.setQualityGateResult(getQualityGatePass(summaryEntities.get(0).getPercentage(), qualityGate));

        if (hasChange) {
            response.setHasChange(true);
            response.setIncrease(latestDifference > 0);
            response.setChangePercentage(Math.abs(latestDifference));
        }

        return response;
    }
}

