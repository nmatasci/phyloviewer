package org.iplantc.phyloviewer.shared.render.style;

/**
 * A style based on an existing style. Returns values from the base style if the value hasn't been set.
 * 
 * Wraps element styles (NodeStyle, BranchStyle, etc) in a corresponding composite element style, so modifying the
 * element styles returned by getters does not modify the base style.
 */
public class CompositeStyle implements IStyle
{	
	private CompositeNodeStyle nodeStyle;
	private CompositeLabelStyle labelStyle;
	private CompositeGlyphStyle glyphStyle;
	private CompositeBranchStyle branchStyle;
	
	private String mainStyleId;
	private String baseStyleId;
	private boolean isInheritable;
	
	/**
	 * Creates a new CompositeStyle for the given baseStyle and mainStyle.
	 */
	public CompositeStyle(IStyle mainStyle, IStyle baseStyle)
	{
		nodeStyle = new CompositeNodeStyle(mainStyle.getNodeStyle(), baseStyle.getNodeStyle());
		labelStyle = new CompositeLabelStyle(mainStyle.getLabelStyle(), baseStyle.getLabelStyle());
		glyphStyle = new CompositeGlyphStyle(mainStyle.getGlyphStyle(), baseStyle.getGlyphStyle());
		branchStyle = new CompositeBranchStyle(mainStyle.getBranchStyle(), baseStyle.getBranchStyle());
		
		this.mainStyleId = mainStyle.getId();
		this.baseStyleId = baseStyle.getId();
	}
	
	/**
	 * Sets the fallback style for all of the element styles
	 */
	public void setBaseStyle(IStyle baseStyle)
	{
		//set element base styles
		getNodeStyle().setBaseStyle(baseStyle.getNodeStyle());
		getLabelStyle().setBaseStyle(baseStyle.getLabelStyle());
		getGlyphStyle().setBaseStyle(baseStyle.getGlyphStyle());
		getBranchStyle().setBaseStyle(baseStyle.getBranchStyle());
		this.baseStyleId = baseStyle.getId();
	}
	
	@Override
	public CompositeBranchStyle getBranchStyle()
	{
		return branchStyle;
	}

	@Override
	public CompositeGlyphStyle getGlyphStyle()
	{
		return glyphStyle;
	}

	@Override
	public CompositeLabelStyle getLabelStyle()
	{
		return labelStyle;
	}

	@Override
	public CompositeNodeStyle getNodeStyle()
	{
		return nodeStyle;
	}

	@Override
	public String getId()
	{
		return mainStyleId + " | " + baseStyleId;
	}

	public boolean isInheritable() {
		return isInheritable;
	}

	public void setInheritable(boolean isInheritable) {
		this.isInheritable = isInheritable;
	}
}
