package com.skyland.jumptonql;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * .nql.xml 文件与 Java DAO 方法之间的公共查找逻辑。
 */
final class NqlFileUtils {

	static final String NQL_FILE_SUFFIX = ".nql.xml";

	private NqlFileUtils() {
	}

	static boolean isNqlFile(@Nullable PsiFile file) {
		return file != null && file.getName().endsWith(NQL_FILE_SUFFIX);
	}

	/**
	 * 在项目范围内查找所有 .nql.xml 文件中 id 值等于 {@code idValue} 的属性。
	 */
	@NotNull
	static List<XmlAttribute> findIdAttributes(@NotNull Project project, @NotNull String idValue) {
		List<XmlAttribute> result = new ArrayList<>();
		GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
		Collection<VirtualFile> xmlFiles = FilenameIndex.getAllFilesByExt(project, "xml", scope);

		PsiManager manager = PsiManager.getInstance(project);
		for (VirtualFile vf : xmlFiles) {
			if (!vf.getName().endsWith(NQL_FILE_SUFFIX)) {
				continue;
			}
			PsiFile psiFile = manager.findFile(vf);
			if (!(psiFile instanceof XmlFile xmlFile)) {
				continue;
			}
			XmlAttribute attr = findIdAttribute(xmlFile, idValue);
			if (attr != null) {
				result.add(attr);
			}
		}
		return result;
	}

	/**
	 * 在单个 XML 文件中递归查找 id 值等于 {@code idValue} 的属性。
	 */
	@Nullable
	static XmlAttribute findIdAttribute(@NotNull XmlFile xmlFile, @NotNull String idValue) {
		XmlTag rootTag = xmlFile.getRootTag();
		return rootTag == null ? null : findIdAttributeInTag(rootTag, idValue);
	}

	@Nullable
	private static XmlAttribute findIdAttributeInTag(@NotNull XmlTag tag, @NotNull String idValue) {
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

	/**
	 * 从 id 属性向上查找所属 {@code <mapper>} 标签的 namespace 属性值。
	 */
	@Nullable
	static String getMapperNamespace(@NotNull XmlAttribute idAttr) {
		XmlTag idTag = idAttr.getParent();
		if (idTag == null) {
			return null;
		}
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
	 * 在指定类中按名称查找方法（不含参数匹配，取第一个同名方法）。
	 */
	@Nullable
	static PsiMethod findMethodInClass(@NotNull PsiClass psiClass, @NotNull String methodName) {
		for (PsiMethod method : psiClass.getMethods()) {
			if (methodName.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}
}
