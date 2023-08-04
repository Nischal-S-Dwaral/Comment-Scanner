package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.List;
import java.util.Optional;

public class PackageDeclarationUtils {

    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {
        Optional<PackageDeclaration> optionalPackageDeclaration = compilationUnit.getPackageDeclaration();

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("PackageInfo");

        optionalPackageDeclaration.ifPresent(packageDeclaration -> {
            parsingInfo.setName(packageDeclaration.getNameAsString());
            packageDeclaration.getRange().ifPresent(range -> {
                parsingInfo.setStartLine(range.begin.line);
                parsingInfo.setEndLine(range.end.line);
            });

            Optional<Comment> optionalComment = packageDeclaration.getParentNode().flatMap(Node::getComment);

            if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
                JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);

                parsingInfo.setJavadocDescription(javadocInfo.getDescription());
                parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
                parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

                if (parsingInfo.getJavadocDescription().isEmpty()) {
                    parsingInfo.setDocumentationReviewComments(List.of("The package declaration Javadoc does not have a description. Expecting a description"));
                    parsingInfo.setJavadocScore(0);
                } else {
                    parsingInfo.setDocumentationReviewComments(List.of("Good Javadoc comments for the package declaration"));
                    parsingInfo.setJavadocScore(1);
                }
            } else {
                parsingInfo.setDocumentationReviewComments(List.of("Missing Javadoc for the package declaration"));
                parsingInfo.setJavadocScore(0);
            }
        });

        return List.of(parsingInfo);
    }
}
