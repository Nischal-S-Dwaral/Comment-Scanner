package uos.msc.project.documentation.coverage.comments.scanner.service.impl.java;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.kohsuke.github.GHContent;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.constants.JavaSpringBootConstants;
import uos.msc.project.documentation.coverage.comments.scanner.entity.CodeLineEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.github.DirectoryGHContent;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ConvertedParsedInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;
import uos.msc.project.documentation.coverage.comments.scanner.repository.CodeLineRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.DirectoryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.FileRepository;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ParseGHContentImpl {

    private final Map<String, String> directoryIdMap = new HashMap<>();
    private final FileRepository fileRepository;
    private final CodeLineRepository codeLineRepository;
    private final DirectoryRepository directoryRepository;

    public ParseGHContentImpl(FileRepository fileRepository, CodeLineRepository codeLineRepository, DirectoryRepository directoryRepository) {
        this.fileRepository = fileRepository;
        this.codeLineRepository = codeLineRepository;
        this.directoryRepository = directoryRepository;
    }

    public List<String> parseGHContentList(String projectId, List<DirectoryGHContent> directoryGHContentList) {

        List<String> savedDirectoryIds = new ArrayList<>();
        for (DirectoryGHContent directoryGHContent : directoryGHContentList) {

            DirectoryEntity directoryEntity = new DirectoryEntity();
            directoryEntity.setName(directoryGHContent.getName());
            directoryEntity.setProjectId(projectId);
            directoryEntity.setPath(directoryGHContent.getPath());

            String directoryId = directoryIdMap.computeIfAbsent(directoryGHContent.getPath(), key -> UUID.randomUUID().toString());
            directoryEntity.setId(directoryId);

            //Parse SubDirectories
            List<GHContent> contentSubDirectories = directoryGHContent.getSubDirectories();
            if (contentSubDirectories.isEmpty()) {
                directoryEntity.setSubdirectories(Collections.emptyList());
            } else {
                List<String> subDirectoriesKeys = contentSubDirectories.stream()
                        .map(ghContent -> directoryIdMap.computeIfAbsent(ghContent.getPath(), key -> UUID.randomUUID().toString()))
                        .toList();

                directoryEntity.setSubdirectories(subDirectoriesKeys);
            }

            //Parse Files
            List<GHContent> directoryFiles = directoryGHContent.getDirectoryFiles();
            if (directoryFiles.isEmpty()) {
                directoryEntity.setFiles(Collections.emptyList());
            } else {
                List<String> filesKeys = parseDirectoryFiles(directoryFiles, directoryId, projectId);
                directoryEntity.setFiles(filesKeys);
            }

            String savedId = directoryRepository.add(directoryEntity);
            savedDirectoryIds.add(savedId);
        }

        return savedDirectoryIds;
    }

    private List<String> parseDirectoryFiles(List<GHContent> directoryFiles, String directoryId, String projectId) {

        List<String> fileIds = new ArrayList<>();

        for (GHContent ghContent: directoryFiles) {
            String fileName = ghContent.getName();
            String fileId = UUID.randomUUID().toString();

            FileEntity fileEntity = new FileEntity();
            fileEntity.setId(fileId);
            fileEntity.setDirectoryId(directoryId);
            fileEntity.setProjectId(projectId);
            fileEntity.setName(fileName);
            fileEntity.setFilePath(ghContent.getPath());

            if (fileName.endsWith(".java")) {
                int coveragePercentage = parseFileAndGetDocumentationPercentage(ghContent, fileName, fileId, projectId);
                fileEntity.setDocumentationScorePercentage(coveragePercentage);
            }
            String savedId = fileRepository.add(fileEntity);
            if (savedId.equalsIgnoreCase(fileId)) {
                fileIds.add(savedId);
            }
        }

        return fileIds;
    }

    private int parseFileAndGetDocumentationPercentage(GHContent ghContent, String fileName, String fileId, String projectId) {

        try (InputStream inputStream = ghContent.read()) {

            String fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            CompilationUnit compilationUnit = StaticJavaParser.parse(fileContent);

            List<ParsingInfo> parsingInfoList = getParsingInfoList(compilationUnit, fileName);
            List<ConvertedParsedInfo> convertedParsedInfoList =
                    CommonJavadocUtils.getConvertedParsedInfo(parsingInfoList);

            List<CodeLineEntity> codeLineEntityList =
                    CodeLineUtils.getCodeLines(fileContent, compilationUnit, convertedParsedInfoList, projectId, fileId);

            List<String> savedCodeLineIds = codeLineRepository.addList(codeLineEntityList);

            return (savedCodeLineIds.size() == codeLineEntityList.size()) ?
                    CommonJavadocUtils.getDocumentationScorePercentage(parsingInfoList) : 0;

        } catch (IOException ioException) {
            throw new InternalServerError("Unable to read file: " + ghContent.getName());
        }
    }

    private static List<ParsingInfo> getParsingInfoList(CompilationUnit compilationUnit, String fileName) {

        List<ParsingInfo> parsingInfoList = new ArrayList<>();

        if (fileName.contains(JavaSpringBootConstants.PACKAGE_INFO_JAVA)) {
            parsingInfoList.addAll(PackageDeclarationUtils.parse(compilationUnit));
        } else {
            parsingInfoList.addAll(ClassOrInterfaceDeclarationsUtils.parse(compilationUnit));
            parsingInfoList.addAll(FieldDeclarationsUtils.parse(compilationUnit));
            parsingInfoList.addAll(EnumDeclarationsUtils.parse(compilationUnit));
            parsingInfoList.addAll(MethodDeclarationsUtils.parse(compilationUnit));
            parsingInfoList.addAll(ConstructorDeclarationsUtils.parse(compilationUnit));
        }

        return parsingInfoList;
    }
}
