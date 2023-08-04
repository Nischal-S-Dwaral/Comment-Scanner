package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnumDeclarationsUtils {

    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {

        List<EnumDeclaration> enumDeclarations = compilationUnit.findAll(EnumDeclaration.class);
        List<ParsingInfo> parsingInfoList = new ArrayList<>();

        for (EnumDeclaration enumDeclaration : enumDeclarations) {
            parsingInfoList.addAll(processEnumDeclaration(enumDeclaration));
        }

        return parsingInfoList;
    }

    private static List<ParsingInfo> processEnumDeclaration(EnumDeclaration enumDeclaration) {

        List<ParsingInfo> parsedInfoEnums = new ArrayList<>();
        Optional<Comment> optionalComment = enumDeclaration.getComment();

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Enum");
        parsingInfo.setName(enumDeclaration.getNameAsString());
        enumDeclaration.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);

            parsingInfo.setJavadocDescription(javadocInfo.getDescription());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            if (parsingInfo.getJavadocDescription().isEmpty()) {
                parsingInfo.setDocumentationReviewComments(List.of("The Enum Javadoc does not have a description. Expecting a description"));
                parsingInfo.setJavadocScore(0);
            } else {
                parsingInfo.setJavadocScore(1);
            }
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing documentation for Enum"));
            parsingInfo.setJavadocScore(0);
        }

        parsedInfoEnums.add(parsingInfo);

        // Verify each enum constant has documentation
        NodeList<EnumConstantDeclaration> enumConstantDeclarationNodeList = enumDeclaration.getEntries();
        for (EnumConstantDeclaration enumConstantDeclarationNode : enumConstantDeclarationNodeList) {
            parsedInfoEnums.add(processEnumConstantDeclarationNode(enumConstantDeclarationNode));
        }

        return parsedInfoEnums;
    }

    private static ParsingInfo processEnumConstantDeclarationNode(EnumConstantDeclaration enumConstantDeclarationNode) {

        Optional<Comment> optionalComment = enumConstantDeclarationNode.getComment();

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Enum Constant");
        parsingInfo.setName(enumConstantDeclarationNode.getNameAsString());
        enumConstantDeclarationNode.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);

            parsingInfo.setJavadocDescription(javadocInfo.getDescription());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            if (parsingInfo.getJavadocDescription().isEmpty()) {
                parsingInfo.setDocumentationReviewComments(List.of("The Enum constant Javadoc does not have a description. Expecting a description"));
                parsingInfo.setJavadocScore(0);
            } else {
                parsingInfo.setDocumentationReviewComments(List.of("Good Javadoc comment for the Enum constant"));
                parsingInfo.setJavadocScore(1);
            }
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing documentation for Enum constant"));
            parsingInfo.setJavadocScore(0);
        }

        return parsingInfo;
    }
}
