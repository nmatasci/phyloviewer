
package org.iplantc.phyloviewer.shared.model;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.model.Node;
import org.junit.Test;

import junit.framework.TestCase;

public class TestNode extends TestCase {
	
	public TestNode() {
		super();
	}
	
	@Test
	public void testFindMaximumDistanceToLeaf() {
		Node root = new Node();
		Node a = new Node();
		a.setBranchLength(1.0);
		Node b = new Node();
		b.setBranchLength(2.0);
		Node c = new Node();
		c.setBranchLength(1.0);
		Node d = new Node();
		d.setBranchLength(3.0);
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(a);
		children.add(b);
		root.setChildren(children);
		
		children = new ArrayList<Node>();
		children.add(c);
		children.add(d);
		b.setChildren(children);
		
		double distance=root.findMaximumDistanceToLeaf();
		assertTrue(5.0==distance);
	}
}