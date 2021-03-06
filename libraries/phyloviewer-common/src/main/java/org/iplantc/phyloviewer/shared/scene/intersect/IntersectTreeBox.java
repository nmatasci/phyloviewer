package org.iplantc.phyloviewer.shared.scene.intersect;

import java.util.HashSet;
import java.util.Set;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;

/**
 * Static utility methods to find which nodes in a tree are within a given region.
 */
public class IntersectTreeBox 
{
	public static Set<INode> intersect(INode node, ILayoutData layout, Box2D range)
	{
		Set<INode> result = new HashSet<INode>();
		boolean ready = node != null && layout != null && range != null && range.valid();
		
		if (ready) 
		{
			visit(node, layout, range, result);
		}
		
		return result;
	}
	
	static void visit(INode node, ILayoutData layout, Box2D range, Set<INode> result) 
	{
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 position = layout.getPosition(node);
		if (node != null && boundingBox != null && position != null) 
		{
			boolean nodeInRange = range.contains(position);
			if (nodeInRange) 
			{
				result.add(node);
			}
			
			if (nodeInRange || boundingBox.intersects(range)) 
			{
				traverse(node, layout, range, result);
			}
		}
	}

	static void traverse(INode node, ILayoutData layout, Box2D range, Set<INode> result) {
		if (node.getChildren() != null)
		{
			for(INode child : node.getChildren()) {
				visit(child, layout, range, result);
			}
		}
	}
}
