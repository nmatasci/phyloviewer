/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.shared.model.INode;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class JsNode extends JavaScriptObject implements INode
{
	protected JsNode()
	{
	}

	@Override
	public final native int getId() /*-{ return this.id; }-*/;

	@Override
	public final native void setId(int id) /*-{ this.id = id; }-*/;

	public final native String getLabel() /*-{ return this.name; }-*/;

	public final native void setLabel(String label) /*-{ this.name = label; }-*/;

	private final native <T extends JavaScriptObject> JsArray<T> getNativeChildren() /*-{ return this.children; }-*/;
	
	final native void setParent(JsNode parent) /*-{ this.parent = parent; }-*/;
	
	public final native JsNode getParent() /*-{ return this.parent }-*/;

	public final int getNumberOfChildren()
	{
		if(null == this.getNativeChildren())
			return 0;
		return this.getNativeChildren().length();
	}

	public final JsNode getChild(int index)
	{
		JsNode child = (JsNode)this.getNativeChildren().get(index);
		
		if (child.getParent() == null)
		{
			child.setParent(this);
		}
		
		return child;
	}

	@Override
	public final List<JsNode> getChildren()
	{
		ArrayList<JsNode> children = new ArrayList<JsNode>(getNumberOfChildren());
		for(int i = 0;i < getNumberOfChildren();i++)
		{
			children.set(i, getChild(i));
		}

		return children;
	}

	public final Boolean isLeaf()
	{
		return 0 == this.getNumberOfChildren();
	}

	public final int getNumberOfLeafNodes()
	{
		int count = 0;
		if(this.isLeaf())
		{
			count = 1;
		}
		else
		{
			for(int i = 0;i < this.getNumberOfChildren();++i)
			{
				count += this.getChild(i).getNumberOfLeafNodes();
			}
		}

		return count;
	}

	private final int _findMaximumDepthToLeafImpl(int currentDepth)
	{
		int localMaximum = currentDepth;
		if(!this.isLeaf())
		{
			for(int i = 0;i < this.getNumberOfChildren();++i)
			{
				int depth = this.getChild(i)._findMaximumDepthToLeafImpl(currentDepth + 1);

				if(depth > localMaximum)
				{
					localMaximum = depth;
				}
			}
		}

		return localMaximum;
	}

	public final int findMaximumDepthToLeaf()
	{
		return this._findMaximumDepthToLeafImpl(0);
	}

	public final String findLabelOfFirstLeafNode()
	{
		if(this.isLeaf())
		{
			return this.getLabel();
		}

		return this.getChild(0).findLabelOfFirstLeafNode();
	}

	@Override
	public final int getNumberOfNodes()
	{
		int count = 1;

		for(int i = 0;i < getNumberOfChildren();i++)
		{
			INode child = getChild(i);
			count += child.getNumberOfNodes();
		}

		return count;
	}

	@Override
	public final native Double getBranchLength() /*-{ this.branchLength; }-*/;

	@Override
	public final native void setBranchLength(Double branchLength) /*-{ this.branchLength = branchLength; }-*/;

	@Override
	public final double findMaximumDistanceToLeaf()
	{
		return this.findMaximumDistanceToLeaf(0.0);
	}

	private double findMaximumDistanceToLeaf(double currentDistance)
	{
		double localMaximum = currentDistance;

		int numChildren = this.getNumberOfChildren();
		if(0 < numChildren)
		{
			for(int i = 0;i < numChildren;++i)
			{
				JsNode child = this.getChild(i);
				double distance = child.findMaximumDistanceToLeaf(currentDistance
						+ this.getBranchLength());

				if(distance > localMaximum)
				{
					localMaximum = distance;
				}
			}
		}

		return localMaximum;
	}
	
	@Override
	public final String getMetaDataString()
	{
		JSONObject object = new JSONObject ( this.getMetaDataStringNative() );
		return object.toString();
	}
	
	private final native JavaScriptObject getMetaDataStringNative() /*-{	return this.metadata; }-*/;
}
