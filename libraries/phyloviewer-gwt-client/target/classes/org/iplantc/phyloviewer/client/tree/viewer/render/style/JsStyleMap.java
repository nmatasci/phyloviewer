package org.iplantc.phyloviewer.client.tree.viewer.render.style;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.HasBranchDecoration;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.JavaScriptObject;

public class JsStyleMap extends JavaScriptObject implements IStyleMap, HasBranchDecoration
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

			if (styleId != null)
			{
				return this.getStyleNative(styleId);
			}
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
	public final native boolean hasBranchDecoration(int nodeId) /*-{
		if(this.branchDecorations != null) {
			return this.branchDecorations[nodeId] == "triangle";
		}
	
		return false;
	}-*/;

	@Override 
	public final String getBranchLabel(INode node)
	{
		return getBranchLabel(node.getId());
	}
	
	public final native String getBranchLabel(int nodeId) /*-{
		if(this.branchLabels != null) {
			return this.branchLabels[nodeId];
		}
	
		return null;
	}-*/;
}
