package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Node implements INode, IsSerializable
{
	private int id;
	private String label;
	private Node[] children;
	private transient Map<String, Object> data = new HashMap<String, Object>();
	private transient INodeStyle style = new NodeStyle();
	
	public Node(int id, String label)
	{
		this.id = id;
		this.label = label;
	}
	
	public Node(Node[] children) 
	{ 
		this.children = children;
	}
	
	public Node() { }

	@Override
	public String findLabelOfFirstLeafNode()
	{
		if ( this.isLeaf() ) {
			return this.getLabel();
		}
		
		return this.getChild(0).findLabelOfFirstLeafNode();
	}
	
	@Override
	public int findMaximumDepthToLeaf()
	{
		int maxChildHeight = -1; //-1 so leaf will return 0

		for (int index = 0; index < getNumberOfChildren(); index++)
		{
			INode child = getChild(index);
			maxChildHeight = Math.max(maxChildHeight, child.findMaximumDepthToLeaf());
		}
		
		return maxChildHeight + 1;
	}
	
	@Override
	public Node getChild(int index)
	{
		if (getChildren() == null) {
			return null;
		} else if (index < 0 || index >= getNumberOfChildren()) {
			throw new ArrayIndexOutOfBoundsException("Child #" + index + " does not exist.");
		}
		
		return getChildren()[index];
	}
	
	@Override
	public Node[] getChildren()
	{
		return children;
	}
	
	@Override
	public Object getData(String key)
	{
		return data.get(key);
	}
	
	@Override
	public int getId()
	{
		return id;
	}
	
	@Override
	public String getJSON()
	{
		String json = "{\"name\":\"" + this.getLabel() + "\",\"children\":[";
		
		if (getChildren() != null) {
			for (int i = 0, len = this.getNumberOfChildren(); i < len; i++) {
				json += this.getChild(i).getJSON();
				if (i < len - 1) {
					json += ",";
				}
			}
		}
		
		json += "]}";
		
		return json;
	}
	
	@Override
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public int getNumberOfChildren()
	{
		if (getChildren() == null)
		{
			return 0;
		}
		else
		{
			return getChildren().length;
		}
	}
	
	@Override
	public int getNumberOfLeafNodes()
	{
		int count = 0;
		if (this.isLeaf()) 
		{
			count = 1;
		}
		else 
		{
			for ( int i = 0; i < this.getNumberOfChildren(); ++i ) 
			{
				count += this.getChild(i).getNumberOfLeafNodes();
			}
		}
		
		return count;
	}
	
	@Override
	public int getNumberOfNodes()
	{
		int count = 1;
		
		if (getChildren() != null) 
		{
			for (INode child : getChildren()) 
			{
				count += child.getNumberOfNodes();
			}
		}
		
		return count;
	}
	
	@Override
	public INodeStyle getStyle()
	{
		return style;
	}
	
	@Override
	public Boolean isLeaf()
	{
		return getNumberOfChildren() == 0;
	}
	
	@Override
	public void setData(String key, Object value)
	{
		this.data.put(key, value);
	}
	
	@Override
	public void setId(int id)
	{
		this.id = id;
	}
	
	@Override
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	@Override
	public void sortChildrenBy(Comparator<INode> comparator)
	{
		if (getChildren() != null)
		{
			List<Node> childList = Arrays.asList(getChildren());
			Collections.sort(childList, comparator);
			setChildren(childList.toArray(new Node[childList.size()]));
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Node))
		{
			return false;
		}

		Node that = (Node) obj;

		return this.shallowEquals(that) && Arrays.equals(this.getChildren(), that.getChildren());
	}
	
	public boolean shallowEquals(Node obj) {
		return this.getId() == obj.getId()
				&& this.getLabel().equals(obj.getLabel());
	}
	
	protected void setChildren(Node[] children) 
	{
		this.children = children;
	}
}