package org.iplantc.phyloviewer.viewer.client;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class FileUploadPopup extends PopupPanel implements HasValueChangeHandlers<String>
{


	final FlowPanel panelImages = new FlowPanel();
	StringBuilder content = new StringBuilder();

	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
	  public void onFinish(IUploader uploader)
	  {
	    if (uploader.getStatus() == Status.SUCCESS)
	    {

	      new PreloadedImage(uploader.fileUrl(), showImage);  
	      UploadedInfo info = uploader.getServerInfo();
	      String[] files = info.message.split(";");
	      for (String file : files) {
	    	  File f = new File(file);
	    	  try {
		    	  BufferedReader reader = new BufferedReader(new FileReader(f));
		    	  String line = null;
		    	  while ((line = reader.readLine()) != null) {
		    		  content.append(line);
		    	  }
		    	  reader.close();
	    	  } catch (IOException ioe) {
	    		  System.out.println("Error reading file: " + info.name);
	    		  ioe.printStackTrace();
	    	  }
	      }
	      System.out.println("----------------------------------------------------------------");
	      System.out.println("File name          :" + info.name);
	      System.out.println("File content-type  :" + info.ctype);
	      System.out.println("File size:         :" + info.size);
	      System.out.println("Server message     :" + info.message);
	      System.out.println("----------------------------------------------------------------");
	      
	    }
	  }
	};
	  
	public FileUploadPopup()
	{
	
		VerticalPanel verticalPanel_all = new VerticalPanel();
		panelImages.add(verticalPanel_all);
		this.add(panelImages);

	    MultiUploader defaultUploader = new MultiUploader();
	    verticalPanel_all.add(defaultUploader);
	    defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

		HorizontalPanel horizontalPanel_OK = new HorizontalPanel();
		horizontalPanel_OK.setStyleName("gwt-horizonalPanel-OK");
		horizontalPanel_OK.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_all.add(horizontalPanel_OK);
		verticalPanel_all.setCellHorizontalAlignment(horizontalPanel_OK, HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_OK.setWidth("150px");

		Button btn_Apply = new Button("Apply", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
		    	ValueChangeEvent.fire(FileUploadPopup.this, content.toString());
				FileUploadPopup.this.hide();
			}
		});	

		btn_Apply.setText("Apply");
		horizontalPanel_OK.add(btn_Apply);
		btn_Apply.setWidth("58px");
		horizontalPanel_OK.setCellHorizontalAlignment(btn_Apply, HasHorizontalAlignment.ALIGN_RIGHT);

		Button btn_cancel = new Button("Cancel", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				FileUploadPopup.this.hide();
			}
		});	

		btn_cancel.setText("Cancel");
		horizontalPanel_OK.add(btn_cancel);
		btn_cancel.setWidth("59px");
		horizontalPanel_OK.setCellHorizontalAlignment(btn_cancel, HasHorizontalAlignment.ALIGN_RIGHT);

	}

	  // Attach an image to the pictures viewer
	  public OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler()
	  {
	    public void onLoad(PreloadedImage image)
	    {
	      image.setWidth("75px");
	      panelImages.add(image);
	    }
	  };
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
