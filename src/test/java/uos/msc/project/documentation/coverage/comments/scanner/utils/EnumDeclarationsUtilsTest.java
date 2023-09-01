package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
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
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.JavadocInfo;
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.CommonJavadocUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.EnumDeclarationsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnumDeclarationsUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private EnumDeclaration enumDeclarationMock;

    @Mock
    private EnumConstantDeclaration enumConstantDeclarationMock;

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
    void testParse_NoEnum() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(new ArrayList<>());
        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertTrue(parsingInfoList.isEmpty());
    }

    @Test
    void testParse_EnumWithoutJavadoc() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        when(enumDeclarationMock.getComment()).thenReturn(Optional.empty());
        when(enumDeclarationMock.getEntries()).thenReturn(new NodeList<>());

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Enum", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("Missing documentation for Enum", parsingInfo.getDocumentationReviewComments().get(0));
    }

    @Test
    void testParse_EnumWithGoodJavadoc_Description() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        when(enumDeclarationMock.getEntries()).thenReturn(new NodeList<>());

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(enumDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This enum is for service enums.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Enum", parsingInfo.getType());
        Assertions.assertEquals(1.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_EnumWithBadJavadoc_EmptyDescription() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        when(enumDeclarationMock.getEntries()).thenReturn(new NodeList<>());

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(enumDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(0);
        javadocInfoMock.setEndLine(0);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Enum", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("The Enum Javadoc does not have a description. Expecting a description", parsingInfo.getDocumentationReviewComments().get(0));
    }

    @Test
    void testParse_EnumConstantDeclarationNodeWithoutJavadoc() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(enumDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This enum is for service enums.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        NodeList<EnumConstantDeclaration> enumConstantDeclarationNodeList = new NodeList<>();
        enumConstantDeclarationNodeList.add(enumConstantDeclarationMock);
        when(enumDeclarationMock.getEntries()).thenReturn(enumConstantDeclarationNodeList);

        when(enumConstantDeclarationMock.getComment()).thenReturn(Optional.empty());

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(2, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(1);
        Assertions.assertEquals("Enum Constant", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("Missing documentation for Enum constant", parsingInfo.getDocumentationReviewComments().get(0));
    }

    @Test
    void testParse_EnumConstantDeclarationNodeWithGoodJavadoc_Description() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(enumDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This enum is for service enums.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        NodeList<EnumConstantDeclaration> enumConstantDeclarationNodeList = new NodeList<>();
        enumConstantDeclarationNodeList.add(enumConstantDeclarationMock);
        when(enumDeclarationMock.getEntries()).thenReturn(enumConstantDeclarationNodeList);

        when(enumConstantDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(2, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(1);
        Assertions.assertEquals("Enum Constant", parsingInfo.getType());
        Assertions.assertEquals(1.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_EnumConstantDeclarationNodeWithBadJavadoc_EmptyDescription() {
        when(compilationUnitMock.findAll(EnumDeclaration.class)).thenReturn(List.of(enumDeclarationMock));
        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(enumDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        NodeList<EnumConstantDeclaration> enumConstantDeclarationNodeList = new NodeList<>();
        enumConstantDeclarationNodeList.add(enumConstantDeclarationMock);
        when(enumDeclarationMock.getEntries()).thenReturn(enumConstantDeclarationNodeList);

        when(enumConstantDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        List<ParsingInfo> parsingInfoList = EnumDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(2, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(1);
        Assertions.assertEquals("Enum Constant", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("The Enum constant Javadoc does not have a description. Expecting a description",
                parsingInfo.getDocumentationReviewComments().get(0));
    }
}
