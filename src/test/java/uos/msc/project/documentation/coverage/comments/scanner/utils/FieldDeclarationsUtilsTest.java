package uos.msc.project.documentation.coverage.comments.scanner.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
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
import uos.msc.project.documentation.coverage.comments.scanner.utils.java.FieldDeclarationsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldDeclarationsUtilsTest {

    @Mock
    private CompilationUnit compilationUnitMock;

    @Mock
    private FieldDeclaration fieldDeclarationMock;

    @Mock
    private VariableDeclarator variableDeclaratorMock;

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
    void testParse_NoFields() {
        when(compilationUnitMock.findAll(FieldDeclaration.class)).thenReturn(new ArrayList<>());
        List<ParsingInfo> parsingInfoList = FieldDeclarationsUtils.parse(compilationUnitMock);
        Assertions.assertTrue(parsingInfoList.isEmpty());
    }

    @Test
    void testParse_FieldWithoutJavadoc() {

        when(compilationUnitMock.findAll(FieldDeclaration.class)).thenReturn(List.of(fieldDeclarationMock));
        NodeList<VariableDeclarator> variableDeclarators = new NodeList<>();
        variableDeclarators.add(mock(VariableDeclarator.class));
        when(fieldDeclarationMock.getVariables()).thenReturn(variableDeclarators);

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(0);
        javadocInfoMock.setEndLine(0);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = FieldDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Variable", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_FieldWithGoodJavadoc() {
        when(compilationUnitMock.findAll(FieldDeclaration.class)).thenReturn(List.of(fieldDeclarationMock));
        NodeList<VariableDeclarator> variableDeclarators = new NodeList<>();
        variableDeclarators.add(variableDeclaratorMock);
        when(variableDeclaratorMock.getParentNode()).thenReturn(Optional.of(fieldDeclarationMock));
        when(fieldDeclarationMock.getVariables()).thenReturn(variableDeclarators);

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(variableDeclaratorMock.getParentNode().flatMap(Node::getComment)).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("This field is to store values of the project id.");
        javadocInfoMock.setStartLine(1);
        javadocInfoMock.setEndLine(2);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = FieldDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Variable", parsingInfo.getType());
        Assertions.assertEquals(1.0, parsingInfo.getJavadocScore());
    }

    @Test
    void testParse_FieldWithBadJavadoc() {
        when(compilationUnitMock.findAll(FieldDeclaration.class)).thenReturn(List.of(fieldDeclarationMock));
        NodeList<VariableDeclarator> variableDeclarators = new NodeList<>();
        variableDeclarators.add(variableDeclaratorMock);
        when(variableDeclaratorMock.getParentNode()).thenReturn(Optional.of(fieldDeclarationMock));
        when(fieldDeclarationMock.getVariables()).thenReturn(variableDeclarators);

        JavadocComment javadocCommentMock = mock(JavadocComment.class);
        when(variableDeclaratorMock.getParentNode().flatMap(Node::getComment)).thenReturn(Optional.of(javadocCommentMock));

        JavadocInfo javadocInfoMock = new JavadocInfo();
        javadocInfoMock.setDescription("");
        javadocInfoMock.setStartLine(0);
        javadocInfoMock.setEndLine(0);

        commonJavadocUtilsMockedStatic.when(() -> CommonJavadocUtils.extractJavadocInfo(any(JavadocComment.class)))
                .thenReturn(javadocInfoMock);

        List<ParsingInfo> parsingInfoList = FieldDeclarationsUtils.parse(compilationUnitMock);

        Assertions.assertEquals(1, parsingInfoList.size());
        ParsingInfo parsingInfo = parsingInfoList.get(0);
        Assertions.assertEquals("Variable", parsingInfo.getType());
        Assertions.assertEquals(0.0, parsingInfo.getJavadocScore());
    }
}
