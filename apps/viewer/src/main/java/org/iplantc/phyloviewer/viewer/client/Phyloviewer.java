/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.render.svg.SVGGraphics;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.RenderPreferences;
import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyleMap;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;
import org.iplantc.phyloviewer.viewer.client.TreeWidget.ViewType;
import org.iplantc.phyloviewer.viewer.client.model.RemoteTree;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedService.NodeResponse;
import org.iplantc.phyloviewer.viewer.client.services.CombinedServiceAsync;
import org.iplantc.phyloviewer.viewer.client.services.CombinedServiceAsyncImpl;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl;
import org.iplantc.phyloviewer.viewer.client.services.SearchServiceAsyncImpl.RemoteNodeSuggestion;
import org.iplantc.phyloviewer.viewer.client.services.StyleServiceClient;
import org.iplantc.phyloviewer.viewer.client.services.StyleServiceClient.StyleServiceException;
import org.iplantc.phyloviewer.viewer.client.services.TreeListService;
import org.iplantc.phyloviewer.viewer.client.services.TreeListServiceAsync;
import org.iplantc.phyloviewer.viewer.client.ui.BranchStyleWidget;
import org.iplantc.phyloviewer.viewer.client.ui.ColorBox;
import org.iplantc.phyloviewer.viewer.client.ui.ContextMenu;
import org.iplantc.phyloviewer.viewer.client.ui.GlyphStyleWidget;
import org.iplantc.phyloviewer.viewer.client.ui.LabelStyleWidget;
import org.iplantc.phyloviewer.viewer.client.ui.NodeStyleWidget;
import org.iplantc.phyloviewer.viewer.client.ui.NodeTable;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The EntryPoint class for phyloviewer.  Builds the user interface and sets up remote services.
 */
public class Phyloviewer implements EntryPoint
{

	public TreeWidget widget;

	List<ITree> trees;

	CombinedServiceAsync combinedService = new CombinedServiceAsyncImpl();
	SearchServiceAsyncImpl searchService = new SearchServiceAsyncImpl();
	TreeListServiceAsync treeList = GWT.create(TreeListService.class);

	EventBus eventBus = new SimpleEventBus();
	
	String layoutType = "LAYOUT_TYPE_CLADOGRAM"; //determines whether we fetch layouts with branch lengths (LAYOUT_TYPE_PHYLOGRAM) or without (LAYOUT_TYPE_CLADOGRAM).  TODO make this an Enum in the shared package.

	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{

		widget = new TreeWidget(searchService, eventBus);

		Style highlight = new Style("highlight");
		highlight.setNodeStyle(new NodeStyle("#C2C2F5", Double.NaN, null));
		highlight.setLabelStyle(new LabelStyle(null));
		highlight.setGlyphStyle(new GlyphStyle(null, "#C2C2F5", Double.NaN));
		highlight.setBranchStyle(new BranchStyle("#C2C2F5", Double.NaN));

		RenderPreferences rp = new RenderPreferences();
		rp.setHighlightStyle(highlight);
		widget.setRenderPreferences(rp);

		MenuBar fileMenu = new MenuBar(true);
		fileMenu.addItem("Open...", new Command()
		{

			@Override
			public void execute()
			{
				Phyloviewer.this.displayTrees();
			}

		});

		Command openURL = new Command()
		{
			@Override
			public void execute()
			{
				Window.open(widget.exportImageURL(), "_blank", "");
			}
		};

		fileMenu.addItem("Get image (opens in a popup window)", openURL);
		
		Command showSVG = new Command() {
			@Override
			public void execute()
			{
				String svg = getSVG();
				String url = "data:image/svg+xml;charset=utf-8," + svg;
				Window.open(url, "_blank", "");				
			}
		};
		fileMenu.addItem("Export to SVG", showSVG);
	
		MenuBar layoutMenu = new MenuBar(true);
		layoutMenu.addItem("Rectangular Cladogram", new Command()
		{
			@Override
			public void execute()
			{
				ITree tree = widget.getDocument().getTree();
				byte[] treeID = ((RemoteTree)tree).getHash();
				widget.setDocument(null);
				widget.setViewType(TreeWidget.ViewType.VIEW_TYPE_CLADOGRAM);
				layoutType = "LAYOUT_TYPE_CLADOGRAM";
				
				loadTree(null, treeID, layoutType);
			}
		});
		layoutMenu.addItem("Rectangular Phylogram", new Command()
		{
			@Override
			public void execute()
			{
				ITree tree = widget.getDocument().getTree();
				byte[] treeID = ((RemoteTree)tree).getHash();
				widget.setDocument(null);
				widget.setViewType(TreeWidget.ViewType.VIEW_TYPE_CLADOGRAM);
				layoutType = "LAYOUT_TYPE_PHYLOGRAM";
				
				loadTree(null, treeID, layoutType);
			}
		});
		layoutMenu.addItem("Circular", new Command()
		{
			@Override
			public void execute()
			{
				ITree tree = widget.getDocument().getTree();
				byte[] treeID = ((RemoteTree)tree).getHash();
				widget.setDocument(null);
				widget.setViewType(TreeWidget.ViewType.VIEW_TYPE_RADIAL);
				layoutType = "LAYOUT_TYPE_CLADOGRAM";
				
				loadTree(null, treeID, layoutType);
			}
		});

		MenuBar styleMenu = new MenuBar(true);
		final TextInputPopup styleTextPopup = new TextInputPopup();
		styleTextPopup.addValueChangeHandler(new ValueChangeHandler<String>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				String style = event.getValue();
				try
				{
					IStyleMap styleMap = StyleServiceClient.parseJSON(style);
					widget.getDocument().setStyleMap(styleMap);
					widget.render();
				}
				catch(StyleServiceException e)
				{
					Window.alert("Unable to parse style document. See https://pods.iplantcollaborative.org/wiki/display/iptol/Using+Phyloviewer+GWT+client+library#UsingPhyloviewerGWTclientlibrary-Addingstylingmarkuptoviewer for help.");
				}
			}
		});

		styleTextPopup.setModal(true);

		styleMenu.addItem("Import Tree Styling (copy and paste JSON)", new Command()
		{
			@Override
			public void execute()
			{
				styleTextPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						int left = (Window.getClientWidth() - offsetWidth) / 3;
						int top = (Window.getClientHeight() - offsetHeight) / 3;
						styleTextPopup.setPopupPosition(left, top);
					}
				});
			}
		});


		final FileUploadPopup styleUploadPopup = new FileUploadPopup();
		styleUploadPopup.addValueChangeHandler(new ValueChangeHandler<String>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				try
				{
			    	String style = event.getValue();
			    	IStyleMap styleMap = StyleServiceClient.parseJSON(style);
					widget.getDocument().setStyleMap(styleMap);
					widget.render();
				}
				catch(Exception e)
				{
					Window.alert("Problem with widget: styleUploadPopup.");
				}
			}
		});

		styleUploadPopup.setModal(true);
	    
	    styleMenu.addItem("Import Tree Styling (upload file)", new Command()
		{
			@Override
			public void execute()
			{
				styleUploadPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						int left = (Window.getClientWidth() - offsetWidth) / 2;
						int top = (Window.getClientHeight() - offsetHeight) / 2;
						styleUploadPopup.setPopupPosition(left, top);
					}
				});
			}
		});


		final MetadataDialoguePopup styleMetadataDialoguePopup = new MetadataDialoguePopup();
		styleMetadataDialoguePopup.addValueChangeHandler(new ValueChangeHandler<String>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				String style = event.getValue();
				try
				{
					//String style = event.getValue();
					IStyleMap styleMap = StyleServiceClient.parseJSON(style);
					widget.getDocument().setStyleMap(styleMap);
					widget.render();
				}
				//catch(StyleServiceException e)
				catch(Exception e)
				{
					Window.alert("Unable to upload document.");
				}
			}
		});

		styleMetadataDialoguePopup.setModal(true);
	    
	    styleMenu.addItem("Tree Style Editing Panel", new Command()
		{
			@Override
			public void execute()
			{
				styleMetadataDialoguePopup.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						int left = (Window.getClientWidth() - offsetWidth) / 2;
						int top = (Window.getClientHeight() - offsetHeight) / 2;
						styleMetadataDialoguePopup.setPopupPosition(left, top);
						//initColorPicker();
					}
				});
			}
		});


		// Make a search box
		final SuggestBox searchBox = new SuggestBox(searchService);
		searchBox.setLimit(10); // TODO make scrollable?
		searchBox.addSelectionHandler(new SelectionHandler<Suggestion>()
		{
			@Override
			public void onSelection(SelectionEvent<Suggestion> event)
			{
				Box2D box = ((RemoteNodeSuggestion)event.getSelectedItem()).getResult().layout.boundingBox;
				widget.show(box);
			}
		});

		// create some styling widgets for the context menu
		NodeStyleWidget nodeStyleWidget = new NodeStyleWidget(widget.getDocument());
		BranchStyleWidget branchStyleWidget = new BranchStyleWidget(widget.getDocument());
		GlyphStyleWidget glyphStyleWidget = new GlyphStyleWidget(widget.getDocument());
		LabelStyleWidget labelStyleWidget = new LabelStyleWidget(widget.getDocument());

		// replace their default TextBoxes with ColorBoxes, which jscolor.js will add a color picker to
		nodeStyleWidget.setColorWidget(createColorBox());
		branchStyleWidget.setStrokeColorWidget(createColorBox());
		glyphStyleWidget.setStrokeColorWidget(createColorBox());
		glyphStyleWidget.setFillColorWidget(createColorBox());
		labelStyleWidget.setColorWidget(createColorBox());

		// add the widgets to separate panels on the context menu
		final ContextMenu contextMenuPanel = new ContextMenu(widget);
		contextMenuPanel.add(new NodeTable(), "Node details", 3);
		contextMenuPanel.add(nodeStyleWidget, "Node", 3);
		contextMenuPanel.add(branchStyleWidget, "Branch", 3);
		contextMenuPanel.add(glyphStyleWidget, "Glyph", 3);
		contextMenuPanel.add(labelStyleWidget, "Label", 3);

		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.add(new Label("Search:"));
		searchPanel.add(searchBox);

		// Make the UI.
		MenuBar menu = new MenuBar();
		final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.EM);
		mainPanel.addNorth(menu, 2);
		mainPanel.addSouth(searchPanel, 2);
		mainPanel.addWest(contextMenuPanel, 0);
		mainPanel.add(widget);
		RootLayoutPanel.get().add(mainPanel);

		MenuBar viewMenu = new MenuBar(true);
		viewMenu.addItem("Layout", layoutMenu);
		viewMenu.addItem("Style", styleMenu);

		contextMenuPanel.setVisible(false);
		viewMenu.addItem("Toggle Context Panel", new Command()
		{
			@Override
			public void execute()
			{
				if(contextMenuPanel.isVisible())
				{
					contextMenuPanel.setVisible(false);
					mainPanel.setWidgetSize(contextMenuPanel, 0);
					mainPanel.forceLayout();
				}
				else
				{
					contextMenuPanel.setVisible(true);
					mainPanel.setWidgetSize(contextMenuPanel, 20);
					mainPanel.forceLayout();
				}
			}
		});

		menu.addItem("File", fileMenu);
		menu.addItem("View", viewMenu);

		// Draw for the first time.
		RootLayoutPanel.get().forceLayout();
		mainPanel.forceLayout();
		widget.setViewType(ViewType.VIEW_TYPE_CLADOGRAM);
		widget.render();

		initColorPicker();

		String[] splitPath = Window.Location.getPath().split("/");
		String treeIdString = null;
		for(int i = 0; i < splitPath.length; i++) {
			if (splitPath[i].equalsIgnoreCase("tree")) {
				treeIdString = splitPath[i+1];
			}
		}
		
		if(treeIdString != null && !treeIdString.equals(""))
		{
			try {
				final byte[] treeId = Hex.decodeHex(treeIdString.toCharArray());
				this.loadTree(null, treeId, layoutType);
			}
			catch(Exception e)
			{
				Window.alert("Unable to parse tree ID string from URL");
			}
		}
		else
		{
			// Present the user the dialog to load a tree.
			this.displayTrees();
		}
		
		
		updateStyle();
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				updateStyle();
			}
		});
		
		
	}

	
	private ColorBox createColorBox()
	{
		ColorBox colorBox = new ColorBox();
		colorBox.addStyleName("{hash:true,required:false}"); // jscolor config
		return colorBox;
	}

	//private final native void initColorPicker()
	native void initColorPicker()
	/*-{
		$wnd.jscolor.init();
	}-*/;

	private void loadTree(final PopupPanel displayTreePanel, final byte[] treeId, final String layoutID)
	{
		combinedService.getRootNode(treeId, layoutID, new AsyncCallback<CombinedService.NodeResponse>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(NodeResponse result)
			{
				RemoteTree tree = new RemoteTree();
				tree.setRootNode(result.node);
				tree.setHash(treeId);
				PagedDocument document = new PagedDocument(tree, result.layout);
				setDocument(document);

				if(displayTreePanel != null)
				{
					displayTreePanel.hide();
				}
			}
		});
	}
	
	private void setDocument(PagedDocument document) 
	{
		document.setCombinedService(combinedService);
		document.setEventBus(eventBus);
		searchService.setDocument(document);
		widget.setDocument(document);
		updateStyle();
	}
	
	private void loadTree(final PopupPanel displayTreePanel, final RemoteTree tree, final String layoutID)
	{
		combinedService.getLayout(tree.getRootNode(), layoutID, new AsyncCallback<CombinedService.LayoutResponse>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert(caught.getMessage());

				if(displayTreePanel != null)
				{
					displayTreePanel.hide();
				}
			}

			@Override
			public void onSuccess(LayoutResponse layoutResponse)
			{
				PagedDocument document = new PagedDocument(tree, layoutResponse);
				setDocument(document);
				
				if(displayTreePanel != null)
				{
					displayTreePanel.hide();
				}
			}
		});
	}

	private void displayTrees()
	{
		final PopupPanel displayTreePanel = new PopupPanel();
		displayTreePanel.setModal(true);

		displayTreePanel.setPopupPositionAndShow(new PopupPanel.PositionCallback()
		{
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				int top = (Window.getClientHeight() - offsetHeight) / 2 - 100;
				displayTreePanel.setPopupPosition(left, top);
			}
		});

		final VerticalPanel vPanel = new VerticalPanel();
		displayTreePanel.add(vPanel);

		Label messageLabel = new Label("Select a tree to load:");
		vPanel.add(messageLabel);

		final Label label = new Label("Retrieving tree list...");
		vPanel.add(label);

		final ListBox lb = new ListBox();
		lb.setVisible(false);
		vPanel.add(lb);

		final HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(new Button("OK", new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent arg0)
			{

				label.setText("Loading...");
				label.setVisible(true);
				lb.setVisible(false);
				hPanel.setVisible(false);

				int index = lb.getSelectedIndex();
				if(index >= 0 && trees != null)
				{
					final ITree data = trees.get(index);
					if(data != null)
					{
						loadTree(displayTreePanel, ((RemoteTree)data), layoutType);
					}
				}
			}

		}));

		hPanel.add(new Button("Cancel", new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent arg0)
			{
				displayTreePanel.hide();
			}
		}));

		vPanel.add(hPanel);

		displayTreePanel.show();

		treeList.getTreeList(new AsyncCallback<List<ITree>>()
		{

			@Override
			public void onFailure(Throwable arg0)
			{
				Window.alert(arg0.getMessage());
			}

			@Override
			public void onSuccess(List<ITree> trees)
			{	
				Phyloviewer.this.trees = new ArrayList<ITree>();
				for (ITree tree : trees) {
//					System.out.println(tree.getId() + ":" + tree.getName());
//					((RemoteTree)tree).setPublic(true);
					if ( ((RemoteTree)tree).isPublic() ) {
//						System.out.println(tree.getId() + ":" + tree.getName());
						lb.addItem(tree.getName());
						Phyloviewer.this.trees.add(tree);
					}
				}

				label.setVisible(false);
				lb.setVisible(true);
			}
		});
	}
	
	private void updateStyle() {
		final String styleID = Window.Location.getParameter("styleID");
		
		if (styleID == null) {
			return;
		}
		
		AsyncCallback<IStyleMap> callback = new AsyncCallback<IStyleMap>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert("Failed to get style " + styleID + "\n" + caught.getMessage());
			}

			@Override
			public void onSuccess(IStyleMap style)
			{
				IDocument document = widget.getDocument();
				if (document != null) {
					document.setStyleMap(style);
					widget.render();
				}
			}
		};
		
		StyleServiceClient.getStyle(styleID, callback);
	}
	
	private String getSVG()
	{
		SVGGraphics graphics = new SVGGraphics();
		graphics.setSize(widget.getView().getOffsetWidth(), widget.getView().getOffsetHeight());
		widget.getView().renderTo(graphics);
		return graphics.toString();
	}
}
