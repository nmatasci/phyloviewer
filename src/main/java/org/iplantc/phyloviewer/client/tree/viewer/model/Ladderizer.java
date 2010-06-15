package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class Ladderizer {
	private Map<INode, Integer> subtreeSizes = new HashMap<INode, Integer>();
	private LadderizeComparator comparator = new LadderizeComparator();
	private Direction direction;
	
	public Ladderizer(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Ladderizes the children of a node 
	 * @return the number of nodes in this subtree
	 */
	public int ladderize(INode node) {
		
		int size = 1;
		
		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			INode child = node.getChild(i);
			int subtreeSize = ladderize(child);
			subtreeSizes.put(child, subtreeSize);
			//System.out.println("Put " + child.hashCode() + ", " + subtreeSize);
			size += subtreeSize;
		}
		
		node.sortChildrenBy(comparator);
		
		return size;
	}
	
	public enum Direction { 
		UP(1), DOWN(-1); 
		private int value;
		private Direction(int value) {
			this.value = value;
		}
	}
	
	private class LadderizeComparator implements Comparator<INode> {
		@Override
		public int compare(INode node0, INode node1) {
			//TODO throw an exception if both nodes aren't already in the subtreeSizes map
			return direction.value * (subtreeSizes.get(node0) - subtreeSizes.get(node1));
		}
	}

}