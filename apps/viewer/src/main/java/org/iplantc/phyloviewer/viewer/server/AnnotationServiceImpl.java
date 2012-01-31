package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import javax.servlet.ServletException;

import org.iplantc.phyloviewer.shared.model.metadata.AnnotationMetadata;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.AnnotationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of AnnotationService servlet.
 */
@SuppressWarnings("serial")
public class AnnotationServiceImpl extends RemoteServiceServlet implements AnnotationService
{
	private AnnotationData data;
	
	/**
	 * Initializes the servlet.
	 * Sets the AnnotationData data access object from the servlet context
	 */
	@Override
	public void init() throws ServletException
	{
		Object attribute = getServletContext().getAttribute(AnnotationData.class.getName());
		
		if (attribute != null)
		{
			data = (AnnotationData) attribute;
		}
	}
	
	/**
	 * Set the AnnotationData data access object.
	 */
	public void setData(AnnotationData data)
	{
		this.data = data;
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
