package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassOrInterfaceDeclarationsUtils {

    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {

        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarationList = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        List<ParsingInfo> parsingInfoList = new ArrayList<>();

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : classOrInterfaceDeclarationList) {
            parsingInfoList.add(processClassOrInterfaceDeclaration(classOrInterfaceDeclaration));
        }

        return parsingInfoList;
    }

    private static ParsingInfo processClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {

        Optional<Comment> optionalComment = classOrInterfaceDeclaration.getComment();

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Class");
        parsingInfo.setName(classOrInterfaceDeclaration.getNameAsString());
        classOrInterfaceDeclaration.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);
            parsingInfo.setJavadocDescription(javadocInfo.getDescription());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            if (parsingInfo.getJavadocDescription().isEmpty()) {
                parsingInfo.setDocumentationReviewComments(List.of("The class/interface Javadoc does not have a description. Expecting a description"));
                parsingInfo.setJavadocScore(0);
            } else {
                parsingInfo.setJavadocScore(1);
            }
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing documentation for class/interface"));
            parsingInfo.setJavadocScore(0);
        }

        return parsingInfo;
    }
}
