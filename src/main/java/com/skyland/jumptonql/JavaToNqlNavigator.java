package com.skyland.jumptonql;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 从 Java DAO 方法跳转到 .nql.xml 文件中对应的 id 属性
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

		List<PsiElement> targets = new ArrayList<>();

		// 1. 添加方法引用跳转（原有功能）
		Collection<PsiReference> references = ReferencesSearch.search(method).findAll();
		for (PsiReference reference : references) {
			PsiElement refElement = reference.getElement();
			// 跳过方法定义本身
			if (!refElement.equals(method.getNameIdentifier())) {
				targets.add(refElement);
			}
		}

		// 2. 添加 nql.xml 跳转
		String methodName = method.getName();
		GlobalSearchScope scope = GlobalSearchScope.projectScope(sourceElement.getProject());
		Collection<VirtualFile> xmlFiles = FilenameIndex.getAllFilesByExt(
				sourceElement.getProject(),
				"xml",
				scope
		);

		for (VirtualFile vf : xmlFiles) {
			if (!vf.getName().endsWith(".nql.xml")) {
				continue;
			}
			PsiFile psiFile = PsiManager.getInstance(sourceElement.getProject()).findFile(vf);
			if (psiFile instanceof XmlFile xmlFile) {
				XmlAttribute idAttr = findIdAttribute(xmlFile, methodName);
				if (idAttr != null) {
					// 使用 PsiElementProxy 包装，显示自定义名称 "nql"
					targets.add(0, new PsiElementProxy(idAttr, "nql: " + methodName));
				}
			}
		}

		return targets.isEmpty() ? null : targets.toArray(new PsiElement[0]);
	}

	/**
	 * 在 XML 文件中查找指定 id 值的属性
	 */
	private XmlAttribute findIdAttribute(XmlFile xmlFile, String idValue) {
		XmlDocument doc = xmlFile.getDocument();
		if (doc == null) {
            return null;
        }

		XmlTag rootTag = doc.getRootTag();
		if (rootTag == null) {
            return null;
        }

		return findIdAttributeInTag(rootTag, idValue);
	}

	private XmlAttribute findIdAttributeInTag(XmlTag tag, String idValue) {
		XmlAttribute idAttr = tag.getAttribute("id");
		if (idAttr != null && idValue.equals(idAttr.getValue())) {
			return idAttr;
		}

		for (XmlTag subTag : tag.getSubTags()) {
			XmlAttribute result = findIdAttributeInTag(subTag, idValue);
			if (result != null) {
                return result;
            }
		}

		return null;
	}
}
