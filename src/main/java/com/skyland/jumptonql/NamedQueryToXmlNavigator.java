package com.skyland.jumptonql;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
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

public class NamedQueryToXmlNavigator implements GotoDeclarationHandler {

	@Override
	public PsiElement[] getGotoDeclarationTargets(@NotNull PsiElement sourceElement, int offset, Editor editor) {
		if (!isMethodNameIdentifier(sourceElement)) {
			return null;
		}

		PsiMethod method = PsiTreeUtil.getParentOfType(sourceElement, PsiMethod.class);
		if (method == null || method.getAnnotation("com.skyland.dynajpa.namedquery.mapper.annotation.NamedQuery") == null) {
			return null;
		}

		Collection<PsiReference> references = ReferencesSearch.search(method).findAll();
		List<PsiElement> targets = new ArrayList<>();
		for (PsiReference reference : references) {
			PsiElement refElement = reference.getElement();
			if (refElement.equals(method.getNameIdentifier())) {
				continue;
			}
			PsiMethod refMethod = PsiTreeUtil.getParentOfType(refElement, PsiMethod.class);
			if (refElement == null) {
				continue;
			}
			PsiClass refClass = PsiTreeUtil.getParentOfType(refMethod, PsiClass.class);
			PsiElementProxy psiElementProxy = new PsiElementProxy(refElement, refMethod.getName() + " (" + refClass.getQualifiedName() + ")");
			if (targets.contains(psiElementProxy)) {
				continue;
			}
			targets.add(psiElementProxy);
		}

		Module module = ModuleUtil.findModuleForPsiElement(sourceElement);
		// 搜索所有 .xml 文件
		Collection<VirtualFile> xmlFiles = FilenameIndex.getAllFilesByExt(
				sourceElement.getProject(),
				"xml", // 这里只需要扩展名，不需要通配符
				GlobalSearchScope.moduleScope(module)
		);

		String methodName = method.getName();
		for (VirtualFile vf : xmlFiles) {
			if (!vf.getName().endsWith(".nql.xml")) {
				continue;
			}
			PsiFile psiFile = PsiManager.getInstance(sourceElement.getProject()).findFile(vf);
			if (psiFile instanceof XmlFile xmlFile) {
				XmlElement target = findXmlElementWithId(xmlFile, methodName);
				if (target != null) {
					targets.addFirst(new PsiElementProxy(target, "nql: " + methodName));
					return targets.toArray(new PsiElement[0]);
				}
			}
		}
		return targets.toArray(new PsiElement[0]);
	}

	// 新增判断方法
	private boolean isMethodNameIdentifier(PsiElement element) {
		// 检查当前元素是否为标识符类型
		if (!(element instanceof PsiIdentifier)) return false;

		// 获取父方法并验证标识符位置
		PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
		if (method == null) return false;

		// 对比当前元素是否就是方法名的标识符
		return element.equals(method.getNameIdentifier());
	}

	private XmlElement findXmlElementWithId(XmlFile xmlFile, String idValue) {
		XmlDocument doc = xmlFile.getDocument();
		if (doc == null) return null;

		XmlTag rootTag = doc.getRootTag();
		if (rootTag == null) return null;

		return findTagWithIdAttribute(rootTag, idValue);
	}

	private XmlElement findTagWithIdAttribute(XmlTag tag, String idValue) {
		XmlAttribute idAttr = tag.getAttribute("id");
		if (idAttr != null && idValue.equals(idAttr.getValue())) {
			return idAttr; // 返回属性本身，跳转时会定位到 id 行
		}

		for (XmlTag subTag : tag.getSubTags()) {
			XmlElement result = findTagWithIdAttribute(subTag, idValue);
			if (result != null) return result;
		}

		return null;
	}
}
