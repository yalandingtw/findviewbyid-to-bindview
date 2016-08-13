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

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import model.BodyDeclareParseResult;
import model.BodyPreDeclareParseResult;
import model.GlobalDeclareParseResult;
import model.ParseResult;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alan Ding on 2016/8/5.
 */
public class Writer<T extends ParseResult> extends WriteCommandAction.Simple {
    protected ArrayList<T> parseResults;

    protected PsiElementFactory mFactory;
    protected Project mProject;
    protected PsiClass mClazz;
    protected HashMap<PsiElement, String[]> blockParamsKey;

    public Writer(Project project, PsiClass clazz, String commandName, ArrayList<T> results, HashMap<PsiElement, String[]> blockParamsKey) {
        super(project, commandName);
        mFactory = JavaPsiFacade.getElementFactory(clazz.getProject());
        parseResults = results;
        mProject = project;
        mClazz = clazz;
        this.blockParamsKey = blockParamsKey;
    }

    @Override
    protected void run() throws Throwable {
        for (int i = 0; i < parseResults.size(); i++) {
            generateBindViewField(parseResults.get(i));
        }
    }

    public void generateBindViewField(ParseResult parseResult) {
        String bindViewFieldText = "";
        StringBuilder sbBindView = new StringBuilder("@BindView");
        StringBuilder sbField = new StringBuilder();
        String fieldName = "";
        Boolean mustToAddField = true;
        fieldName = parseResult.getParamsKey();
        sbBindView.append("(").append(parseResult.getLayoutId()).append(")\n");
        sbField.append(parseResult.getViewType()).append(" ").append(parseResult.getParamsKey()).append(";");
        bindViewFieldText = sbBindView.toString() + sbField.toString();

        if (!bindViewFieldText.isEmpty()) {
            PsiField fieldByName = mClazz.findFieldByName(fieldName, true);
            if (parseResult instanceof GlobalDeclareParseResult) {

                if (Parser.equalsFieldByText(mClazz, sbField.toString())) {
                    //刪掉原有的field
                    System.out.print("delete:\n" + fieldByName.getText() + "\n");
                    fieldByName.delete();
                }

                parseResult.getPsiElement().delete();

            } else if (parseResult instanceof BodyDeclareParseResult) {
                parseResult.getPsiElement().delete();
                if (fieldByName != null && Parser.isBindViewField(fieldByName)) {
                    mustToAddField = false;
                }
            } else if (parseResult instanceof BodyPreDeclareParseResult) {
                BodyPreDeclareParseResult bodyPreDeclareParseResult = (BodyPreDeclareParseResult) parseResult;
                System.out.print("delete:\n" + bodyPreDeclareParseResult.getBodyDeclareElement().getText() + "\n");
                bodyPreDeclareParseResult.getPsiElement().delete();
                bodyPreDeclareParseResult.getBodyDeclareElement().delete();
            }
            if (mustToAddField) {
                //加上BindView Field.
                mClazz.add(mFactory.createFieldFromText(bindViewFieldText.toString(), mClazz));
                System.out.print("write:\n" + bindViewFieldText + "\n");
            } else {
                System.out.print("skip:\n" + bindViewFieldText + "\n");

            }
        }
    }
}
