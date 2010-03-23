package org.iplantc.iptol.client.views.widgets;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFileDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderDeletedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEventHandler;
import org.iplantc.iptol.client.images.Resources;
import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.File;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Folder;
import org.iplantc.iptol.client.services.FileDeleteCallback;
import org.iplantc.iptol.client.services.FolderDeleteCallback;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.views.widgets.panels.TreeStoreManager;
import org.iplantc.iptol.client.views.widgets.panels.TreeStoreWrapper;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A widget that displays files/folders in tree structure.
 * provides context menu for uploading, downloading etc...
 * @author sriram
 *
 */
public class DataBrowserTree extends ContentPanel
{
	private Dialog upload_dialog;
	private HandlerManager eventbus;
	private Button options = new Button();
	private String idWorkspace;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private TreePanel<DiskResource> treePanel = new TreePanel<DiskResource>(storeWrapper.getStore());
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);

	public DataBrowserTree(String idWorkspace,HandlerManager eventbus)
	{
		this.idWorkspace = idWorkspace;
		this.eventbus = eventbus;

		setScrollMode(Scroll.AUTO);

		initEventHandlers();
	}

	/**
	 * Put together the widget
	 */
	public void assembleView()
	{
		treePanel.setBorders(true);
		treePanel.setDisplayProperty("name");
		treePanel.setContextMenu(buildFolderContextMenu());
		treePanel.setAutoHeight(true);
		treePanel.setWidth(195);
		
		//disable multi-select
		TreePanelSelectionModel<DiskResource> sm = new TreePanelSelectionModel<DiskResource>();
		sm.setSelectionMode(SelectionMode.SINGLE);
		treePanel.setSelectionModel(sm);  
		
		add(treePanel);
		
		setHeading(displayStrings.dataBrowser());

		options.setScale(ButtonScale.SMALL);
		options.setArrowAlign(ButtonArrowAlign.RIGHT);
		options.setIcon(Resources.ICONS.listItems());
		options.setMenu(buildOptionsMenu());

		getHeader().addTool(options);

		//provide icons to the tree nodes
		treePanel.setIconProvider(new ModelIconProvider<DiskResource>()
		{
			@Override
			public AbstractImagePrototype getIcon(DiskResource model)
			{
				 if(model instanceof Folder)
				 {
					 return (treePanel.isExpanded(model)) ? treePanel.getStyle().getNodeOpenIcon() : treePanel.getStyle().getNodeCloseIcon();
				 }
				 else
				 {
					 return Resources.ICONS.green();
				 }
			}
		});

		//retrieve all the files that have been uploaded already
		refreshTree();

		//load info about the file on the status bar
		treePanel.addListener(Events.OnClick,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				DiskResource folder = treePanel.getSelectionModel().getSelectedItem();

				//set context menu
				if(folder instanceof Folder)
				{
					treePanel.setContextMenu(buildFolderContextMenu());
				}
				else
				{
					treePanel.setContextMenu(buildFileContextMenu());
				}

				DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(folder);
	 			eventbus.fireEvent(event);
			}
		});
	}

	private MenuItem buildCreateFolderMenuItem()
	{
		MenuItem ret = new MenuItem();
		
		ret.setText(displayStrings.createFolder());
		ret.setIcon(Resources.ICONS.add());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootFolderId(),eventbus));
				dlg.show();
			}
		});
		
		return ret;
	}

	private MenuItem buildHelpMenuItem()
	{
		MenuItem ret = new MenuItem();
		
		ret.setText(displayStrings.help());
		ret.setIcon(Resources.ICONS.user());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				//TODO: implement me
			}
		});
		
		return ret;
	}
	private Menu buildOptionsMenu()
	{
		final Menu optionsMenu = new Menu();
		
		optionsMenu.add(buildFileUploadMenuItem());
		optionsMenu.add(buildCreateFolderMenuItem());
		optionsMenu.add(buildHelpMenuItem());
		
		return optionsMenu;
	}

	private MenuItem buildFileUploadMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setId("upload_menu_item");
		ret.setText(displayStrings.upload());
		ret.setIcon(Resources.ICONS.upload());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				//do we have an item selected?
				if(selected != null)
				{
					//do we have a folder selected?
					if(selected instanceof Folder)
					{
						promptUpload(selected.getId(),ce.getXY());
					}
					else
					{	
						//we have a file selected - let's upload to the parent
						Folder parent = (Folder)selected.getParent();
						
						promptUpload(parent.getId(),ce.getXY());
					}
				}
				else
				{
					// nothing is selected - let's upload to the default upload folder
					promptUpload(storeWrapper.getUploadFolderId(),ce.getXY());
				}
			}
		});

		return ret;
	}

	private MenuItem buildContextFileUploadMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setId("upload_menu_item");
		ret.setText(displayStrings.upload());
		ret.setIcon(Resources.ICONS.upload());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					promptUpload(selected.getId(),ce.getXY());
				}
			}
		});

		return ret;
	}

	private MenuItem buildFolderRenameMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.rename());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					IPlantDialog dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFolderDialogPanel(idWorkspace,selected.getId(),selected.getName(),eventbus));
					dlg.show();
				}
			}
		});

		return ret;
	}

	private MenuItem buildFolderDeleteMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.delete());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					String id = selected.getId();
					FolderServices.deleteFolder(idWorkspace,id,new FolderDeleteCallback(eventbus,id));
				}
			}
		});

		return ret;
	}

	private MenuItem buildFileDeleteMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.delete());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					String id  = selected.getId();
					FolderServices.deleteFile(id,new FileDeleteCallback(eventbus,id));
				}
			}
		});

		return ret;
	}
	
	private MenuItem buildFileDownloadMenuItem()
	{
		MenuItem ret = new MenuItem();
		
		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.downloadFile());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				final DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
				
				if(selected != null)
				{
					String address = "http://" + Window.Location.getHostName() + ":14444/files/" + selected.getId() + "/content";
					Window.open(address,null,null);			
				}
			}
		});	
		
		return ret;
	}
	
	private MenuItem buildFileEditMenuItem()
	{
		MenuItem ret = new MenuItem();
		
		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					GetDataEvent event = new GetDataEvent(GetDataEvent.DataType.RAW,selected.getId(),selected.getName());
					eventbus.fireEvent(event);
				}
			}
		});	
		
		return ret;
	}
	
	private MenuItem buildFileRenameMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.rename());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					IPlantDialog dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFileDialogPanel(selected.getId(),selected.getName(),eventbus));
					dlg.show();
				}
			}
		});

		return ret;	
	}
	
	private Menu buildFolderContextMenu()
	{
		Menu contextMenu = new Menu();

		contextMenu.add(buildContextFileUploadMenuItem());
		contextMenu.add(buildFolderRenameMenuItem());
		contextMenu.add(buildFolderDeleteMenuItem());

		return contextMenu;
	}
	
	private Menu buildFileContextMenu()
	{
		Menu contextMenu = new Menu();
		
		contextMenu.add(buildFileEditMenuItem());
		contextMenu.add(buildFileRenameMenuItem());
		contextMenu.add(buildFileDeleteMenuItem());
		contextMenu.add(buildFileDownloadMenuItem());
		
		return contextMenu;
	}

	private void promptUpload(String idParent,Point p)
	{
		UploadPanel upload_panel = new UploadPanel(idWorkspace,idParent,displayStrings.uploadYourData(),onFinishUploaderHandler);
		upload_panel.assembleComponents();

		upload_dialog = new Dialog();
		upload_dialog.add(upload_panel);
		upload_dialog.setPagePosition(p);
		upload_dialog.setHeaderVisible(true);
		upload_dialog.setHeading(displayStrings.uploadAFile());
		upload_dialog.setButtons(Dialog.CANCEL);
		upload_dialog.setHideOnButtonClick(true);
		upload_dialog.setWidth(375);
		upload_dialog.show();
	}

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
		private Folder getFolder()
		{
			TreeStoreManager mgr = TreeStoreManager.getInstance();			
			Folder ret = mgr.getUploadFolder(storeWrapper); 
					
			DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
			
			if(selected != null)
			{
				//if we have a file... let's return the parent's id
				if(selected instanceof File)
				{
					File file = (File)selected;
					ret = (Folder)file.getParent();					
				}
				else if(selected instanceof Folder)
				{
					ret = (Folder)selected;
				}	
			}
			
			return ret;
		}
		
		public void onFinish(IUploader uploader)
		{
			if(uploader.getStatus() == Status.SUCCESS)
			{			
				Folder folder = getFolder();
				
				String response = uploader.getServerResponse();

				if(response != null)
				{	
					JsArray<FileInfo> fileInfos = asArrayofFileData(response);

					//there is always only one record
					if(fileInfos != null)
					{
						FileInfo info = fileInfos.get(0);
						
						if(info != null)
						{
							FileUploadedEvent event = new FileUploadedEvent(folder.getId(),info);							
							eventbus.fireEvent(event);
						
							Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());						
						}						
					}					
				}

				treePanel.setExpanded(folder,true);
			}
			else
			{
				MessageBox.alert(displayStrings.fileUpload(),displayStrings.fileUploadFailed(),null);
			}
			
			if(upload_dialog != null)
			{
				upload_dialog.hide();
			}
		}
	};

	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;

	private void refreshTree()
	{
		FolderServices.getFiletree(idWorkspace,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				//TODO: handle failure
			}

			@Override
			public void onSuccess(String result)
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				
				mgr.updateWrapper(storeWrapper,result);	
				
				treePanel.expandAll();
			}
		});
	}
	
	private void highlightItem(DiskResource resource)
	{
		if(resource != null)
		{
			treePanel.getSelectionModel().select(resource,false);	
			
			DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(resource);
			eventbus.fireEvent(clickevent);
		}
	}
	
	private void initEventHandlers()
	{	
		eventbus.addHandler(FolderCreatedEvent.TYPE,new FolderCreatedEventHandler()
		{
			@Override
			public void onCreated(FolderCreatedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				Folder folder = mgr.doFolderCreate(storeWrapper,event.getId(),event.getName());
				
				highlightItem(folder);
			}
		});
		
		eventbus.addHandler(FolderRenamedEvent.TYPE,new FolderRenamedEventHandler()
		{
			@Override
			public void onRenamed(FolderRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				Folder folder = mgr.doFolderRename(storeWrapper,event.getId(),event.getName());		
				
				highlightItem(folder);
			}
		});
		
		eventbus.addHandler(FolderDeletedEvent.TYPE,new FolderDeletedEventHandler()
		{
			@Override
			public void onDeleted(FolderDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFolderDelete(storeWrapper,event.getId());		
				
				DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(null);
				eventbus.fireEvent(clickevent);				
			}
		});	
		
		eventbus.addHandler(FileUploadedEvent.TYPE,new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				File file = mgr.doFileAdd(storeWrapper,event.getParentId(),event.getFileInfo());
				
				highlightItem(file);
			}
		});	

		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				File file = mgr.doFileRename(storeWrapper,event.getId(),event.getName());
			
				if(file != null)
				{
					highlightItem(file);
				}		
			}
		});	
		
		eventbus.addHandler(FileDeletedEvent.TYPE,new FileDeletedEventHandler()
		{
			@Override
			public void onDeleted(FileDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFileDelete(storeWrapper,event.getId());
				
				DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(null);
				eventbus.fireEvent(clickevent);
			}
		});		
	}
}
