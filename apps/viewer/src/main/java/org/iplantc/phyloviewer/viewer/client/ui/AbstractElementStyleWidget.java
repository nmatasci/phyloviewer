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
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.MutableStyleMap;
import org.iplantc.phyloviewer.shared.render.style.Style;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * An abstract base class for widgets that display and edit styling for node rendering elements.
 */
public abstract class AbstractElementStyleWidget extends FlexTable implements NodeSelectionHandler, DocumentChangeHandler
{
	private IDocument document;
	private Set<INode> nodes = Collections.emptySet();
	private ArrayList<HasValue<?>> widgets = new ArrayList<HasValue<?>>();
	
	/**
	 * Create a new AbstractElementStyleWidget that edits styles in the given document
	 */
	public AbstractElementStyleWidget(IDocument document)
	{	
		this.document = document; 
	}
	
	/**
	 * Set the document that this widget edits styles in
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
	}
	
	/**
	 * Gets the current style for a given node.
	 * @param node the node to get a style for
	 * @param create if true, creates a style for the node if it doesn't exist.
	 * @return the style (or null if !create and the style doesn't exist in the document).
	 */
	public IStyle getStyle(INode node, boolean create)
	{
		IStyleMap styleMap = document.getStyleMap();
		IStyle style = styleMap.get(node);
		
		if (create && style == null && styleMap instanceof MutableStyleMap)
		{	
			style = new Style(String.valueOf(node.getId()));
			style = new CompositeStyle(style, Defaults.DEFAULT_STYLE);
			((MutableStyleMap)styleMap).put(node, style);
		}
		
		return style;
	}
	
	/**
	 * @return the currently selected set of nodes that this widget is editing styles for
	 */
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
	
	/**
	 * Subclasses should update the style data displayed in their editor, showing a single node's current
	 * style.
	 */
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
