/**
 */
package org.somox.sourcecodedecorator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceFile;

import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File Level Source Code Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Human readable information for the source code link
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.somox.sourcecodedecorator.FileLevelSourceCodeLink#getRepositoryComponent <em>Repository Component</em>}</li>
 *   <li>{@link org.somox.sourcecodedecorator.FileLevelSourceCodeLink#getFile <em>File</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.somox.sourcecodedecorator.SourceCodeDecoratorPackage#getFileLevelSourceCodeLink()
 * @model
 * @generated
 */
public interface FileLevelSourceCodeLink extends EObject {
	/**
	 * Returns the value of the '<em><b>Repository Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Component</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Repository Component</em>' reference.
	 * @see #setRepositoryComponent(RepositoryComponent)
	 * @see org.somox.sourcecodedecorator.SourceCodeDecoratorPackage#getFileLevelSourceCodeLink_RepositoryComponent()
	 * @model ordered="false"
	 * @generated
	 */
	RepositoryComponent getRepositoryComponent();

	/**
	 * Sets the value of the '{@link org.somox.sourcecodedecorator.FileLevelSourceCodeLink#getRepositoryComponent <em>Repository Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Repository Component</em>' reference.
	 * @see #getRepositoryComponent()
	 * @generated
	 */
	void setRepositoryComponent(RepositoryComponent value);

	/**
	 * Returns the value of the '<em><b>File</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File</em>' reference.
	 * @see #setFile(SourceFile)
	 * @see org.somox.sourcecodedecorator.SourceCodeDecoratorPackage#getFileLevelSourceCodeLink_File()
	 * @model ordered="false"
	 * @generated
	 */
	SourceFile getFile();

	/**
	 * Sets the value of the '{@link org.somox.sourcecodedecorator.FileLevelSourceCodeLink#getFile <em>File</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File</em>' reference.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(SourceFile value);

} // FileLevelSourceCodeLink
