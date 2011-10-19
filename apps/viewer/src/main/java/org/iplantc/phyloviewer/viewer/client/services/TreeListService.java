package org.iplantc.phyloviewer.viewer.client.services;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeList")
public interface TreeListService extends RemoteService {
	
	List<ITree> getTreeList() throws TreeDataException;
}
