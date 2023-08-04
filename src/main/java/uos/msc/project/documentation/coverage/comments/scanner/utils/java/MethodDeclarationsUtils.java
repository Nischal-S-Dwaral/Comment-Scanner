package uos.msc.project.documentation.coverage.comments.scanner.utils.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.javadoc.JavadocBlockTag;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.IntermediateValidationResult;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MethodDeclarationsUtils {

    /**
     * //TODO: Add implementation to check description if method has 0 parameters and void return type
     */
    public static List<ParsingInfo> parse(CompilationUnit compilationUnit) {
        List<ParsingInfo> parsingInfoList = new ArrayList<>();
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);

        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            parsingInfoList.add(processMethodDeclaration(methodDeclaration));
        }

        return parsingInfoList;
    }

    private static ParsingInfo processMethodDeclaration(MethodDeclaration methodDeclaration) {

        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setType("Method");
        parsingInfo.setName(methodDeclaration.getNameAsString());
        methodDeclaration.getRange().ifPresent(range -> {
            parsingInfo.setStartLine(range.begin.line);
            parsingInfo.setEndLine(range.end.line);
        });

        Optional<Comment> optionalComment = methodDeclaration.getComment();

        if (optionalComment.isPresent() && optionalComment.get() instanceof JavadocComment javadocComment) {
            JavadocInfo javadocInfo = CommonJavadocUtils.extractJavadocInfo(javadocComment);
            parsingInfo.setJavadocDescription(javadocComment.getContent());
            parsingInfo.setJavadocStartLine(javadocInfo.getStartLine());
            parsingInfo.setJavadocEndLine(javadocInfo.getEndLine());

            List<JavadocBlockTag> javadocBlockTagList = javadocComment.parse().getBlockTags();

            IntermediateValidationResult parametersIntermediateValidationResult = CommonJavadocUtils.validateParametersDocumentation(
                    javadocBlockTagList, methodDeclaration.getParameters());
            List<String> reviewComments = new ArrayList<>(parametersIntermediateValidationResult.getReviewComment());

            IntermediateValidationResult returnIntermediateValidationResult = validateReturnDocumentation(
                    javadocBlockTagList, methodDeclaration.getType().isVoidType());
            reviewComments.addAll(returnIntermediateValidationResult.getReviewComment());

            IntermediateValidationResult throwsIntermediateValidationResult = validateThrowDocumentation(javadocBlockTagList, methodDeclaration);
            reviewComments.addAll(throwsIntermediateValidationResult.getReviewComment());

            double javadocScore = (returnIntermediateValidationResult.getScore()
                    + parametersIntermediateValidationResult.getScore() + throwsIntermediateValidationResult.getScore()) / 3;

            parsingInfo.setDocumentationReviewComments(reviewComments);
            parsingInfo.setJavadocScore(javadocScore);
        } else {
            parsingInfo.setDocumentationReviewComments(List.of("Missing documentation for method"));
            parsingInfo.setJavadocScore(0);
        }

        return parsingInfo;
    }

    private static IntermediateValidationResult validateReturnDocumentation(List<JavadocBlockTag> javadocBlockTagList,
                                                                            boolean isVoidType) {

        List<String> returnDocumentationReviewComments = new ArrayList<>();
        AtomicInteger score = new AtomicInteger();

        boolean hasReturnTag = javadocBlockTagList.stream()
                .anyMatch(tag -> tag != null
                        && tag.getTagName().equals("return"));

        if (isVoidType) {
            if (hasReturnTag) {
                returnDocumentationReviewComments.add("There is no need to add @return tag as return type is void");
            } else {
                score.getAndIncrement();
            }
        } else {
            if (hasReturnTag) {
                boolean isReturnTagDescriptionEmpty = javadocBlockTagList.stream()
                        .anyMatch(tag -> tag != null
                                && tag.getTagName().equals("return")
                                && tag.getContent().isEmpty());

                if (isReturnTagDescriptionEmpty) {
                    returnDocumentationReviewComments.add("Missing @return description for the method");
                } else {
                    score.getAndIncrement();
                }
            } else {
                returnDocumentationReviewComments.add("Missing @return tag for the method");
            }
        }

        return new IntermediateValidationResult(
                score.get(),
                returnDocumentationReviewComments
        );
    }

    private static IntermediateValidationResult validateThrowDocumentation(List<JavadocBlockTag> javadocBlockTagList, MethodDeclaration methodDeclaration) {

        List<String> throwsDocumentationReviewComments = new ArrayList<>();
        AtomicInteger shouldBePresent = new AtomicInteger();
        AtomicInteger isPresent = new AtomicInteger();

        // Check if the method has any "throws" declarations in the method signature
        NodeList<ReferenceType> thrownExceptions = methodDeclaration.getThrownExceptions();
        Set<String> declaredThrows = new HashSet<>();
        for (ReferenceType thrownException : thrownExceptions) {
            declaredThrows.add(thrownException.toString());
        }

        List<String> throwStatements = methodDeclaration.getBody()
                .map(body -> body.findAll(ThrowStmt.class).stream()
                        .map(stmt -> stmt.getExpression().toString())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        javadocBlockTagList.stream()
                .filter(tag -> tag.getTagName().equals("throws"))
                .forEach(tag -> {
                    String exceptionName = tag.getName().orElse("");
                    String exceptionDescription = tag.getContent().toText().trim();

                    if (!declaredThrows.contains(exceptionName)) {
                        throwStatements.removeIf(throwStmt -> throwStmt.contains(exceptionName));
                    } else {
                        declaredThrows.remove(exceptionName);
                    }
                    // Check if the description of the @throws tag is non-empty
                    if (exceptionDescription.isEmpty()) {
                        throwsDocumentationReviewComments.add("Empty description for @throws " + exceptionName);
                    } else {
                        isPresent.getAndIncrement();
                        shouldBePresent.getAndIncrement();
                    }
                });

        throwsDocumentationReviewComments.addAll(
                declaredThrows.stream()
                        .map(exceptionName -> {
                            shouldBePresent.incrementAndGet();
                            return "Missing @throws for " + exceptionName;
                        })
                        .toList()
        );

        throwsDocumentationReviewComments.addAll(
                throwStatements.stream()
                        .map(throwStatement -> {
                            shouldBePresent.incrementAndGet();
                            return "Missing @throws for " + throwStatement;
                        })
                        .toList()
        );

        return new IntermediateValidationResult(
                (shouldBePresent.get() == 0) ? 1.0 : (double) isPresent.get() / shouldBePresent.get(),
                throwsDocumentationReviewComments
        );
    }
}
