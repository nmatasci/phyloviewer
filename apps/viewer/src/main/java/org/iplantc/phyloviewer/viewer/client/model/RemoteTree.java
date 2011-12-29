package org.iplantc.phyloviewer.viewer.client.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;

@Entity
@Table(name="tree")
public class RemoteTree extends Tree implements Serializable
{	
	private static final long serialVersionUID = -2029381657931174210L;
	private String name;
	private byte[] hash;
	private boolean isPublic = false;
	private boolean importComplete = false;
	
	public RemoteTree() {
		
	}
	
	public RemoteTree(String name) {
		this.name = name;
	}
	
	/**
	 * Creates a shallow copy of the given RemoteTree
	 */
	public RemoteTree(RemoteTree tree) {
		this.setHash(tree.getHash());
		this.setId(tree.getId());
		this.setImportComplete(tree.isImportComplete());
		this.setName(tree.getName());
		this.setPublic(tree.isPublic());
		this.setRootNode(tree.getRootNode());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="tree_id")
	@Override
	public int getId()
	{
		return super.getId();
	}

	@Override
	public void setId(int id)
	{
		super.setId(id);
	}
	
	@ManyToOne(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.DETACH}) //many trees (having different user-assigned names, for example) could point at the same root node
	@Override
	public RemoteNode getRootNode()
	{
		return (RemoteNode) super.getRootNode();
	}

	@Override
	public void setRootNode(INode node)
	{
		super.setRootNode(node);
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public byte[] getHash()
	{
		return hash;
	}

	public void setHash(byte[] hash)
	{
		this.hash = hash;
	}

	public boolean isPublic()
	{
		return isPublic;
	}

	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	public boolean isImportComplete()
	{
		return importComplete;
	}

	public void setImportComplete(boolean importComplete)
	{
		this.importComplete = importComplete;
	}
	
	public void clean()
	{
		//nothing to clean.  subclasses will override
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RemoteTree && super.equals(obj))
		{
			RemoteTree other = (RemoteTree) obj;
			return this.name.equals(other.name)
					&& Arrays.equals(this.hash, other.hash)
					&& this.importComplete == other.importComplete
					&& this.isPublic == other.isPublic;
		}
		else
		{
			return false;
		}
	}
	
	
}
