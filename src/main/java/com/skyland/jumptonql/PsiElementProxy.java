package com.skyland.jumptonql;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PsiElementProxy implements PsiNamedElement {

	private final PsiElement delegate;
	private String presentableText;
	private List<Icon> icons = new ArrayList<>();

	public PsiElementProxy(PsiElement delegate, String presentableText) {
		this.delegate = delegate;
		this.presentableText = presentableText;
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public void addIcon(Icon icon) {
		icons.add(icon);
	}

	@Override
	public @Nullable @NlsSafe String getName() {
		return presentableText;
	}

	@Override
	public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
		this.presentableText = s;
		return this;
	}

	@Override
	public @NotNull Project getProject() throws PsiInvalidElementAccessException {
		return delegate.getProject();
	}

	@Override
	public @NotNull Language getLanguage() {
		return delegate.getLanguage();
	}

	@Override
	public PsiManager getManager() {
		return delegate.getManager();
	}

	@Override
	public @NotNull PsiElement[] getChildren() {
		return delegate.getChildren();
	}

	@Override
	public PsiElement getParent() {
		return delegate.getParent();
	}

	@Override
	public PsiElement getFirstChild() {
		return delegate.getFirstChild();
	}

	@Override
	public PsiElement getLastChild() {
		return delegate.getLastChild();
	}

	@Override
	public PsiElement getNextSibling() {
		return delegate.getNextSibling();
	}

	@Override
	public PsiElement getPrevSibling() {
		return delegate.getPrevSibling();
	}

	@Override
	public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
		return delegate.getContainingFile();
	}

	@Override
	public TextRange getTextRange() {
		return delegate.getTextRange();
	}

	@Override
	public int getStartOffsetInParent() {
		return delegate.getStartOffsetInParent();
	}

	@Override
	public int getTextLength() {
		return delegate.getTextLength();
	}

	@Override
	public @Nullable PsiElement findElementAt(int i) {
		return delegate.findElementAt(i);
	}

	@Override
	public @Nullable PsiReference findReferenceAt(int i) {
		return delegate.findReferenceAt(i);
	}

	@Override
	public int getTextOffset() {
		return delegate.getTextOffset();
	}

	@Override
	public @NlsSafe String getText() {
		return delegate.getText();
	}

	@Override
	public char[] textToCharArray() {
		return delegate.textToCharArray();
	}

	@Override
	public PsiElement getNavigationElement() {
		return delegate.getNavigationElement();
	}

	@Override
	public PsiElement getOriginalElement() {
		return delegate.getOriginalElement();
	}

	@Override
	public boolean textMatches(@NotNull @NonNls CharSequence charSequence) {
		return delegate.textMatches(charSequence);
	}

	@Override
	public boolean textMatches(@NotNull PsiElement psiElement) {
		return delegate.textMatches(psiElement);
	}

	@Override
	public boolean textContains(char c) {
		return delegate.textContains(c);
	}

	@Override
	public void accept(@NotNull PsiElementVisitor psiElementVisitor) {
		delegate.accept(psiElementVisitor);
	}

	@Override
	public void acceptChildren(@NotNull PsiElementVisitor psiElementVisitor) {
		delegate.acceptChildren(psiElementVisitor);
	}

	@Override
	public PsiElement copy() {
		return delegate.copy();
	}

	@Override
	public PsiElement add(@NotNull PsiElement psiElement) throws IncorrectOperationException {
		return delegate.add(psiElement);
	}

	@Override
	public PsiElement addBefore(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
		return delegate.addBefore(psiElement, psiElement1);
	}

	@Override
	public PsiElement addAfter(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
		return delegate.addAfter(psiElement, psiElement1);
	}

	@Override
	public void checkAdd(@NotNull PsiElement psiElement) throws IncorrectOperationException {
		delegate.checkAdd(psiElement);
	}

	@Override
	public PsiElement addRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
		return delegate.addRange(psiElement, psiElement1);
	}

	@Override
	public PsiElement addRangeBefore(@NotNull PsiElement psiElement, @NotNull PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
		return delegate.addRangeBefore(psiElement, psiElement1, psiElement2);
	}

	@Override
	public PsiElement addRangeAfter(PsiElement psiElement, PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
		return delegate.addRangeAfter(psiElement, psiElement1, psiElement2);
	}

	@Override
	public void delete() throws IncorrectOperationException {
		delegate.delete();
	}

	@Override
	public void checkDelete() throws IncorrectOperationException {
		delegate.checkDelete();
	}

	@Override
	public void deleteChildRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
		delegate.deleteChildRange(psiElement, psiElement1);
	}

	@Override
	public PsiElement replace(@NotNull PsiElement psiElement) throws IncorrectOperationException {
		return delegate.replace(psiElement);
	}

	@Override
	public boolean isValid() {
		return delegate.isValid();
	}

	@Override
	public boolean isWritable() {
		return delegate.isWritable();
	}

	@Override
	public @Nullable PsiReference getReference() {
		return delegate.getReference();
	}

	@Override
	public PsiReference[] getReferences() {
		return delegate.getReferences();
	}

	@Override
	public <T> @Nullable T getCopyableUserData(@NotNull Key<T> key) {
		return delegate.getCopyableUserData(key);
	}

	@Override
	public <T> void putCopyableUserData(@NotNull Key<T> key, @Nullable T t) {
		delegate.putCopyableUserData(key, t);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor psiScopeProcessor, @NotNull ResolveState resolveState, @Nullable PsiElement psiElement, @NotNull PsiElement psiElement1) {
		return delegate.processDeclarations(psiScopeProcessor, resolveState, psiElement, psiElement1);
	}

	@Override
	public @Nullable PsiElement getContext() {
		return delegate.getContext();
	}

	@Override
	public boolean isPhysical() {
		return delegate.isPhysical();
	}

	@Override
	public @NotNull GlobalSearchScope getResolveScope() {
		return delegate.getResolveScope();
	}

	@Override
	public @NotNull SearchScope getUseScope() {
		return delegate.getUseScope();
	}

	@Override
	public ASTNode getNode() {
		return delegate.getNode();
	}

	@Override
	public boolean isEquivalentTo(PsiElement psiElement) {
		return delegate.isEquivalentTo(psiElement);
	}

	@Override
	public Icon getIcon(int i) {
		return delegate.getIcon(i);
	}

	@Override
	public <T> @Nullable T getUserData(@NotNull Key<T> key) {
		return delegate.getUserData(key);
	}

	@Override
	public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {
		delegate.putUserData(key, t);
	}

	@Override
	public int hashCode() {
		return this.presentableText.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PsiElementProxy proxy) {
			return this.presentableText.equals(proxy.presentableText);
		}
		return false;
	}
}
