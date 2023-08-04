package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ConvertedParsedInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CodeLineUtils {

    public static List<CodeLineEntity> getCodeLines(String fileContent,
                                                    CompilationUnit compilationUnit,
                                                    List<ConvertedParsedInfo> convertedParsedInfoList,
                                                    String projectId, String fileId) {

        List<CodeLineEntity> codeLineEntityList = new ArrayList<>();

        // Get the range of the main file content
        Range fileRange = compilationUnit.getRange().orElse(null);

        List<Range> ranges = new ArrayList<>();
        if (fileRange != null) {
            ranges.add(fileRange);
        }

        // Sort the ranges based on their line positions
        ranges.sort(Comparator.comparingInt(range -> range.begin.line));

        try(BufferedReader reader = new BufferedReader(new StringReader(fileContent))) {
            String codeLine;
            int lineNumber = 1;

            while ((codeLine = reader.readLine()) != null) {

                for (Range range : ranges) {
                    if (lineNumber >= range.begin.line && lineNumber <= range.end.line) {
                        boolean lineNumberFound = false;

                        for (ConvertedParsedInfo convertedParsedInfo : convertedParsedInfoList) {
                            if (convertedParsedInfo.getLineNumber() == lineNumber) {
                                CodeLineEntity codeLineEntity = new CodeLineEntity(
                                        lineNumber,
                                        codeLine,
                                        true,
                                        convertedParsedInfo.getColour(),
                                        convertedParsedInfo.getComments(),
                                        projectId,
                                        fileId
                                );
                                codeLineEntityList.add(codeLineEntity);
                                lineNumberFound = true;
                                break;
                            }
                        }

                        if (!lineNumberFound) {
                            codeLineEntityList.add(new CodeLineEntity(lineNumber, codeLine, projectId, fileId));
                        }

                        break;
                    }
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeLineEntityList;
    }
}
