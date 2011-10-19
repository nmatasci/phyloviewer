package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;
import org.iplantc.phyloviewer.viewer.client.services.TreeListService;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeListServiceImpl extends RemoteServiceServlet implements TreeListService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1593366473133954060L;

	@Override
	public List<ITree> getTreeList() throws TreeDataException {
		
		ITreeData data = (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
		return data.getTrees();
	}	
}
