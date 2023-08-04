package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FieldDeclarationsUtils {

    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {

        List<FieldDeclaration> fieldDeclarations = compilationUnit.findAll(FieldDeclaration.class);
        List<ParsingInfo> parsingInfoList = new ArrayList<>();

        for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
            // Since fields can be declared with multiple variables separated by commas,we'll check each variable separately.
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
                parsingInfoList.add(processVariableDeclaration(variableDeclarator));
            }
        }
        return parsingInfoList;
    }

    private static ParsingInfo processVariableDeclaration(VariableDeclarator variableDeclarator) {

        Optional<Comment> optionalComment = variableDeclarator.getParentNode().flatMap(Node::getComment);

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Variable");
        parsingInfo.setName(variableDeclarator.getNameAsString());
        variableDeclarator.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);

            parsingInfo.setJavadocDescription(javadocInfo.getDescription());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            if (parsingInfo.getJavadocDescription().isEmpty()) {
                parsingInfo.setDocumentationReviewComments(List.of("The variable Javadoc does not have a description. Expecting a description"));
                parsingInfo.setJavadocScore(0);
            } else {
                parsingInfo.setJavadocScore(1);
            }
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing Javadoc for the variable"));
            parsingInfo.setJavadocScore(0);
        }

        return parsingInfo;
    }
}
