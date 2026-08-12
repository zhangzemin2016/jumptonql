package com.skyland.jumptonql;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 从 .nql.xml 文件中的 id 属性值跳转到 Java DAO 方法。
 * 点击 id="value" 中的 value 部分即可跳转到对应方法。
 */
public class NqlToJavaNavigator implements GotoDeclarationHandler {

	@Override
	public PsiElement @Nullable [] getGotoDeclarationTargets(@NotNull PsiElement sourceElement, int offset, @Nullable Editor editor) {
		// 只在 .nql.xml 文件中生效
		if (!NqlFileUtils.isNqlFile(sourceElement.getContainingFile())) {
			return null;
		}

		// 获取点击位置所在的 id 属性
		XmlAttribute idAttr = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute.class);
		if (idAttr == null || !"id".equals(idAttr.getName())) {
			return null;
		}

		String idValue = idAttr.getValue();
		if (idValue == null || idValue.isEmpty()) {
			return null;
		}

		// 通过 mapper namespace 定位 Java 类
		String namespace = NqlFileUtils.getMapperNamespace(idAttr);
		if (namespace == null || namespace.isEmpty()) {
			return null;
		}

		Project project = sourceElement.getProject();
		PsiClass daoClass = JavaPsiFacade.getInstance(project)
				.findClass(namespace, GlobalSearchScope.projectScope(project));
		if (daoClass == null) {
			return null;
		}

		// 在类中查找同名方法
		PsiMethod method = NqlFileUtils.findMethodInClass(daoClass, idValue);
		if (method == null) {
			return null;
		}

		return new PsiElement[]{method};
	}
}
