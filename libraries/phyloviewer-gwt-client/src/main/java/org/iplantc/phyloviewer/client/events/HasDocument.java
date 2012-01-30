package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.model.IDocument;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An object that implements this interface has a document and registers DocumentChangeHandlers that are
 * called when that document changes
 */
public interface HasDocument
{
	/**
	 * Set the document
	 */
	public void setDocument(IDocument document);
	
	/**
	 * @return the document 
	 */
	public IDocument getDocument();
	
	/**
	 * Add a handler for DocumentChangeEvents on this object
	 * @return a HandlerRegistration used to de-register the handler later
	 */
	public HandlerRegistration addDocumentChangeHandler(DocumentChangeHandler handler);
}
