package org.iplantc.de.client.views.widgets.portlets.panels;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.models.FileIdentifier;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public abstract class ProvenanceContentPanel extends ContentPanel 
{
	///////////////////////////////////////
	//private variables
	protected TextArea areaProvenance;
	protected BorderLayoutData centerData;
	protected BorderLayoutData southData;
	protected FileIdentifier file;
	protected boolean dirty = false;
	protected static DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	///////////////////////////////////////
	//constructor
	protected ProvenanceContentPanel(FileIdentifier file)
	{
		super();
		this.file = file;		
		setHeight(380);
		setHeaderVisible(false);
		
		areaProvenance = buildTextArea(false);		
		centerData = buildCenterData();
	}
	
	///////////////////////////////////////
	//protected methods	
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
			  
		setLayout(new BorderLayout());		
	}
	
	///////////////////////////////////////
	protected BorderLayoutData buildCenterData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.CENTER,400);  
		ret.setMargins(new Margins(0,0,0,0));
		
		return ret;
	}

	///////////////////////////////////////
	protected BorderLayoutData buildSouthData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.SOUTH,80);  
		
		ret.setSplit(true);  
		ret.setCollapsible(true);
		ret.setFloatable(false);
	    ret.setMargins(new Margins(0,0,0,0));
		ret.setMinSize(36);
		
		return ret;
	}
	
	///////////////////////////////////////
	protected TextArea buildTextArea(boolean editable)
	{
		TextArea ret = new TextArea();
		
		ret.setStyleName("iplantc-caption-label");		
		ret.setHideLabel(true);			
				
		if(!editable)
		{		
			ret.setAutoValidate(false);
			ret.setHideLabel(true);				
			ret.setReadOnly(true);				
		}
		
		return ret;
	}
	
	///////////////////////////////////////
	protected String formatProvenance(String in)
	{
		String ret = in;
		
		if(in != null)
		{			
			ret = ret.replace("\\t","\t");
			ret = ret.replace("\\r\\n","\n");
			ret = ret.replace("\\r","\n");
		}

		return ret;
	}
	
	///////////////////////////////////////
	//public methods
	public void updateProvenance(String provenance)
	{
		if(provenance != null && provenance.length() > 0)
		{			
			areaProvenance.setValue(formatProvenance(provenance));
			
			if(southData == null)
			{
				southData = buildSouthData();
				add(areaProvenance,southData);
			}						
		}
		else
		{
			areaProvenance.hide();			
		}
	}
	
	///////////////////////////////////////
	public void setFileIdentifier(FileIdentifier file)
	{
		this.file = file;
	}
	
	///////////////////////////////////////
	public FileIdentifier getFileIdentifier()
	{
		return file;
	}
	
	///////////////////////////////////////
	public boolean isDirty()
	{
		return dirty;
	}
			
	///////////////////////////////////////
	public abstract String getTabHeader();

	///////////////////////////////////////
	public abstract int getTabIndex();	
}