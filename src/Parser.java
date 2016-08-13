/**
 * Copyright (C) 2016 Alan Ding
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.intellij.psi.*;
import model.BodyDeclareParseResult;
import model.BodyPreDeclareParseResult;
import model.GlobalDeclareParseResult;
import model.ParseResult;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Alan Ding on 2016/8/5.
 */
public class Parser {

    public static ParseResult parsePsiAssignmentExpression(PsiMethod method, PsiAssignmentExpression assignmentExpression, HashMap<PsiElement, String[]> blockParamsKey) {

        boolean isDeclareGlobal = false;
        String viewType = "";
        String layoutId = "";
        ParseResult result = null;

        if (assignmentExpression.getRExpression() instanceof PsiTypeCastExpression) {

            PsiTypeCastExpression rExpression = (PsiTypeCastExpression) assignmentExpression.getRExpression();
            viewType = rExpression.getCastType().getText();
            Iterator<PsiElement> iterator = blockParamsKey.keySet().iterator();
            while (iterator.hasNext()) {
                PsiElement psiElement = iterator.next();
                String[] values = blockParamsKey.get(psiElement);
                System.out.print("比對區域宣告:" + psiElement.getText() + "\n");

                isDeclareGlobal = viewType.equals(values[0]) && assignmentExpression.getLExpression().getText().equals(values[1]);

                if (isDeclareGlobal) {
                    result = new BodyPreDeclareParseResult(method.getName(), assignmentExpression, psiElement);
                    iterator.remove();
                    break;
                }
            }

            if (!isDeclareGlobal) {
                result = new GlobalDeclareParseResult(method.getName(), assignmentExpression);
            }
            result.setParamsKey(assignmentExpression.getLExpression().getText());
            result.setViewType(viewType);
            if (rExpression.getOperand() instanceof PsiMethodCallExpression && rExpression.getOperand().getText().contains("findViewById")) {
                PsiMethodCallExpression operand = (PsiMethodCallExpression) rExpression.getOperand();
                PsiExpression[] expressions = operand.getArgumentList().getExpressions();
                if (expressions.length == 1) {
                    PsiExpression expression = expressions[0];
                    layoutId = expression.getText();
                    result.setLayoutId(layoutId);
                }
            }

        }
        return result;
    }

    /**
     * 取得宣告型別以及參數名稱
     *
     * @param declarationStatement
     * @return
     */
    public static String[] getDeclareTypeAndName(PsiDeclarationStatement declarationStatement) {
        String[] result = new String[]{"", ""};
        PsiElement firstChild = declarationStatement.getFirstChild();
        if (firstChild instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) firstChild;
            PsiTypeElement viewType = psiLocalVariable.getTypeElement();
            PsiIdentifier nameIdentifier = psiLocalVariable.getNameIdentifier();
            result[0] = viewType.getText();
            result[1] = nameIdentifier.getText();
        }
        return result;
    }

    public static boolean isOnlyDeclare(PsiDeclarationStatement declarationStatement) {
        PsiElement firstChild = declarationStatement.getFirstChild();
        if (firstChild instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) firstChild;
            if (psiLocalVariable.getInitializer() != null) {
                PsiExpression initializer = psiLocalVariable.getInitializer();
                return initializer instanceof PsiLiteralExpression;
            } else {
                return true;
            }
        }
        return true;
    }

    public static BodyDeclareParseResult parsePsiDeclarationStatement(PsiMethod method, PsiDeclarationStatement declarationStatement) {
        BodyDeclareParseResult result = new BodyDeclareParseResult(method.getName(), declarationStatement);

        PsiElement firstChild = declarationStatement.getFirstChild();
        if (firstChild instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) firstChild;
            PsiTypeElement viewType = psiLocalVariable.getTypeElement();
            PsiIdentifier nameIdentifier = psiLocalVariable.getNameIdentifier();

            PsiExpression initializer = psiLocalVariable.getInitializer();
            if (initializer instanceof PsiTypeCastExpression) {
                PsiTypeCastExpression typeCastExpression = (PsiTypeCastExpression) initializer;
                PsiExpression operand = typeCastExpression.getOperand();
                if (operand instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) operand;
                    PsiExpression[] expressions = methodCallExpression.getArgumentList().getExpressions();
                    if (expressions.length == 1) {
                        result.setLayoutId(expressions[0].getText());
                        result.setViewType(viewType.getText());
                        result.setParamsKey(nameIdentifier.getText());
                    }
                }

            }
        }
        return result;
    }

    public static boolean equalsFieldByText(PsiClass mClazz, String text) {
        PsiField[] allFields = mClazz.getAllFields();
        for (PsiField field : allFields) {
            if (field.getText().equals(text)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBindViewField(PsiField field) {
        return field.getText().contains("@BindView");
    }

//    public static ParseResult parsePsiMethodCallExpression(PsiMethod method, PsiMethodCallExpression psiMethodCallExpression) {
//
//    }
}
