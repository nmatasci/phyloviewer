package org.iplantc.phyloviewer.shared.render.style;

import java.io.Serializable;

/**
 * Basic implementation of IStyle
 */
public class Style implements IStyle, Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private INodeStyle nodeStyle = new NodeStyle();
	private ILabelStyle labelStyle = new LabelStyle();
	private IGlyphStyle glyphStyle = new GlyphStyle();
	private IBranchStyle branchStyle = new BranchStyle();
	private boolean isInheritable;
	
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
	
	public boolean isInheritable() {
		return isInheritable;
	}

	public void setInheritable(boolean isInheritable) {
		this.isInheritable = isInheritable;
	}

	@Override
	public String toString()
	{
		//TODO allow the element styles to be null?  It would make this a lot more concise is most cases, but would involve a lot of null checks elsewhere
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"id\":\"" + this.getId() + '"');
		sb.append(",\"nodeStyle\":" + nodeStyle.toString());
		sb.append(",\"labelStyle\":" + labelStyle.toString());
		sb.append(",\"branchStyle\":" + branchStyle.toString());
		sb.append(",\"glyphStyle\":" + glyphStyle.toString());
		sb.append("}");
		
		return sb.toString();
	}
}
