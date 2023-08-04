package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.javadoc.JavadocBlockTag;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ConvertedParsedInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.IntermediateValidationResult;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonJavadocUtils {

    public static JavadocInfo extractJavadocInfo(JavadocComment javadocComment) {
        JavadocInfo javadocInfo = new JavadocInfo();
        javadocInfo.setDescription(javadocComment.parse().getDescription().toText().trim());
        javadocComment.getRange().ifPresent(range -> {
            javadocInfo.setStartLine(range.begin.line);
            javadocInfo.setEndLine(range.end.line);
        });
        return javadocInfo;
    }

    public static IntermediateValidationResult validateParametersDocumentation(List<JavadocBlockTag> javadocBlockTagList, NodeList<Parameter> parameterNodeList) {

        List<String> parametersDocumentationReviewComments = new ArrayList<>();
        AtomicInteger actualParamDocumentationCount = new AtomicInteger();

        parameterNodeList.forEach(parameter -> {
            String parameterName = parameter.getNameAsString();

            boolean hasParamTag = javadocBlockTagList.stream()
                    .anyMatch(tag -> tag != null
                            && tag.getTagName().equals("param")
                            && tag.getName().isPresent() && tag.getName().get().equals(parameterName));

            if (!hasParamTag) {
                parametersDocumentationReviewComments.add("Missing @param tag for parameter: " + parameterName);
            } else {
                boolean isParamDescriptionWrong = javadocBlockTagList.stream()
                        .anyMatch(tag -> tag != null
                                && tag.getTagName().equals("param")
                                && tag.getName().isPresent() && tag.getName().get().equals(parameterName)
                                && tag.getContent().isEmpty());

                if (isParamDescriptionWrong) {
                    parametersDocumentationReviewComments.add("Missing @param tag description for parameter: " + parameterName);
                } else {
                    actualParamDocumentationCount.getAndIncrement();
                }
            }
        });

        return new IntermediateValidationResult(
                (parameterNodeList.size() == 0) ? 1.0 : (double) actualParamDocumentationCount.get() / parameterNodeList.size(),
                parametersDocumentationReviewComments
        );
    }

    public static List<ConvertedParsedInfo> getConvertedParsedInfo(List<ParsingInfo> parsingInfoList) {
        List<ConvertedParsedInfo> convertedParsedInfoList = new ArrayList<>();

        for (ParsingInfo parsingInfo : parsingInfoList) {
            int startLine = parsingInfo.getJavadocStartLine();
            int endLine = parsingInfo.getJavadocEndLine();
            String colour = getColorFromJavadocScore(parsingInfo.getJavadocScore());
            String comments = getCommentsFromJavadocScore(parsingInfo.getJavadocScore(), parsingInfo.getDocumentationReviewComments());

            if (startLine == 0) {
                convertedParsedInfoList.add(new ConvertedParsedInfo(parsingInfo.getStartLine(), colour, comments));
            } else {
                for (int lineNumber = startLine; lineNumber <= endLine; lineNumber++) {
                    convertedParsedInfoList.add(new ConvertedParsedInfo(lineNumber, colour, comments));
                }
            }
        }

        return convertedParsedInfoList;
    }

    private static String getColorFromJavadocScore(double javadocScore) {
        if (javadocScore == 0.0) {
            return "red";
        } else if (javadocScore == 1.0) {
            return "green";
        } else {
            return "orange";
        }
    }

    private static String getCommentsFromJavadocScore(double javadocScore, List<String> reviewComments) {
        if (javadocScore == 1.0) {
            return "Documentation is good";
        } else {
            return reviewComments.toString();
        }
    }

    public static int getDocumentationScorePercentage(List<ParsingInfo> parsingInfoList) {

        double averageJavadocScore = parsingInfoList.stream()
                .mapToDouble(ParsingInfo::getJavadocScore)
                .average()
                .orElse(0.0);

        return (int) Math.round(averageJavadocScore * 100);
    }

    public static DirectoryEntity findDirectoryWithBasePath(List<DirectoryEntity> directories) {
        if (directories == null || directories.isEmpty()) {
            return null;
        }

        String[] components = directories.get(0).getPath().split("/");

        for (int i = 1; i < directories.size(); i++) {
            String path = directories.get(i).getPath();
            String[] otherComponents = path.split("/");

            int minLength = Math.min(components.length, otherComponents.length);

            int j;
            for (j = 0; j < minLength; j++) {
                if (!components[j].equals(otherComponents[j])) {
                    break;
                }
            }

            // Update components to contain only the common prefix
            components = java.util.Arrays.copyOfRange(components, 0, j);
        }

        // Join the common components to form the base path
        String basePath = String.join("/", components);

        for (DirectoryEntity directory : directories) {
            if (directory.getPath().equals(basePath)) {
                return directory;
            }
        }
        return null;
    }
}
