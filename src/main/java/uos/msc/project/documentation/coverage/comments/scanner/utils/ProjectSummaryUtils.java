package uos.msc.project.documentation.coverage.comments.scanner.utils;

import uos.msc.project.documentation.coverage.comments.scanner.entity.SummaryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.summary.ProjectSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}

