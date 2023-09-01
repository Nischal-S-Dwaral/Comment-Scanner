package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
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
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.PackageDeclarationUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PackageDeclarationUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private PackageDeclaration packageDeclarationMock;

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
    void testParse_NoPackageDeclaration() {
        when(compilationUnitMock.getPackageDeclaration()).thenReturn(Optional.empty());
        List<ParsingInfo> parsingInfoList = PackageDeclarationUtils.parse(compilationUnitMock);
        Assertions.assertEquals(parsingInfoList.size(), 0);
    }

    @Test
    void testParse_PackageDeclarationWithoutJavadoc() {

        when(compilationUnitMock.getPackageDeclaration()).thenReturn(Optional.of(packageDeclarationMock));
        when(packageDeclarationMock.getParentNode().flatMap(Node::getComment)).thenReturn(Optional.empty());

        List<ParsingInfo> parsingInfoList = PackageDeclarationUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);

        Assertions.assertEquals("PackageInfo", parsingInfo.getType());
        Assertions.assertEquals(0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("Missing Javadoc for the package declaration", parsingInfo.getDocumentationReviewComments().get(0));
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_PackageDeclarationWithGoodJavadoc() {
        when(compilationUnitMock.getPackageDeclaration()).thenReturn(Optional.of(packageDeclarationMock));

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(packageDeclarationMock.getParentNode()).thenReturn(Optional.of(javadocCommentMock));
        when(packageDeclarationMock.getParentNode().flatMap(Node::getComment)).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This package is for service implementation.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = PackageDeclarationUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("PackageInfo", parsingInfo.getType());
        Assertions.assertEquals(1.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_PackageDeclarationWithBadJavadoc() {
        when(compilationUnitMock.getPackageDeclaration()).thenReturn(Optional.of(packageDeclarationMock));

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(packageDeclarationMock.getParentNode()).thenReturn(Optional.of(javadocCommentMock));
        when(packageDeclarationMock.getParentNode().flatMap(Node::getComment)).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = PackageDeclarationUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("PackageInfo", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("The package declaration Javadoc does not have a description. Expecting a description", parsingInfo.getDocumentationReviewComments().get(0));
    }
}
