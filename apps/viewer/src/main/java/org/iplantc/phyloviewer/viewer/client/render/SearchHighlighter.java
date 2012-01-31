package org.iplantc.phyloviewer.viewer.client.render;

import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.viewer.client.model.RemoteNode;
import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl.SearchResultListener;

/**
 * Listens to a SearchServiceAsyncImpl for search results and highlights the ancestors of the result
 * nodes in the tree (even if the highlighted nodes themselves haven't been loaded yet). Also listens to
 * the nodes in a view's tree for new children and highlights them if they are ancestors of the search
 * result nodes.
 */
public class SearchHighlighter implements SearchResultListener
{
	private View view;
	private ITree tree;
	private final SearchServiceAsyncImpl searchService;
	private RenderPreferences renderPreferences;

	/**
	 * Creates a new SearchHighlighter that listens to the given SearchServiceAsyncImpl for search
	 * results to highlight
	 */
	public SearchHighlighter(SearchServiceAsyncImpl searchService)
	{
		this.searchService = searchService;
		searchService.addSearchResultListener(this);
	}

	/**
	 * Removes listeners and clears highlights
	 */
	public void dispose()
	{
		searchService.removeSearchResultListener(this);
		this.clear();
	}

	/**
	 * Sets the view that this listener will update when it changes highlights
	 * @deprecated
	 */
	public void setView(View view)
	{
		this.view = view;
	}

	/**
	 * Set the RenderPreferences that this highlighter adds highlights to
	 * @deprecated
	 */
	public void setRenderPreferences(RenderPreferences renderPreferences)
	{
		this.renderPreferences = renderPreferences;
	}

	/**
	 * Sets the tree that this highlighter highlights
	 * @deprecated
	 */
	public void setTree(ITree tree)
	{
		this.tree = tree;
	}

	@Override
	public void handleSearchResult(SearchResult[] results, String query, byte[] rootID)
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}

		if(tree != null)
		{
			for(SearchResult result : results)
			{
				highlightSubtree((RemoteNode)tree.getRootNode(), result);
			}
		}

		if(view != null)
		{
			view.requestRender();
		}
	}
	
	private void highlightSubtree(RemoteNode node, SearchResult result) 
	{
		if(node.getTopology().subtreeContains(result.node.getTopology()))
		{
			renderPreferences.highlightNode(node);
			renderPreferences.highlightBranch(node);
			
			if(node.getChildren() != null)
			{
				for(RemoteNode child : node.getChildren())
				{
					highlightSubtree(child, result);
				}
			}
		}
	}

	/**
	 * Clears highlights
	 * @deprecated
	 */
	public void clear()
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}
	}
}
