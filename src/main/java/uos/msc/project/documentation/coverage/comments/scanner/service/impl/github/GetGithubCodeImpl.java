package uos.msc.project.documentation.coverage.comments.scanner.service.impl.github;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Component;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.BadRequest;
import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;
import uos.msc.project.documentation.coverage.comments.scanner.utils.EncryptionUtils;

import java.io.IOException;

/**
 * Component class to get the data from GitHub repository
 */
@Component
public class GetGithubCodeImpl {

    /**
     * @param owner Owner of the repository
     * @param repositoryName Name of the repository
     * @param encodedAccessToken Personal Access Token for the GitHub repository encoded
     * @return GitHub Repository data
     * @throws InternalServerError I/O error while getting data from GitHub repository
     */
    public GHRepository getGHRepository(String owner, String repositoryName, String encodedAccessToken) {
        try {
            String decryptedAccessToken = EncryptionUtils.decrypt(encodedAccessToken, "123456789");
            GitHub github = GitHub.connectUsingOAuth(decryptedAccessToken);
            return github.getRepository(owner + "/" + repositoryName);
        } catch (IOException ioException) {
            throw new BadRequest("Couldn't get Data from GitHub: " + ioException.getMessage());
        }
    }
}
