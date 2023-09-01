package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.javadoc.Javadoc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.IntermediateValidationResult;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.CommonJavadocUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.ConstructorDeclarationsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConstructorDeclarationsUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private ConstructorDeclaration constructorDeclarationMock;

    MockedStatic<CommonJavadocUtils> commonJavadocUtilsMockedStatic;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        commonJavadocUtilsMockedStatic = Mockito.mockStatic(CommonJavadocUtils.class);
    }

    @AfterEach
    void destroy() {
        commonJavadocUtilsMockedStatic.close();
    }

    @Test
    void testParse_NoConstructors() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(new ArrayList<>());
        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertTrue(parsingInfoList.isEmpty());
    }

    @Test
    void testParse_ConstructorWithJavadocNoParameters() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(List.of(constructorDeclarationMock));
        when(constructorDeclarationMock.getNameAsString()).thenReturn("MyConstructor");

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(constructorDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This constructor creates a new object with default values.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        IntermediateValidationResult intermediateValidationResultMock = new IntermediateValidationResult(0.0, new ArrayList<>());
        Javadoc javadocMock = mock(Javadoc.class);

        Mockito.when(javadocCommentMock.parse()).thenReturn(javadocMock);
        Mockito.when(javadocCommentMock.parse().getBlockTags()).thenReturn(new ArrayList<>());
        Mockito.when(constructorDeclarationMock.getParameters()).thenReturn(new NodeList<>());

        when(CommonJavadocUtils.validateParametersDocumentation(
                anyList(), any())).thenReturn(intermediateValidationResultMock);


        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertNotEquals(parsingInfoList.size(), 0);
        Assertions.assertEquals(parsingInfoList.get(0).getJavadocScore(), 1.0);
    }

    @Test
    void testParse_ConstructorWithOutJavadocNoParameters() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(List.of(constructorDeclarationMock));
        when(constructorDeclarationMock.getNameAsString()).thenReturn("MyConstructor");

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(constructorDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        IntermediateValidationResult intermediateValidationResultMock = new IntermediateValidationResult(0.0, new ArrayList<>());
        Javadoc javadocMock = mock(Javadoc.class);

        Mockito.when(javadocCommentMock.parse()).thenReturn(javadocMock);
        Mockito.when(javadocCommentMock.parse().getBlockTags()).thenReturn(new ArrayList<>());
        Mockito.when(constructorDeclarationMock.getParameters()).thenReturn(new NodeList<>());

        when(CommonJavadocUtils.validateParametersDocumentation(
                anyList(), any())).thenReturn(intermediateValidationResultMock);


        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertNotEquals(parsingInfoList.size(), 0);
        Assertions.assertEquals(parsingInfoList.get(0).getJavadocScore(), 0.0);
    }

    @Test
    void testParse_ParametersConstructorWithJavadoc() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(List.of(constructorDeclarationMock));
        when(constructorDeclarationMock.getNameAsString()).thenReturn("MyConstructor");

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(constructorDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This constructor creates a new object with default values.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        IntermediateValidationResult intermediateValidationResultMock = new IntermediateValidationResult(1.0, new ArrayList<>());
        Javadoc javadocMock = mock(Javadoc.class);

        Mockito.when(javadocCommentMock.parse()).thenReturn(javadocMock);
        Mockito.when(javadocCommentMock.parse().getBlockTags()).thenReturn(new ArrayList<>());

        NodeList parameterNodeListMock = mock(NodeList.class);
        Mockito.when(constructorDeclarationMock.getParameters()).thenReturn(parameterNodeListMock);

        when(CommonJavadocUtils.validateParametersDocumentation(
                anyList(), any())).thenReturn(intermediateValidationResultMock);


        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertNotEquals(parsingInfoList.size(), 0);
        Assertions.assertEquals(parsingInfoList.get(0).getJavadocScore(), 1.0);
    }

    @Test
    void testParse_ParametersConstructorWithoutParameterJavadoc() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(List.of(constructorDeclarationMock));
        when(constructorDeclarationMock.getNameAsString()).thenReturn("MyConstructor");

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(constructorDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This constructor creates a new object with default values.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        IntermediateValidationResult intermediateValidationResultMock = new IntermediateValidationResult(0.0, Arrays.asList("Missing @param tag for parameter: a"));
        Javadoc javadocMock = mock(Javadoc.class);

        Mockito.when(javadocCommentMock.parse()).thenReturn(javadocMock);
        Mockito.when(javadocCommentMock.parse().getBlockTags()).thenReturn(new ArrayList<>());

        NodeList parameterNodeListMock = mock(NodeList.class);
        Mockito.when(constructorDeclarationMock.getParameters()).thenReturn(parameterNodeListMock);

        when(CommonJavadocUtils.validateParametersDocumentation(
                anyList(), any())).thenReturn(intermediateValidationResultMock);


        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertNotEquals(parsingInfoList.size(), 0);
        Assertions.assertEquals(parsingInfoList.get(0).getJavadocScore(), 0.5);
    }

    @Test
    void testParse_ConstructorWithoutJavadoc() {
        when(compilationUnitMock.findAll(ConstructorDeclaration.class)).thenReturn(List.of(constructorDeclarationMock));
        when(constructorDeclarationMock.getNameAsString()).thenReturn("MyConstructor");

        LineComment lineCommentMock = mock(LineComment.class);
        Optional<Comment> optionalComment = Optional.of(lineCommentMock);
        when(constructorDeclarationMock.getComment()).thenReturn(optionalComment);

        List<ParsingInfo> parsingInfoList = ConstructorDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertNotEquals(parsingInfoList.size(), 0);
        Assertions.assertEquals(parsingInfoList.get(0).getJavadocScore(), 0.0);
        Assertions.assertEquals(parsingInfoList.get(0).getDocumentationReviewComments().get(0),"Missing documentation for constructor");
    }
}
