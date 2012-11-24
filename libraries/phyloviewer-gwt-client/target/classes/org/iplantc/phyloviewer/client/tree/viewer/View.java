/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.DocumentChangeEvent;
import org.iplantc.phyloviewer.client.events.DocumentChangeHandler;
import org.iplantc.phyloviewer.client.events.HasDocument;
import org.iplantc.phyloviewer.client.events.HasNodeSelectionHandlers;
import org.iplantc.phyloviewer.client.events.NodeSelectionEvent;
import org.iplantc.phyloviewer.client.events.NodeSelectionHandler;
import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.client.events.RenderHandler;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.HasRenderPreferences;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Abstract base class for tree views.
 * 
 * Basic usage:
 * Use setDocument() to set the document to view
 * Use requestRender() to render the current document
 * 
 * Also, listens for RenderEvents on its EventBus and renders when it receives one.
 */
public abstract class View extends FocusPanel implements RequiresResize, HasDocument,
		HasNodeSelectionHandlers, HasRenderPreferences
{
	public enum LayoutType
	{
		LAYOUT_TYPE_CLADOGRAM, LAYOUT_TYPE_CIRCULAR
	}

	private Camera camera;
	private IDocument document;
	private boolean renderRequestPending = false;
	LayoutType layoutType;
	private EventBus eventBus = new SimpleEventBus();
	private RenderPreferences renderPreferences = new RenderPreferences();

	/** A NodeSelectionHandler that re-fires selection events with this view as the source */
	protected NodeSelectionHandler refireHandler = new NodeSelectionHandler()
	{
		@Override
		public void onNodeSelection(NodeSelectionEvent event)
		{
			getEventBus().fireEventFromSource(new NodeSelectionEvent(event.getSelectedNodes()),
					View.this);
		}
	};

	public View()
	{
		this.initEventListeners();
	}

	@Override
	public IDocument getDocument()
	{
		return document;
	}

	/**
	 * Set the document to be rendered. Fires a DocumentChangeEvent.
	 */
	@Override
	public void setDocument(IDocument document)
	{
		this.document = document;

		if(eventBus != null)
		{
			eventBus.fireEventFromSource(new DocumentChangeEvent(document), this);
		}
	}
	
	/**
	 * Set the style map.
	 * @param styleMap
	 */
	public void setStyleMap(IStyleMap styleMap)
	{
		IDocument document = getDocument();
		if(document != null)
		{
			document.setStyleMap(styleMap);
			this.requestRender();
		}
	}

	@Override
	public HandlerRegistration addDocumentChangeHandler(DocumentChangeHandler handler)
	{
		return eventBus.addHandlerToSource(DocumentChangeEvent.TYPE, this, handler);
	}

	@Override
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler)
	{
		return eventBus.addHandlerToSource(NodeSelectionEvent.TYPE, this, handler);
	}

	/**
	 * A convenience method for getting the tree from the current document
	 */
	public ITree getTree()
	{
		return this.getDocument() != null ? this.getDocument().getTree() : null;
	}

	protected Camera getCamera()
	{
		return camera;
	}

	protected void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	/**
	 * A convenience method for getting the layout from the current document
	 */
	public ILayoutData getLayout()
	{
		return document != null ? document.getLayout() : null;
	}

	/**
	 * Zoom to fit the entire tree
	 */
	public void zoomToFit()
	{
		if(null != this.getTree())
		{
			zoomToFitSubtree(getTree().getRootNode());
		}
	}

	/**
	 * Zoom to fit a subtree rooted at the given node
	 */
	public void zoomToFitSubtree(final INode subtree)
	{
		if(subtree != null)
		{
			this.zoomToFitSubtree(subtree.getId());
		}
	}

	/**
	 * Zoom to fit a subtree rooted at the node with the given nodeId
	 */
	public void zoomToFitSubtree(final int nodeId)
	{
		ILayoutData layout = this.getLayout();
		if(null != layout)
		{
			// No need to check for layout data. As of now, layout data always is present with node data.
			// If this changes, logic for handling remote data should be in the document.
			Box2D boundingBox = layout.getBoundingBox(nodeId);
			this.zoomToBoundingBox(boundingBox);
		}
	}

	/**
	 * Zoom to fit a given rectangle
	 */
	public void zoomToBoundingBox(Box2D boundingBox)
	{
		if(null != this.getCamera())
		{
			getCamera().zoomToBoundingBox(boundingBox);
			this.dispatch(new RenderEvent());
		}
	}

	protected abstract void resize(int width, int height);

	/**
	 * Render immediately. (Not recommended.  Use requestRender() instead.)
	 */
	public abstract void render();
	
	/**
	 * Render to the given graphics target
	 */
	public abstract void renderTo(IGraphics g);

	/**
	 * @return true if this view is ready to render
	 */
	protected abstract boolean isReady();

	/**
	 * Request that this view render its document. Rendering is scheduled to happen after the current
	 * browser event loop returns. Multiple calls to requestRender() during a single event loop will
	 * result in a single render.
	 */
	public void requestRender()
	{

		if(!this.renderRequestPending)
		{
			this.renderRequestPending = true;
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{

				@Override
				public void execute()
				{
					if(View.this.isReady())
					{
						View.this.render();
						View.this.renderRequestPending = false;
					}
					else
					{
						Scheduler.get().scheduleDeferred(this);
					}
				}
			});
		}
	}

	/**
	 * @return a URL for a static image of the current view
	 */
	public abstract String exportImageURL();

	public void setEventBus(EventBus eventBus)
	{
		this.eventBus = eventBus;
		this.initEventListeners();
	}

	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public void onResize()
	{
		resize(getParent().getOffsetWidth(), getParent().getOffsetHeight());
		this.requestRender();
	}

	@Override
	public RenderPreferences getRenderPreferences()
	{
		return renderPreferences;
	}

	@Override
	public void setRenderPreferences(RenderPreferences rp)
	{
		this.renderPreferences = rp;
	}

	public LayoutType getLayoutType()
	{
		return layoutType;
	}

	protected void setLayoutType(LayoutType type)
	{
		this.layoutType = type;
	}

	/**
	 * Fires an event from this View on its EventBus
	 */
	protected void dispatch(GwtEvent<?> event)
	{
		if(eventBus != null)
		{
			eventBus.fireEventFromSource(event, this);
		}
	}

	/**
	 * Adds a RenderEvent listener on this View's event bus
	 */
	protected void initEventListeners()
	{
		if(eventBus != null)
		{
			eventBus.addHandler(RenderEvent.TYPE, new RenderHandler()
			{
				@Override
				public void onRender(RenderEvent event)
				{
					requestRender();
				}
			});
		}
	}

	/**
	 * Pans the camera and re-renders
	 * @see Camera#pan(double, double)
	 */
	public void pan(double xAmount, double yAmount)
	{
		getCamera().pan(xAmount, yAmount);
		dispatch(new RenderEvent());
	}

	/**
	 * Zooms the camera and re-renders
	 * @see Camera#zoom(double)
	 */
	public void zoom(double amount)
	{
		getCamera().zoom(amount);
		dispatch(new RenderEvent());
	}
}
