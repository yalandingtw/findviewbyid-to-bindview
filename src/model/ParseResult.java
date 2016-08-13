package model;

import com.intellij.psi.PsiElement;

/**
 * Created by Alan Ding on 2016/8/5.
 */
public class ParseResult {
    String methodName = "";
    String paramsKey = "";
    String viewType = "";
    String layoutId = "";
    PsiElement psiElement;

    public ParseResult(String methodName, PsiElement psiElement) {
        this.methodName = methodName;
        this.psiElement = psiElement;
    }

    public ParseResult(String paramsKey, String viewType, String layoutId, PsiElement psiElement) {
        this.paramsKey = paramsKey;
        this.viewType = viewType;
        this.layoutId = layoutId;
        this.psiElement = psiElement;
    }


    public PsiElement getPsiElement() {
        return psiElement;
    }
    public String getParamsKey() {
        return paramsKey;
    }

    public void setParamsKey(String paramsKey) {
        this.paramsKey = paramsKey;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return layoutId + " 指向 " + viewType + " 變數 " + paramsKey + "\n";
    }
}
