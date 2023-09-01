package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
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
import uos.msc.project.documentation.coverage.comments.scanner.model.java.parse.ParsingInfo;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.CommonJavadocUtils;
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.MethodDeclarationsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MethodDeclarationsUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private MethodDeclaration methodDeclarationMock;

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
    void testParse_NoMethod() {
        when(compilationUnitMock.findAll(MethodDeclaration.class)).thenReturn(new ArrayList<>());
        List<ParsingInfo> parsingInfoList = MethodDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertTrue(parsingInfoList.isEmpty());
    }

    @Test
    void testParse_MethodWithoutJavadoc() {
        when(compilationUnitMock.findAll(MethodDeclaration.class)).thenReturn(List.of(methodDeclarationMock));
        when(methodDeclarationMock.getComment()).thenReturn(Optional.empty());

        List<ParsingInfo> parsingInfoList = MethodDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Method", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
        Assertions.assertEquals("Missing documentation for method", parsingInfo.getDocumentationReviewComments().get(0));
    }
}
