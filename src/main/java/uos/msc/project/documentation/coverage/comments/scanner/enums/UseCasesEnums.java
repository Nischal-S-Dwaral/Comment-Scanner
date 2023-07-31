package uos.msc.project.documentation.coverage.comments.scanner.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing different use cases.
 */
public enum UseCasesEnums {

    /**
     * Represents the ADD_SPRING_BOOT use case.
     */
    ADD_SPRING_BOOT("addSpringBoot");

    /**
     * The name of the use case.
     */
    private final String useCaseName;

    /**
     * A map that stores the string representation of the use case as the key and the corresponding enum value as the value.
     */
    private static final Map<String, UseCasesEnums> stringUseCasesEnumsMap = new HashMap<>();

    /**
     * Constructs a UseCasesEnums object with the specified use case name.
     *
     * @param useCaseName the name of the use case.
     */
    UseCasesEnums(final String useCaseName) {
        this.useCaseName = useCaseName;
    }

    static {
        for (UseCasesEnums enums : values()) {
            stringUseCasesEnumsMap.put(enums.useCaseName, enums);
        }
    }

    /**
     * Returns the UseCasesEnums enum value corresponding to the given string.
     *
     * @param val the string value to match.
     * @return the corresponding UseCasesEnums enum value, or null if not found.
     */
    public static UseCasesEnums getEnumByString(final String val) {
        return stringUseCasesEnumsMap.get(val);
    }

    /**
     * @return the name of the use case.
     */
    public String getUseCaseName() {
        return useCaseName;
    }
}