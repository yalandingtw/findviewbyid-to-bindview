package model;

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

import com.intellij.psi.PsiElement;

/**
 * ex:
 * Toolbar toolBar;
 *
 * toolBar =  (Toolbar) findViewById(R.id.toolbar);
 *
 *
 * Created by Alan Ding on 2016/8/13.
 */
public class BodyPreDeclareParseResult extends ParseResult {
    PsiElement bodyDeclareElement;

    public BodyPreDeclareParseResult(String methodName, PsiElement psiElement, PsiElement bodyDeclareElement) {
        super(methodName, psiElement);
        this.bodyDeclareElement = bodyDeclareElement;
    }

    public BodyPreDeclareParseResult(String paramsKey, String viewType, String layoutId, PsiElement psiElement, PsiElement bodyDeclareElement) {
        super(paramsKey, viewType, layoutId, psiElement);
        this.bodyDeclareElement = bodyDeclareElement;
    }

    public PsiElement getBodyDeclareElement() {
        return bodyDeclareElement;
    }
}
