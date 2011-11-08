package org.iplantc.phyloviewer.viewer.server;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.SearchService;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;
import org.iplantc.phyloviewer.viewer.server.persistence.UnpersistTreeData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SearchServiceImpl extends RemoteServiceServlet implements SearchService
{
	private static final long serialVersionUID = -7938571144166651105L;

	@Override
	public SearchResult[] find(String query, byte[] rootID, SearchType type, String layoutID)
	{
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		String queryString = type.queryString(query);
		
		ITreeData treeData = (ITreeData) getServletContext().getAttribute(Constants.TREE_DATA_KEY);
		RemoteNode root;
		try
		{
			root = treeData.getRootNode(rootID);
		}
		catch(TreeDataException e)
		{
			return results.toArray(new SearchResult[results.size()]);
		}
		
		
		EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("EntityManagerFactory");
		EntityManager em = emf.createEntityManager();
		TypedQuery<RemoteNode> q = em.createQuery("SELECT n FROM RemoteNode n WHERE n.topology.rootNode.id = :rootId AND n.label LIKE :pattern", RemoteNode.class);
		q.setParameter("rootId", root.getId());
		q.setParameter("pattern", queryString);
		List<RemoteNode> nodes = q.getResultList();
		
		ILayoutData layout = (ILayoutData) this.getServletContext().getAttribute(Constants.LAYOUT_DATA_KEY);
		
		for (RemoteNode node : nodes) {
			SearchResult result = new SearchResult();
			result.node = UnpersistTreeData.clone(node);
			result.layout = layout.getLayout(node, layoutID);
			results.add(result);
		}
		
		return results.toArray(new SearchResult[results.size()]);
	}
}
