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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.iplantc.phyloviewer.viewer.server.ParseTree;

/**
 * A persistent, serializable implementation of ITree
 */
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
	
	@OneToOne(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.DETACH})
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

	/**
	 * @return this tree's unique identifier. When trees are re-imported, this identifier should not
	 *         change.
	 * @see ParseTree#saveTree(java.util.Map)
	 */
	public byte[] getHash()
	{
		return hash;
	}

	public void setHash(byte[] hash)
	{
		this.hash = hash;
	}

	/**
	 * @return whether this tree should be listed publicly
	 */
	public boolean isPublic()
	{
		return isPublic;
	}

	
	/**
	 * Set whether this tree should be listed publicly
	 */
	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	/**
	 * @return true if the import process (nodes, annotations, layouts, overview image) is complete for
	 *         this tree
	 */
	public boolean isImportComplete()
	{
		return importComplete;
	}

	/**
	 *  Set whether the import process is complete forthis tree
	 */
	public void setImportComplete(boolean importComplete)
	{
		this.importComplete = importComplete;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RemoteTree && super.equals(obj))
		{
			RemoteTree other = (RemoteTree) obj;
			return this.name == null && other.name == null || this.name.equals(other.name)
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
