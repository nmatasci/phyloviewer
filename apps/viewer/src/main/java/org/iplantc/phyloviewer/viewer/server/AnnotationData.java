package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

/**
 * An interface for annotation data access
 */
public interface AnnotationData
{
	/**
	 * @return metadata for all annotations in the given tree
	 */
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree);
	
	/**
	 * @return metadata for all annotations in the given tree matching the given property. There should
	 *         be just one element in this list, but it's possible to have annotations with the same
	 *         propertyOrRel but different datatypes, in which case there would be more than one element.
	 */
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree, String propertyOrRel);
	
	//public Set<Annotation> getAnnotations(AnnotatedNode node); TODO
}
