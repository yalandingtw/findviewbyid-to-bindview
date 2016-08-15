import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import model.BodyDeclareParseResult;
import model.ParseResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/8/4.
 */
public class FindViewByIdToBindView extends BaseGenerateAction {
    protected PsiClass mClass;
    private ArrayList<PsiMethodCallExpression> findbyIdExpressions;
    private HashMap<PsiElement, String[]> blockParamsKey;
    private ArrayList<ParseResult> parseResults;

    public FindViewByIdToBindView() {
        super(null);
    }

    public FindViewByIdToBindView(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(PsiClass targetClass) {
        return super.isValidForClass(targetClass) && !(targetClass instanceof PsiAnonymousClass);
    }

    @Override
    public void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        mClass = getTargetClass(editor, file);

        parseResults = new ArrayList<>();

        PsiMethod[] allMethods = mClass.getAllMethods();
        findbyIdExpressions = new ArrayList<>();
        for (PsiMethod allMethod : allMethods) {
            //全方法
            if (allMethod.getBody() != null) {
                System.out.print("========================" + allMethod.getName() + "========================\n");
                PsiStatement[] statements = allMethod.getBody().getStatements();
                blockParamsKey = new HashMap<>();
                for (PsiStatement statement : statements) {
                    //每個指令以;為單位
                    if (statement.getText().contains("findViewById(R.id.")) {
                        //不是方法內宣告
                        parseStatement(mClass, allMethod, statement);
                    } else {
                        if (statement instanceof PsiDeclarationStatement) {
                            PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) statement;
                            if (Parser.isOnlyDeclare(declarationStatement)) {
                                //宣告變數 先存起來
                                String[] declareTypeAndName = Parser.getDeclareTypeAndName(declarationStatement);
                                blockParamsKey.put(declarationStatement, declareTypeAndName);//參數名稱,型別
                            }
                        }
                    }
                }
                System.out.print("====================================================\n");
            }
        }

        new Writer(project, mClass, "WriteBindView", parseResults, blockParamsKey).execute();

    }

    public void parseStatement(PsiClass targetClass, PsiMethod method, PsiStatement statement) {
        System.out.print(statement.getText() + "\n");

        if (statement instanceof PsiExpressionStatement) {
            PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
            if (expression instanceof PsiAssignmentExpression) {
                //或許是全域;
                PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) expression;
                ParseResult parseResult = Parser.parsePsiAssignmentExpression(method, assignmentExpression, blockParamsKey);
                if (parseResult != null) {
                    parseResults.add(parseResult);
                    System.out.printf(parseResult.toString());
                }
            } else if (expression instanceof PsiMethodCallExpression) {

                //findViewById(R.id.).set....

//                PsiMethodCallExpression psiMethodCallExpression = (PsiMethodCallExpression) expression;
//                ParseResult parseResult = Parser.parsePsiMethodCallExpression(method, psiMethodCallExpression);
//                parseResults.add(parseResult);

//                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
//                PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
//                PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
//                if (qualifierExpression instanceof PsiMethodCallExpression) {
//                    PsiMethodCallExpression qualifierExpression1 = (PsiMethodCallExpression) qualifierExpression;
//                    System.out.print("整行:" + qualifierExpression1.getText() + "\n");
//                    findbyIdExpressions.add(qualifierExpression1);
//                }
            }
        } else if (statement instanceof PsiDeclarationStatement) {

            PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) statement;
            BodyDeclareParseResult bodyDeclareParseResult = Parser.parsePsiDeclarationStatement(method, declarationStatement);
            if (bodyDeclareParseResult != null) {
                System.out.printf(bodyDeclareParseResult.toString());
                parseResults.add(bodyDeclareParseResult);
            }
        }
    }

    @Override
    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return super.isValidForFile(project, editor, file);
    }
}
