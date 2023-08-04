package uos.msc.project.documentation.coverage.comments.scanner.utils.github;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.model.github.DirectoryGHContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for processing Java GitHub repository's contents.
 */
public class JavaGithubUtils {

    /**
     * Retrieves the list of {@link DirectoryGHContent} objects representing the contents of the specified GitHub repository.
     *
     * @param repository GitHub repository for which to retrieve the content.
     * @return A list of {@link DirectoryGHContent} representing the contents of the GitHub repository.
     */
    public static List<DirectoryGHContent> getDirectoryGHContentList(GHRepository repository) {
        List<DirectoryGHContent> directoryGHContentList = new ArrayList<>();
        getDirectoryGHContentRecursive(repository, "/", directoryGHContentList, repository.getName());
        return getFilteredGHContentList(directoryGHContentList);
    }

    /**
     * Recursively retrieves the {@link DirectoryGHContent} objects for the specified GitHub repository path and its subdirectories.
     *
     * @param repository GitHub repository from which to retrieve the content.
     * @param path       Path of the current directory to process recursively.
     * @param resultList List to which the retrieved {@link DirectoryGHContent} objects will be added.
     * @param name Name of the directory
     */
    private static void getDirectoryGHContentRecursive(
            GHRepository repository, String path, List<DirectoryGHContent> resultList, String name) {
        DirectoryGHContent result = getDirectoryGHContentForPath(repository, path, name);
        resultList.add(result);

        for (GHContent subDirectory : result.getSubDirectories()) {
            getDirectoryGHContentRecursive(repository, subDirectory.getPath(), resultList, subDirectory.getName());
        }
    }

    /**
     * Retrieves the {@link DirectoryGHContent} object for the specified GitHub repository path.
     *
     * @param repository The GitHub repository from which to retrieve the content.
     * @param path       The path of the directory for which to retrieve the content.
     * @param name       Name of the directory
     * @return {@link DirectoryGHContent} list representing the contents of the specified directory.
     * @throws InternalServerError I/O error while retrieving the directory content from the repository.
     */
    private static DirectoryGHContent getDirectoryGHContentForPath(GHRepository repository, String path, String name) {
        try {
            List<GHContent> directoryContents = repository.getDirectoryContent(path);

            List<GHContent> subDirectories = directoryContents.stream()
                    .filter(GHContent::isDirectory)
                    .toList();

            List<GHContent> directoryFiles = directoryContents.stream()
                    .filter(GHContent::isFile)
                    .toList();

            return new DirectoryGHContent(path, name, subDirectories, directoryFiles);
        } catch (IOException ioException) {
            throw new InternalServerError("Error while getting directory content for path "
                    + path + ": " + ioException.getMessage());
        }
    }

    /**
     * Filters the list of {@link DirectoryGHContent} objects to exclude certain paths and empty directories.
     *
     * @param directoryGHContentList Original list of {@link DirectoryGHContent} objects to be filtered.
     * @return Filtered list of {@link DirectoryGHContent} objects.
     */
    private static List<DirectoryGHContent> getFilteredGHContentList(List<DirectoryGHContent> directoryGHContentList) {
        return directoryGHContentList.stream()
                .filter(content -> !content.getPath().contains("gradle"))
                .filter(content -> !content.getPath().contains("src/test"))
                .filter(content -> !content.getPath().contains("main/resources"))
                .filter(content -> !content.getPath().equals("/"))
                .filter(content -> !content.getDirectoryFiles().isEmpty())
                .toList();
    }
}
