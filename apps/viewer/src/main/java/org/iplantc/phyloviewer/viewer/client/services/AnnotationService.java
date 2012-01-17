package org.iplantc.phyloviewer.viewer.client.services;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

import com.google.gwt.user.client.rpc.RemoteService;

public interface AnnotationService extends RemoteService
{
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree);
	public AnnotationMetadata getAnnotationMetadata(RemoteTree tree, String propertyOrRel);
	//public Set<Annotation> getAnnotations(AnnotatedNode node); TODO
}
