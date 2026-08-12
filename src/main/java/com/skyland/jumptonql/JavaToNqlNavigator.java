package com.skyland.jumptonql;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 从 Java DAO 方法名跳转到 .nql.xml 文件中对应的 id 属性。
 * <p>
 * GotoDeclarationHandler 的 EP 采用"首个非空结果优先"策略（见 GotoDeclarationUtil），
 * 一旦本 handler 返回非空数组，IDEA 后续的 handler 和"声明或引用"回退逻辑都会被跳过。
 * 因此需要在返回值中同时包含 nql 目标和方法的引用（调用处），确保弹窗正常展示选项。
 */
public class JavaToNqlNavigator implements GotoDeclarationHandler {

	@Override
	public PsiElement @Nullable [] getGotoDeclarationTargets(@NotNull PsiElement sourceElement, int offset, @Nullable Editor editor) {
		// 只处理 Java 方法名标识符
		if (!(sourceElement instanceof PsiIdentifier)) {
			return null;
		}

		PsiMethod method = PsiTreeUtil.getParentOfType(sourceElement, PsiMethod.class);
		if (method == null) {
			return null;
		}

		// 确保点击的是方法名本身
		if (!sourceElement.equals(method.getNameIdentifier())) {
			return null;
		}

		String methodName = method.getName();

		// 1. 查找 .nql.xml 中 id 等于方法名的属性（本插件核心功能）
		List<XmlAttribute> idAttrs = NqlFileUtils.findIdAttributes(sourceElement.getProject(), methodName);
		if (idAttrs.isEmpty()) {
			// 没有 nql 映射时返回 null，让 IDEA 执行原生逻辑（显示引用等）
			return null;
		}

		List<PsiElement> targets = new ArrayList<>();

		// nql 目标放在首位
		for (XmlAttribute idAttr : idAttrs) {
			targets.add(new NqlNavigationTarget(idAttr, "nql: " + methodName));
		}

		// 2. 补充方法引用（调用处），保持 IDEA 原有的引用展示
		Collection<PsiReference> references = ReferencesSearch.search(method).findAll();
		for (PsiReference reference : references) {
			PsiElement refElement = reference.getElement();
			if (!refElement.equals(method.getNameIdentifier())) {
				targets.add(refElement);
			}
		}

		return targets.toArray(new PsiElement[0]);
	}
}
