package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.AnnotationService;
import org.iplantc.phyloviewer.viewer.server.persistence.AnnotationData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AnnotationServiceImpl extends RemoteServiceServlet implements AnnotationService
{
	private AnnotationService data;
	
	@Override
	public void init() throws ServletException
	{
		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("EntityManagerFactory");
		data = new AnnotationData(emf);
	}

	@Override
	public List<AnnotationMetadata> getAnnotationMetadata(RemoteTree tree)
	{
		return data.getAnnotationMetadata(tree);
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata(RemoteTree tree, String propertyOrRel)
	{
		return data.getAnnotationMetadata(tree, propertyOrRel);
	}
}
