package uos.msc.project.documentation.coverage.comments.scanner.entity;

import lombok.Data;

/**
 * Entity class mapping to a document for the Project Collection in Firebase
 */
@Data
public class ProjectEntity {

    /**
     * ID of the document
     */
    private String id;
    /**
     * Personal Access Token for the gitHub repository encoded.
     */
    private String encodedAccessToken;
    /**
     * Repository owner's name
     */
    private String owner;
    /**
     * Name of the repository
     */
    private String repository;
    /**
     * ID of the logged-in user
     */
    private String userId;
    /**
     * Coding Language of the repository
     */
    private String language;
}
