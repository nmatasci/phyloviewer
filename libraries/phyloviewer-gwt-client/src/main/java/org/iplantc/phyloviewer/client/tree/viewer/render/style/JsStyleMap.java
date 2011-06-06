package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStyleMap extends JavaScriptObject implements IStyleMap
{
	protected JsStyleMap()
	{
	}

	@Override
	public final IStyle get(INode node)
	{
		if(node != null)
		{
			String styleId = this.getStyleIdForNodeId(node.getId());
			if (styleId == null) {
				styleId = this.getStyleIdForNodeLabel(node.getLabel());
			}

			return this.getStyleNative(styleId);
		}
		return null;
	}

	private final native String getStyleIdForNodeId(int nodeId) /*-{
		if(this.nodeStyleMappings != null) {
			return this.nodeStyleMappings[nodeId];
		}
		
		return null;
	}-*/;
	
	private final native String getStyleIdForNodeLabel(String nodeLabel) /*-{
		if(this.nameStyleMappings != null) {
			return this.nameStyleMappings[nodeLabel];
		}
	
		return null;
	}-*/;

	private final native JsStyle getStyleNative(String styleId) /*-{return this.styles[styleId];}-*/;

	@Override
	public final void put(INode node, IStyle style)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public final native boolean hasBranchDecoration(int nodeId) /*-{
		if(this.branchDecorations != null) {
			return this.branchDecorations[nodeId] == "triangle";
		}
	
		return false;
	}-*/;

}
