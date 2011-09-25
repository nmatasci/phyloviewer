package org.iplantc.phyloviewer.viewer.client.render;

import org.iplantc.phyloviewer.client.tree.viewer.View;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.viewer.client.services.SearchService.SearchResult;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl.SearchResultListener;

/**
 * Listens to a SearchServiceAsyncImpl for search results and highlights the ancestors of the result
 * nodes in the tree. Also listens to the nodes in a view's tree for new children and highlights them if
 * they are ancestors of the search result nodes.
 */
public class SearchHighlighter implements SearchResultListener
{
	private View view;
	private ITree tree;
	private final SearchServiceAsyncImpl searchService;
	private RenderPreferences renderPreferences;

	public SearchHighlighter(SearchServiceAsyncImpl searchService)
	{
		this.searchService = searchService;
		searchService.addSearchResultListener(this);
	}

	public void dispose()
	{
		searchService.removeSearchResultListener(this);
		this.clear();
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public void setRenderPreferences(RenderPreferences renderPreferences)
	{
		this.renderPreferences = renderPreferences;
	}

	public void setTree(ITree tree)
	{
		this.tree = tree;
	}

	@Override
	public void handleSearchResult(SearchResult[] result, String query, int treeID)
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}

		if(tree != null)
		{
			renderPreferences.highlightSubtree(tree.getRootNode().getId());
		}

		if(view != null)
		{
			view.requestRender();
		}
	}

	/**
	 * Removes this listener from all of the nodes of the tree
	 */
	public void clear()
	{
		if(renderPreferences != null)
		{
			renderPreferences.clearAllHighlights();
		}
	}
}
