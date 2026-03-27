package com.skyland.jumptonql;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 从 .nql.xml 文件中的 id 属性值跳转到 Java DAO 方法
 * 点击 id="value" 中的 value 部分
 */
public class NqlToJavaNavigator implements GotoDeclarationHandler {

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@NotNull PsiElement sourceElement, int offset, @Nullable Editor editor) {
        // 检查文件是否为 .nql.xml
        PsiFile file = sourceElement.getContainingFile();
        if (file == null || !file.getName().endsWith(".nql.xml")) {
            return null;
        }

        // 获取点击位置所在的 XmlAttribute
        XmlAttribute idAttr = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute.class);
        if (idAttr == null) {
            return null;
        }

        // 只处理 id 属性
        if (!"id".equals(idAttr.getName())) {
            return null;
        }

        String idValue = idAttr.getValue();
        if (idValue == null || idValue.isEmpty()) {
            return null;
        }

        // 获取 mapper 标签的 namespace
        String namespace = getMapperNamespace(idAttr);
        if (namespace == null || namespace.isEmpty()) {
            return null;
        }

        // 查找对应的 Java 类
        Project project = sourceElement.getProject();
        PsiClass daoClass = findClass(project, namespace);
        if (daoClass == null) {
            return null;
        }

        // 在该类中查找同名方法
        PsiMethod method = findMethodInClass(daoClass, idValue);
        if (method == null) {
            return null;
        }

        return new PsiElement[]{method};
    }

    /**
     * 获取 mapper 标签的 namespace 属性值
     */
    private String getMapperNamespace(XmlAttribute idAttr) {
        // id 所在的标签（如 select, update, insert, delete）
        XmlTag idTag = idAttr.getParent();
        if (idTag == null) {
            return null;
        }

        // 向上查找 mapper 标签
        XmlTag parent = idTag.getParentTag();
        while (parent != null) {
            if ("mapper".equals(parent.getName())) {
                return parent.getAttributeValue("namespace");
            }
            parent = parent.getParentTag();
        }

        return null;
    }

    /**
     * 根据全限定名查找类
     */
    private PsiClass findClass(Project project, String qualifiedName) {
        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
        return facade.findClass(qualifiedName, GlobalSearchScope.projectScope(project));
    }

    /**
     * 在指定类中查找同名方法
     */
    private PsiMethod findMethodInClass(PsiClass psiClass, String methodName) {
        for (PsiMethod method : psiClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
