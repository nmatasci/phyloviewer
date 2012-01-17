package org.iplantc.phyloviewer.viewer.client.services;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnnotationServiceAsync
{

	void getAnnotationMetadata(RemoteTree tree, AsyncCallback<List<AnnotationMetadata>> callback);

	void getAnnotationMetadata(RemoteTree tree, String propertyOrRel,
			AsyncCallback<AnnotationMetadata> callback);

}
