package uos.msc.project.documentation.coverage.comments.scanner.utils;

import java.util.Objects;

public class CommonUtils {

    public static <T> boolean checkIfObjectIsNotNull(T offer) {
        return Objects.nonNull(offer);
    }
}
