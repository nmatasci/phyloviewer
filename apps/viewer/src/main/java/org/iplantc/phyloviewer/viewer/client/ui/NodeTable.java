package org.iplantc.phyloviewer.viewer.client.ui;

import java.util.Set;

import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.metadata.Annotation;
import org.iplantc.phyloviewer.viewer.client.model.AnnotatedNode;
import org.iplantc.phyloviewer.viewer.client.model.LiteralMetaAnnotation;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class NodeTable extends FlexTable implements NodeSelectionHandler
{
	public NodeTable()
	{
		super();
		setStylePrimaryName("nodeTable");
	}

	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		removeAllRows();
		
		if (event.getSelectedNodes().size() == 1)
		{
			INode node = event.getSelectedNodes().iterator().next();
			
			addRow("id", String.valueOf(node.getId()));
			addRow("label", node.getLabel());
			addRow("# of children", String.valueOf(node.getNumberOfChildren()));
			addRow("# of leaves", String.valueOf(node.getNumberOfLeafNodes()));
			addRow("subtree size", String.valueOf(node.getNumberOfNodes()));
			addRow("height", String.valueOf(node.findMaximumDepthToLeaf()));
			
			if (node instanceof AnnotatedNode) {
				displayAnnotations(((AnnotatedNode)node));
			}
		}
	}

	private void displayAnnotations(AnnotatedNode node)
	{
		Set<Annotation> annotations = node.getAnnotations();
		for (Annotation a: annotations) {
			if (a instanceof LiteralMetaAnnotation)
			{
				displayAnnotation((LiteralMetaAnnotation) a);
			}
		}
	}

	private void displayAnnotation(LiteralMetaAnnotation a)
	{
		String label = a.getProperty();
		String value = a.getValue();
		
		addRow(label, value);
	}
	
	private void addRow(String label, String value) 
	{
		int row = getRowCount();
		setWidget(row, 0, new Label(label));
		setText(row, 1, value);
	}
}
