package com.skyland.jumptonql;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 轻量级 PsiElement 包装器，用于在跳转弹窗中显示自定义名称（如 "nql: methodName"），
 * 实际导航操作委托给被包装的真实元素。
 * <p>
 * 继承 {@link FakePsiElement} 避免逐个实现 PsiElement 接口的全部方法。
 */
class NqlNavigationTarget extends FakePsiElement {

	private final PsiElement delegate;
	private final String displayName;

	NqlNavigationTarget(@NotNull PsiElement delegate, @NotNull String displayName) {
		this.delegate = delegate;
		this.displayName = displayName;
	}

	@Override
	public @Nullable String getName() {
		return displayName;
	}

	@Override
	public @NotNull PsiElement getParent() {
		return delegate.getParent();
	}

	@Override
	public @NotNull PsiManager getManager() {
		return delegate.getManager();
	}

	@Override
	public @NotNull Project getProject() {
		return delegate.getProject();
	}

	@Override
	public @Nullable PsiFile getContainingFile() {
		return delegate.getContainingFile();
	}

	@Override
	public @NotNull PsiElement getNavigationElement() {
		return delegate.getNavigationElement();
	}

	@Override
	public int getTextOffset() {
		return delegate.getTextOffset();
	}

	@Override
	public boolean isValid() {
		return delegate.isValid();
	}

	@Override
	public @Nullable TextRange getTextRange() {
		return delegate.getTextRange();
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return delegate.getIcon(flags);
	}

	@Override
	public boolean isEquivalentTo(PsiElement another) {
		return this == another || delegate.isEquivalentTo(another);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NqlNavigationTarget that)) return false;
		return delegate.equals(that.delegate) && displayName.equals(that.displayName);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode() * 31 + displayName.hashCode();
	}
}
