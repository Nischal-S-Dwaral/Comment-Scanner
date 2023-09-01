package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.ClassOrInterfaceDeclarationsUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.CommonJavadocUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClassOrInterfaceDeclarationsUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private ClassOrInterfaceDeclaration classOrInterfaceDeclarationMock;

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
    void testParse_NoClass() {
        when(compilationUnitMock.findAll(ClassOrInterfaceDeclaration.class)).thenReturn(new ArrayList<>());
        List<ParsingInfo> parsingInfoList = ClassOrInterfaceDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertTrue(parsingInfoList.isEmpty());
    }

    @Test
    void testParse_ClassWithoutJavadoc() {
        when(compilationUnitMock.findAll(ClassOrInterfaceDeclaration.class)).thenReturn(List.of(classOrInterfaceDeclarationMock));
        when(classOrInterfaceDeclarationMock.getComment()).thenReturn(Optional.empty());

        List<ParsingInfo> parsingInfoList = ClassOrInterfaceDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Class", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("Missing documentation for class/interface", parsingInfo.getDocumentationReviewComments().get(0));
    }

    @Test
    void testParse_ClassGoodJavadoc_Description() {
        when(compilationUnitMock.findAll(ClassOrInterfaceDeclaration.class)).thenReturn(List.of(classOrInterfaceDeclarationMock));

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(classOrInterfaceDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This class is for service implementation.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = ClassOrInterfaceDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Class", parsingInfo.getType());
        Assertions.assertEquals(1.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_ClassBadJavadoc_EmptyDescription() {
        when(compilationUnitMock.findAll(ClassOrInterfaceDeclaration.class)).thenReturn(List.of(classOrInterfaceDeclarationMock));

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(classOrInterfaceDeclarationMock.getComment()).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(0);
        javadocInfoMock.setEndLine(0);
        when(CommonJavadocUtils.extractJavadocInfo(javadocCommentMock)).thenReturn(javadocInfoMock);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = ClassOrInterfaceDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Class", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("The class/interface Javadoc does not have a description. Expecting a description", parsingInfo.getDocumentationReviewComments().get(0));
    }
}
