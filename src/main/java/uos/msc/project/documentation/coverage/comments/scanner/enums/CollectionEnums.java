package uos.msc.project.documentation.coverage.comments.scanner.enums;

/**
 * Enum representing different firebase collections
 */
public enum CollectionEnums {

    /**
     * Represents the ProjectEntity's collection
     */
    PROJECT("project"),
    /**
     * Represents the DirectoryEntity's collection
     */
    DIRECTORY("directory"),
    /**
     * Represents the FileEntity's collection
     */
    FILE("file"),
    /**
     * Represents the CodeLineEntity's collection
     */
    CODE_LINE("codeLine"),
    /**
     * Represents the SummaryEntity's collection
     */
    SUMMARY("summary");

    /**
     * The name of the collection.
     */
    private final String collection;

    /**
     * Constructs a CollectionEnums object with the specified collection name.
     * @param collection the name of the collection
     */
    CollectionEnums(String collection) {
        this.collection = collection;
    }

    /**
     * @return the name of the collection
     */
    public String getCollection() {
        return collection;
    }
}
