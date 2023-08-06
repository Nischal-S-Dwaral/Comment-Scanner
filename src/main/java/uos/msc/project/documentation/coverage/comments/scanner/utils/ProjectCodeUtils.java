package uos.msc.project.documentation.coverage.comments.scanner.utils;

import uos.msc.project.documentation.coverage.comments.scanner.entity.DirectoryEntity;
import uos.msc.project.documentation.coverage.comments.scanner.entity.FileEntity;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.ProjectCodeDirectoryStructure;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.ProjectCodeFileStructure;
import uos.msc.project.documentation.coverage.comments.scanner.model.project.get.GetProjectCodeStructureResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectCodeUtils {

    public static GetProjectCodeStructureResponse getResponse(
            List<DirectoryEntity> directoryEntityList, List<FileEntity> fileEntityList) {

        Map<String, FileEntity> fileEntityMap = new HashMap<>();
        for (FileEntity fileEntity: fileEntityList) {
            fileEntityMap.put(fileEntity.getId(), fileEntity);
        }

        Map<String, DirectoryEntity> directoryEntityMap = new HashMap<>();
        for (DirectoryEntity directoryEntity: directoryEntityList) {
            directoryEntityMap.put(directoryEntity.getId(), directoryEntity);
        }

        List<ProjectCodeDirectoryStructure> projectCodeDirectoryStructureList = new ArrayList<>();
        for (DirectoryEntity directoryEntity: directoryEntityList) {
            ProjectCodeDirectoryStructure projectCodeDirectoryStructure = generateProjectCodeDirectoryStructure(
                    directoryEntity, directoryEntityMap, fileEntityMap
            );

            projectCodeDirectoryStructureList.add(projectCodeDirectoryStructure);
        }

        return new GetProjectCodeStructureResponse(projectCodeDirectoryStructureList);
    }

    private static ProjectCodeDirectoryStructure generateProjectCodeDirectoryStructure(
            DirectoryEntity directoryEntity, Map<String, DirectoryEntity> directoryEntityMap,
            Map<String, FileEntity> fileEntityMap) {

        ProjectCodeDirectoryStructure projectCodeDirectoryStructure = new ProjectCodeDirectoryStructure();
        projectCodeDirectoryStructure.setId(directoryEntity.getId());
        projectCodeDirectoryStructure.setPath(directoryEntity.getPath());
        projectCodeDirectoryStructure.setName(directoryEntity.getName());
        projectCodeDirectoryStructure.setBaseDirectory(directoryEntity.isBaseDirectory());
        projectCodeDirectoryStructure.setCoverage(directoryEntity.getCoverage());
        projectCodeDirectoryStructure.setDirectories(getSubDirectoryList(directoryEntity.getSubdirectories(), directoryEntityMap));
        projectCodeDirectoryStructure.setFiles(getFileList(directoryEntity.getFiles(), fileEntityMap));

        return projectCodeDirectoryStructure;
    }

    private static List<ProjectCodeDirectoryStructure> getSubDirectoryList(
            List<String> subdirectories, Map<String, DirectoryEntity> directoryEntityMap) {

        List<ProjectCodeDirectoryStructure> subdirectoryList = new ArrayList<>();

        for (String subdirectory : subdirectories) {
            DirectoryEntity foundDirectory = directoryEntityMap.get(subdirectory);
            if (CommonUtils.checkIfObjectIsNotNull(foundDirectory)) {

                ProjectCodeDirectoryStructure directoryStructure = new ProjectCodeDirectoryStructure();
                directoryStructure.setId(foundDirectory.getId());
                directoryStructure.setName(foundDirectory.getName());
                directoryStructure.setCoverage(foundDirectory.getCoverage());

                subdirectoryList.add(directoryStructure);
            }
        }

        return subdirectoryList;
    }

    private static List<ProjectCodeFileStructure> getFileList(List<String> files, Map<String, FileEntity> fileEntityMap) {
        List<ProjectCodeFileStructure> fileStructureList = new ArrayList<>();

        for (String file : files) {
            FileEntity foundFileEntity = fileEntityMap.get(file);
            if (CommonUtils.checkIfObjectIsNotNull(foundFileEntity)) {

                ProjectCodeFileStructure projectCodeFileStructure = new ProjectCodeFileStructure();
                projectCodeFileStructure.setId(foundFileEntity.getId());
                projectCodeFileStructure.setName(foundFileEntity.getName());
                projectCodeFileStructure.setCoverage(foundFileEntity.getDocumentationScorePercentage());

                fileStructureList.add(projectCodeFileStructure);
            }
        }

        return fileStructureList;
    }
}
