package org.iplantc.phyloviewer.shared.render.style;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Style implements IStyle, IsSerializable {
	
	String id;
	INodeStyle nodeStyle = new NodeStyle();
	ILabelStyle labelStyle = new LabelStyle();
	IGlyphStyle glyphStyle = new GlyphStyle();
	IBranchStyle branchStyle = new BranchStyle();
	
	public Style(String id) {
		this.id = id;
	}
	
	public Style(String id,INodeStyle nodeStyle,ILabelStyle labelStyle,IGlyphStyle glyphStyle,IBranchStyle branchStyle) {
		this.id = id;
		this.nodeStyle = nodeStyle;
		this.labelStyle = labelStyle;
		this.glyphStyle = glyphStyle;
		this.branchStyle = branchStyle;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public INodeStyle getNodeStyle() {
		return nodeStyle;
	}
	
	public void setNodeStyle(INodeStyle nodeStyle) {
		this.nodeStyle = nodeStyle;
	}

	@Override
	public ILabelStyle getLabelStyle() {
		return labelStyle;
	}
	
	public void setLabelStyle(ILabelStyle labelStyle) {
		this.labelStyle = labelStyle;
	}

	@Override
	public IGlyphStyle getGlyphStyle() {
		return glyphStyle;
	}
	
	public void setGlyphStyle(IGlyphStyle glyphStyle) {
		this.glyphStyle = glyphStyle;
	}

	@Override
	public IBranchStyle getBranchStyle() {
		return branchStyle;
	}
	
	public void setBranchStyle(IBranchStyle branchStyle) {
		this.branchStyle = branchStyle;
	}

}
