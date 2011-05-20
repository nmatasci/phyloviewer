package org.iplantc.phyloviewer.viewer.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Calculates topology fields while importing the tree. This is not necessary for trees with RemoteNode
 * nodes. For those, use ImportRemoteNodeTree instead.
 */
public class ImportNodeTree extends ImportTree<INode>
{
	public ImportNodeTree(Connection conn) throws SQLException
	{
		super(conn);
	}

	public ImportNodeTree(Connection conn, ExecutorService executor) throws SQLException
	{
		super(conn, executor);
	}

	@Override
	protected void addSubtree(INode node, Integer parentId) throws SQLException
	{
		this.addSubtree(node, parentId, new TopologyCounter());
	}
	
	private void addSubtree(INode node, Integer parentId, TopologyCounter counter) throws SQLException
	{
		counter.firstVisit();
		int firstVisit = counter.traversalCount;
		int depth = counter.depth;
		int maxChildHeight = -1;
		int totalLeavesInSubtree = 0;
		
		if (node.getChildren() != null && node.getChildren().length > 0)
		{
			for (INode child : node.getChildren())
			{
				int childId = treeWriter.addNode(child.getLabel());
				child.setId(childId);
				addSubtree(child, node.getId(), counter);
				
				maxChildHeight = Math.max(maxChildHeight, counter.height);
				totalLeavesInSubtree += counter.numLeaves;
				
				treeWriter.addAltLabel(childId, counter.altLabel); //FIXME altLabel could be null if there are no named nodes in the subtree
			}
		}
		else 
		{
			counter.leaf(node.getLabel());
			totalLeavesInSubtree = 1;
		}
		
		counter.secondVisit(depth, maxChildHeight + 1, totalLeavesInSubtree);
		int secondVisit = counter.traversalCount;
		
		addTopologyToBatch(parentId, node.getId(), node.getChildren().length, firstVisit, secondVisit, depth, counter);
	}
	
	private void addTopologyToBatch(Integer parentId, int childId, int numChildren, int firstVisit, int secondVisit, int depth, TopologyCounter counter) throws SQLException
	{
		treeWriter.addTopologyToBatch(parentId, childId, counter.subtreeSize(firstVisit, secondVisit), 
				counter.numLeaves, counter.height, 
				firstVisit, secondVisit, depth, 
				numChildren);
	}

	/**
	 * Keeps track of some topological stats during a depth-first traversal of a tree.
	 */
	private class TopologyCounter 
	{
		int numLeaves = 0;
		int traversalCount = 0; 
		int depth = -1;
		int height = 0;
		String altLabel = "root"; //arbitrary.  will be updated to the name of the last-visited leaf node in the subtree
		
		/**
		 * Called when the node is first visited. Updates traversalCount and depth. Resets numLeaves to
		 * zero. The traversalCount and depth should be saved by the caller before visiting children, for
		 * use in secondVisit.
		 */
		public void firstVisit() 
		{
			traversalCount++;
			depth++;
			numLeaves = 0;
			this.altLabel = null;
		}
		
		/**
		 * Called after all of a node's subtree has been visited (depth-first)
		 * 
		 * @param depth to the current node. Counter depth is reset to this.
		 * @param height the height of the current node. Equal to the maximum of its children's heights,
		 *            plus one. Counter height is set to this.
		 * @param numLeaves the total number of leaves in the current node's subtree.
		 */
		public void secondVisit(int depth, int height, int numLeaves) 
		{
			traversalCount++;
			this.depth = depth;
			this.height = height;
			this.numLeaves = numLeaves;
		}
		
		/**
		 * Called when a leaf is visited. Resets height to zero and numLeaves to one.
		 */
		public void leaf(String name)
		{
			height = 0;
			numLeaves = 1;
			
			if (name != null && !name.isEmpty()) {
				this.altLabel = name;
			}
		}
		
		/**
		 * Returns the size of a subtree, given its root's firstVisit and secondVisit traversalCounts.
		 */
		public int subtreeSize(int firstVisit, int secondVisit)
		{
			return (secondVisit - firstVisit + 1) / 2;
		}
	}
}
