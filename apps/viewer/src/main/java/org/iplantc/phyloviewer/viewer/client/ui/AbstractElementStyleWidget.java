package org.iplantc.phyloviewer.viewer.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.style.CompositeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractElementStyleWidget extends FlexTable implements NodeSelectionHandler, DocumentChangeHandler
{
	private IDocument document;
	private Set<INode> nodes = Collections.emptySet();
	private ArrayList<HasValue<?>> widgets = new ArrayList<HasValue<?>>();
	
	public AbstractElementStyleWidget(IDocument document)
	{	
		this.document = document; 
	}
	
	public void setDocument(IDocument document)
	{
		this.document = document;
	}
	
	/**
	 * @param node the node to get a style for
	 * @param create create a style for the node if it doesn't exist.
	 * @return the style (or null if !create and the style doesn't exist in the document).
	 */
	public IStyle getStyle(INode node, boolean create)
	{
		IStyle style = document.getStyleMap().get(node);
		
		if (create && style == null)
		{	
			style = new Style(String.valueOf(node.getId()));
			style = new CompositeStyle(style, Defaults.DEFAULT_STYLE);
			document.getStyleMap().put(node, style);
		}
		
		return style;
	}
	
	public Set<INode> getNodes()
	{
		return nodes;
	}
	
	protected final void setWidget(int row, int col, HasValue<?> widget)
	{
		if (widget instanceof Widget)
		{
			widgets.add(widget);
			setWidget(row, col, (Widget)widget);
		}
	}
	
	@Override
	public void onNodeSelection(NodeSelectionEvent event)
	{
		this.nodes = Collections.emptySet(); //empty first to prevent updateWidgets() from firing events that will alter styles for the previously selected nodes
		clearWidgets(widgets);
		updateWidgets(event.getSelectedNodes());
		
		AbstractElementStyleWidget.this.nodes = event.getSelectedNodes();
	}

	@Override
	public void onDocumentChange(DocumentChangeEvent event)
	{
		this.document = event.getDocument();
	}
	
	public abstract void updateValues(INode node);
	
	private void updateWidgets(Set<INode> selectedNodes)
	{
		setEnabled(widgets, true);
		
		if(selectedNodes.size() == 1)
		{
			INode node = selectedNodes.iterator().next();
			updateValues(node);
		} 
		else if(selectedNodes.size() == 0)
		{
			setEnabled(widgets, false);
		}
	}

	private void clearWidgets(List<HasValue<?>> widgets)
	{
		for (HasValue<?> widget : widgets)
		{
			widget.setValue(null, true); //fireEvents = true to let listeners (e.g. the color picker's background color) update for empty value
		}
	}

	private void setEnabled(List<HasValue<?>> widgets, boolean enabled)
	{
		for (HasValue<?> widget : widgets)
		{
			if (widget instanceof HasEnabled)
			{
				((HasEnabled)widget).setEnabled(enabled);
			}
		}
	}
}
