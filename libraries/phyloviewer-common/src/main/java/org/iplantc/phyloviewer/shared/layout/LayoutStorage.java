package org.iplantc.phyloviewer.shared.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * A basic implementation of ILayoutData, which stores and retrieves layout data based on node ID.
 */
public class LayoutStorage implements ILayoutData
{
	private Map<Integer,Vector2> positions = new HashMap<Integer,Vector2>();
	private Map<Integer,Box2D> bounds = new HashMap<Integer,Box2D>();

	public LayoutStorage()
	{
	}

	@Override
	public Box2D getBoundingBox(INode node)
	{
		return this.getBoundingBox(node.getId());
	}

	@Override
	public Box2D getBoundingBox(int nodeId)
	{
		return bounds.get(nodeId);
	}

	public Box2D getBoundingBox(Integer key)
	{
		return bounds.get(key);
	}

	@Override
	public Vector2 getPosition(INode node)
	{
		return positions.get(node.getId());
	}
	
	public Vector2 getPosition(Integer key)
	{
		return positions.get(key);
	}

	@Override
	public boolean containsNode(INode node)
	{
		return this.positions.containsKey(node.getId());
	}

	/**
	 * A convenience method to check if the structure contains data for all of the given nodes.
	 */
	public boolean containsNodes(INode[] nodes)
	{
		for(int i = 0;i < nodes.length;i++)
		{
			if(!this.containsNode(nodes[i]))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Clear any existing layout data and set the capacity to the given numberOfNodes.
	 */
	public void init(int numberOfNodes)
	{
		positions = new HashMap<Integer,Vector2>(numberOfNodes);
		bounds = new HashMap<Integer,Box2D>(numberOfNodes);
	}

	/**
	 * A convenience method to set both properties at once.
	 */
	public void setPositionAndBounds(int nodeId, Vector2 position, Box2D box)
	{
		bounds.put(nodeId, box);
		positions.put(nodeId, position);
	}

	public void setBoundingBox(INode node, Box2D box2d)
	{
		bounds.put(node.getId(), box2d);
	}

	public void setPosition(INode node, Vector2 vector2)
	{
		positions.put(node.getId(), vector2);
	}

	/**
	 * @return the set of all node IDs mapped by this LayoutStorage
	 */
	public Set<Integer> keySet()
	{
		return positions.keySet();
	}
}
