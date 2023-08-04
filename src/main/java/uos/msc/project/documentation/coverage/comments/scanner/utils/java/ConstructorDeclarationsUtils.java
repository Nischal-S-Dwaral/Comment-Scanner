package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.IntermediateValidationResult;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstructorDeclarationsUtils {

    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {
        List<ParsingInfo> parsingInfoList = new ArrayList<>();
        List<ConstructorDeclaration> constructorDeclarations = compilationUnit.findAll(ConstructorDeclaration.class);

        for (ConstructorDeclaration constructorDeclaration : constructorDeclarations) {
            parsingInfoList.add(processConstructorDeclaration(constructorDeclaration));
        }

        return parsingInfoList;
    }

    private static ParsingInfo processConstructorDeclaration(ConstructorDeclaration constructorDeclaration) {

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Constructor");
        parsingInfo.setName(constructorDeclaration.getNameAsString());
        constructorDeclaration.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        Optional<Comment> optionalComment = constructorDeclaration.getComment();

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);
            parsingInfo.setJavadocDescription(javadocComment.getContent());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            List<String> reviewComments = new ArrayList<>();
            AtomicInteger score = new AtomicInteger(1);

            if (javadocInfo.getDescription().isEmpty()) {
                reviewComments.add("Missing description for Javadoc of the constructor");
                score.getAndDecrement();
            }

            IntermediateValidationResult intermediateValidationResult = CommonJavadocUtils.validateParametersDocumentation(
                    javadocComment.parse().getBlockTags(),
                    constructorDeclaration.getParameters()
            );
            reviewComments.addAll(intermediateValidationResult.getReviewComment());

            parsingInfo.setDocumentationReviewComments(reviewComments);
            parsingInfo.setJavadocScore(
                    (score.get() + intermediateValidationResult.getScore()) / 2);
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing documentation for constructor"));
            parsingInfo.setJavadocScore(0);
        }

        return parsingInfo;
    }
}
