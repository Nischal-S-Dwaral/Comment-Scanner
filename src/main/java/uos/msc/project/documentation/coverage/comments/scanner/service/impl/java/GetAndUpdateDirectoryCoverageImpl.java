package uos.msc.project.documentation.coverage.comments.scanner.service.impl.java;

import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.repository.DirectoryRepository;
import uos.msc.project.documentation.coverage.comments.scanner.repository.FileRepository;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.CommonJavadocUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetAndUpdateDirectoryCoverageImpl {

    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    public GetAndUpdateDirectoryCoverageImpl(DirectoryRepository directoryRepository, FileRepository fileRepository) {
        this.directoryRepository = directoryRepository;
        this.fileRepository = fileRepository;
    }

    public int calculateDirectoryScore(String projectId) {

        List<DirectoryEntity> directoryEntityList = directoryRepository.getDirectoryEntityList(projectId);
        List<FileEntity> fileEntityList = fileRepository.getFileEntityList(projectId);

        Map<String, List<FileEntity>> filesByDirectoryId = new HashMap<>();
        for (FileEntity file : fileEntityList) {
            String directoryId = file.getDirectoryId();
            if (!filesByDirectoryId.containsKey(directoryId)) {
                filesByDirectoryId.put(directoryId, new ArrayList<>());
            }
            filesByDirectoryId.get(directoryId).add(file);
        }

        DirectoryEntity baseDirectoryEntity = CommonJavadocUtils.findDirectoryWithBasePath(directoryEntityList);
        int summaryCoverageScore = 0;
        List<DirectoryEntity> updatedDirectoryList = new ArrayList<>();

        for (DirectoryEntity directory : directoryEntityList) {
            dfsCalculateDocumentationScore(directory, filesByDirectoryId, directoryEntityList);
            if (directory.getId().equals(baseDirectoryEntity.getId())) {
                directory.setBaseDirectory(Boolean.TRUE);
                summaryCoverageScore = directory.getCoverage();
            } else {
                directory.setBaseDirectory(Boolean.FALSE);
            }
            updatedDirectoryList.add(directory);
        }

        List<String> savedDirectories = directoryRepository.updateList(updatedDirectoryList);
        return savedDirectories.size() == directoryEntityList.size() ? summaryCoverageScore : 0;
    }

    // DFS method to calculate the documentationScorePercentage for each directory and its subdirectories.
    private void dfsCalculateDocumentationScore(DirectoryEntity directory,
                                                Map<String, List<FileEntity>> filesByDirectoryId,
                                                List<DirectoryEntity> directoryEntityList) {

        String directoryId = directory.getId();
        List<FileEntity> filesInDirectory = filesByDirectoryId.getOrDefault(directoryId, new ArrayList<>());

        double totalScore = 0.0;
        for (FileEntity file : filesInDirectory) {
            totalScore += file.getDocumentationScorePercentage();
        }

        double avgScore = filesInDirectory.isEmpty() ? 0.0 : totalScore / filesInDirectory.size();
        int totalDirectories = 1;

        for (String subDirId : directory.getSubdirectories()) {
            DirectoryEntity subDirectory = findDirectoryById(directoryEntityList, subDirId);

            if (subDirectory != null) {
                dfsCalculateDocumentationScore(subDirectory, filesByDirectoryId, directoryEntityList);
                avgScore += subDirectory.getCoverage();
                totalDirectories++;
            }
        }

        avgScore /= totalDirectories;
        directory.setCoverage((int) Math.round(avgScore));
    }

    private DirectoryEntity findDirectoryById(List<DirectoryEntity> directories, String id) {
        for (DirectoryEntity directory : directories) {
            if (directory.getId().equals(id)) {
                return directory;
            }
        }
        return null;
    }
}
