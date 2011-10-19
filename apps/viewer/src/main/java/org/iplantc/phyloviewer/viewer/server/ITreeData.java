package org.iplantc.phyloviewer.viewer.server;

import java.util.List;

import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.TreeDataException;

public interface ITreeData
{

	/**
	 * Get the root node for the given tree id
	 * @param the id of the tree to get the root node.
	 * @return the node.
	 */
	public abstract RemoteNode getRootNode(int treeID) throws TreeDataException;
	
	/** 
	 * Get a list of loaded trees.
	 * @return Gets a list of all loaded trees.
	 */
	public abstract List<ITree> getTrees() throws TreeDataException;
	
	/**
	 * @param depth the number of levels below the root to include.  Use Integer.MAX_VALUE to guarantee getting the whole tree.
	 * @return the subtree rooted at rootID, to <strong>at least</strong> the given depth.
	 */
	public abstract RemoteNode getSubtree(int rootID, int depth) throws TreeDataException;

	/**
	 * Gets the children of the parent node with id parentID. (The order of the children is not defined
	 * in the current implementation.)
	 * 
	 * @param parentID
	 * @return the children of parentID.  Null if the node had no children
	 */
	public abstract List<RemoteNode> getChildren(int parentID) throws TreeDataException;
	
}
