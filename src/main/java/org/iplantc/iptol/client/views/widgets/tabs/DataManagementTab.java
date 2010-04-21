package org.iplantc.iptol.client.views.widgets.tabs;

import java.util.ArrayList;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.OnFinishUploaderHandler;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.JsonBuilder;
import org.iplantc.iptol.client.dialogs.ImportDialog;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.views.widgets.UploadPanel;
import org.iplantc.iptol.client.views.widgets.panels.DataManagementGridPanel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;

public class DataManagementTab extends WorkspaceTab 
{	
	private VerticalPanel panel;
	private DataManagementGridPanel pnlDataManagementGrid;
		
	//////////////////////////////////////////
	//constructor
	public DataManagementTab(String idWorkspace) 
	{
		super(idWorkspace,displayStrings.myData(),Type.DATA_MANAGEMENT);
	}
	
	//////////////////////////////////////////
	//private methods
	private void promptUpload(final String idParent,Point p)
	{	
		final Dialog dlgUpload= new Dialog();
		
		OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() 
		{
			public void onFinish(IUploader uploader)
			{
				if(uploader.getStatus() == Status.SUCCESS)
				{					
					String response = uploader.getServerResponse();
					
					if(response != null)
					{	
						JSONObject obj = JSONParser.parse(response).isObject();
						JsArray<FileInfo> fileInfos = JsonBuilder.asArrayofFileData(obj.get("created").toString());
						ArrayList<String> deleteIds = null;
						JSONArray arr = null; 
						if(obj.get("deletedIds")!=null ) {
							arr = obj.get("deletedIds").isArray();
						}
						StringBuffer sb = null;
						
						if(arr!=null) {
							deleteIds = new ArrayList<String>();
							//remove sorrounding quotes
							for (int i=0;i<arr.size();i++) {
								sb = new StringBuffer(arr.get(i).toString());
								sb.deleteCharAt(0);
								sb.deleteCharAt(sb.length() - 1);
								deleteIds.add(sb.toString());
							}
						}
						//there is always only one record
						if(fileInfos != null)
						{
							FileInfo info = fileInfos.get(0);
							
							if(info != null)
							{
								EventBus eventbus = EventBus.getInstance();
								FileUploadedEvent event = new FileUploadedEvent(idParent,info,deleteIds );							
								eventbus.fireEvent(event);
							
								Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());						
							}						
						}				
					}	

				}
				else
				{
					IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
					MessageBox.alert(displayStrings.fileUpload(),errorStrings.fileUploadFailed(),null);
				}
				
				if(dlgUpload != null)
				{
					dlgUpload.hide();
				}
			}
		};
		
		UploadPanel upload_panel = new UploadPanel(idWorkspace,idParent,displayStrings.uploadYourData(),onFinishUploaderHandler);
		upload_panel.assembleComponents();
				
		dlgUpload.add(upload_panel);
		dlgUpload.setPagePosition(p);
		dlgUpload.setHeaderVisible(true);
		dlgUpload.setHeading(displayStrings.uploadAFile());
		dlgUpload.setButtons(Dialog.CANCEL);
		dlgUpload.setHideOnButtonClick(true);
		dlgUpload.setResizable(false);
		dlgUpload.setWidth(375);
		dlgUpload.setModal(true);
		dlgUpload.show();	
	}
	
	//////////////////////////////////////////
	private void doCreateFolder()
	{
		if(pnlDataManagementGrid != null)
		{
			pnlDataManagementGrid.promptForFolderCreate();
		}		
	}

	//////////////////////////////////////////
	private void promptForImport(Point p)
	{
		String idFolder = pnlDataManagementGrid.getUploadParentId();
		
		//do we have an item selected?
		if(idFolder != null)
		{
			ImportDialog dlg = new ImportDialog(p,idWorkspace,idFolder);
			dlg.show();
		}
	}
	
	//////////////////////////////////////////
	private MenuItem buildImportMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.tagImport());
		Menu sub = new Menu();
		
		MenuItem item = new MenuItem(displayStrings.phylota());
		item.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				promptForImport(ce.getXY());
			}
		});
		
		//add our item to our sub-menu
		sub.add(item);
		ret.setSubMenu(sub);
		
		return ret;	
	}	
	
	//////////////////////////////////////////
	private MenuBarItem buildFileMenu()
	{
		Menu menu = new Menu();  
		
		MenuItem item = new MenuItem(displayStrings.tagNew());  
		menu.add(item);  
		
		//new folder menu item       
		Menu sub = new Menu();  
		sub.add(new MenuItem(displayStrings.folder(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				doCreateFolder();
			}
		}));  
		
		item.setSubMenu(sub);  
		
		//import menu item
		menu.add(buildImportMenuItem());
		
		//upload menu item
		item = new MenuItem(displayStrings.upload(),new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				String parentId = pnlDataManagementGrid.getUploadParentId();
				promptUpload(parentId,ce.getXY());
			}
		});
		
		menu.add(item);
					
		return new MenuBarItem(displayStrings.file(),menu);
	}
			
	//////////////////////////////////////////
	private MenuBar buildMenuBar()
	{
		MenuBar ret = new MenuBar();  
		
		ret.setBorders(true);  
		ret.setStyleAttribute("borderTop","none");  
		ret.add(buildFileMenu());
		ret.add(buildHelpMenu());
		
		return ret;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void buildTabBody() 
	{	
		add(buildMenuBar());
		
		panel = new VerticalPanel();
		panel.setSpacing(15);
		
		pnlDataManagementGrid = new DataManagementGridPanel(idWorkspace,displayStrings.availableFiles()); 
		panel.add(pnlDataManagementGrid);
		
		add(panel);		
	}

	//////////////////////////////////////////
	@Override
	protected void doAboutDisplay()
	{
		Window.open("help/about.html",displayStrings.about(),null);
	}
	
	//////////////////////////////////////////
	@Override
	protected void doHelpContentDisplay()  
	{
		Window.open("help/mydata.html",displayStrings.help(),null);		
	}
}
