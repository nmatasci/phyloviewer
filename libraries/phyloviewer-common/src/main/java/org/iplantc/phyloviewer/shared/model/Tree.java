package org.iplantc.phyloviewer.shared.model;

import java.io.Serializable;


public class Tree implements ITree, Serializable {
	private static final long serialVersionUID = 5545157148705536777L;
	private INode rootNode;
	private int id;
	
	public Tree() {
	}

	public Tree(int id, INode root) {
		this.setRootNode(root);
		this.id = id;
	}

	@Override
	public int getNumberOfNodes() {
		return rootNode.getNumberOfNodes();
	}

	@Override
	public INode getRootNode() {
		return rootNode;
	}

	@Override
	public void setRootNode(INode node) {
		this.rootNode = node;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof Tree))
		{
			return false;
		}

		Tree that = (Tree)obj;
		return this.id == that.getId() && this.rootNode.equals(that.getRootNode());
	}

	@Override
	public String getName()
	{
		// TODO 
		return String.valueOf(this.id);
	}
}
