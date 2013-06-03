package org.iplantc.phyloviewer.viewer.client;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.iplantc.phyloviewer.viewer.client.ui.ColorBox;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class MetadataDialoguePopup extends PopupPanel implements HasValueChangeHandlers<String>
{

	final LayoutPanel metadataLayoutPanel = new LayoutPanel() ;
	final FileUpload fileUploadDialogue = new FileUpload() ;
	final ListBox listBox_uploadData = new ListBox() ;
	int selection ;
	
	JSONObject jsonObject_read                  = new JSONObject() ;
	JSONObject jsonObject_readStyles            = new JSONObject() ;
	JSONObject jsonObject_readNameStyleMappings = new JSONObject() ;
	
	final JSONObject jsonObject_apply                  = new JSONObject() ;
	final JSONObject jsonObject_applyStyles            = new JSONObject() ;
	final JSONObject jsonObject_applyNameStyleMappings = new JSONObject() ;
	final JSONObject jsonObject_applyNodeStyleMappings = new JSONObject() ;

	private ColorBox createColorBox()
	{
	  ColorBox colorBox = new ColorBox();
	  colorBox.addStyleName("{hash:true,required:false}"); // jscolor config
	  return colorBox;
	}

	private final native void initColorPicker()
	/*-{
	  $wnd.jscolor.init();
	}-*/;

	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
		public void onFinish( IUploader uploader )
		{
			if ( uploader.getStatus() == Status.SUCCESS )
			{

				UploadedInfo info = uploader.getServerInfo() ;
				String[] files = info.message.split( ";" ) ;

				for ( String file : files )
				{
					File f = new File( file ) ;
					try
					{
						BufferedReader reader = new BufferedReader( new FileReader(f) ) ;

						String line = null ;
						StringBuilder content = new StringBuilder() ;

						while ( (line = reader.readLine()) != null )
						{
							content.append( line ) ;
						}

						reader.close() ;

						jsonObject_read = (JSONObject) JSONParser.parseStrict( content.toString() ) ;
						jsonObject_readStyles = (JSONObject) jsonObject_read.get("styles") ;
						jsonObject_readNameStyleMappings = (JSONObject) jsonObject_read.get("nameStyleMappings") ;

						for ( Iterator<String> it = jsonObject_readNameStyleMappings.keySet().iterator() ; it.hasNext() ; )
						{
							String name_readNameStyleMappings = it.next() ;
							addListItem( name_readNameStyleMappings , name_readNameStyleMappings ) ;
							String styleName = ((JSONString) jsonObject_readNameStyleMappings.get(name_readNameStyleMappings)).stringValue() ;

							JSONObject jsonObject_style = (JSONObject) jsonObject_readStyles.get( styleName ) ;
							
							JSONObject jsonObject_nodeStyle = (JSONObject) jsonObject_style.get("nodeStyle") ;
							String nodeStyleColor = new String(((JSONString) jsonObject_nodeStyle.get("color")).stringValue());
							double nodeStylePointSize = ((JSONNumber) jsonObject_nodeStyle.get("pointSize")).doubleValue();
							String nodeStyleNodeShape = new String(((JSONString) jsonObject_nodeStyle.get("nodeShape")).stringValue());

							JSONObject jsonObject_labelStyle = (JSONObject) jsonObject_style.get("labelStyle") ;
							String labelStyleColor = new String(((JSONString) jsonObject_labelStyle.get("color")).stringValue()) ;

							JSONObject jsonObject_branchStyle = (JSONObject) jsonObject_style.get("branchStyle") ;
							String branchStyleStrokeColor = new String(((JSONString) jsonObject_branchStyle.get("strokeColor")).stringValue()) ;
							double branchStyleLineWidth = ((JSONNumber) jsonObject_branchStyle.get("lineWidth")).doubleValue() ;

							JSONObject jsonObject_glyphStyle = (JSONObject) jsonObject_style.get("glyphStyle") ;
							String glyphStyleFillColor = new String(((JSONString) jsonObject_glyphStyle.get("fillColor")).stringValue()) ;
							String glyphStyleStrokeColor = new String(((JSONString) jsonObject_glyphStyle.get("strokeColor")).stringValue()) ;
							double glyphStyleLineWidth = ((JSONNumber) jsonObject_glyphStyle.get("lineWidth")).doubleValue() ;

							JSONObject jsonObject_applyNodeStyle = new JSONObject() ;
							jsonObject_applyNodeStyle.put( "color" ,  new JSONString( nodeStyleColor ) ) ;
							jsonObject_applyNodeStyle.put( "pointSize" , new JSONNumber( nodeStylePointSize ) ) ;
							jsonObject_applyNodeStyle.put( "nodeShape" , new JSONString( nodeStyleNodeShape ) ) ;

							JSONObject jsonObject_applyLabelStyle = new JSONObject() ;
							jsonObject_applyLabelStyle.put( "color" , new JSONString( labelStyleColor ) ) ;

							JSONObject jsonObject_applyBranchStyle = new JSONObject() ;
							jsonObject_applyBranchStyle.put( "strokeColor" , new JSONString( branchStyleStrokeColor ) ) ;
							jsonObject_applyBranchStyle.put( "lineWidth" , new JSONNumber( branchStyleLineWidth ) ) ;

							JSONObject jsonObject_applyGlyphStyle = new JSONObject() ;
							jsonObject_applyGlyphStyle.put( "fillColor" , new JSONString( glyphStyleFillColor ) ) ;
							jsonObject_applyGlyphStyle.put( "strokeColor" , new JSONString( glyphStyleStrokeColor )  ) ;
							jsonObject_applyGlyphStyle.put( "lineWidth" , new JSONNumber( glyphStyleLineWidth ) ) ;

							JSONObject jsonObject_build = new JSONObject() ;
							jsonObject_build.put( "id" , new JSONString( name_readNameStyleMappings ) ) ;
							jsonObject_build.put( "nodeStyle" , jsonObject_applyNodeStyle ) ;
							jsonObject_build.put( "labelStyle" , jsonObject_applyLabelStyle ) ;
							jsonObject_build.put( "branchStyle" , jsonObject_applyBranchStyle ) ;
							jsonObject_build.put( "glyphStyle" , jsonObject_applyGlyphStyle ) ;

							jsonObject_applyStyles.put( name_readNameStyleMappings , jsonObject_build ) ;
							jsonObject_applyNameStyleMappings.put( name_readNameStyleMappings , new JSONString( name_readNameStyleMappings ) ) ;

						}
						
						jsonObject_apply.put( "styles" , jsonObject_applyStyles ) ;
						jsonObject_apply.put( "nameStyleMappings" , jsonObject_applyNameStyleMappings ) ;
						jsonObject_apply.put( "nodeStyleMappings" , jsonObject_applyNodeStyleMappings ) ;

					}
					catch ( IOException ioe )
					{
						System.out.println( "Error reading file: " + info.name ) ;
						ioe.printStackTrace() ;
					}
				}
				//System.out.println("----------------------------------------------------------------");
				//System.out.println("File name          :" + info.name);
				//System.out.println("File content-type  :" + info.ctype);
				//System.out.println("File size:         :" + info.size);
				//System.out.println("Server message     :" + info.message);
				//System.out.println("----------------------------------------------------------------");

			}
		}
	};


	public MetadataDialoguePopup()
	{


		VerticalPanel verticalPanel_all = new VerticalPanel();
		verticalPanel_all.add(metadataLayoutPanel);
		verticalPanel_all.setSize("690px", "400px");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel_all.add(horizontalPanel);
		horizontalPanel.setWidth("690px");

		Label lblH1_treeMarkup = new Label("Tree Markup");
		lblH1_treeMarkup.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		lblH1_treeMarkup.setStyleName("gwt-Label-TreeMarkup");
		horizontalPanel.add(lblH1_treeMarkup);
		horizontalPanel.setCellVerticalAlignment(lblH1_treeMarkup, HasVerticalAlignment.ALIGN_MIDDLE);

		Image image_iPlantGlobalLogo = new Image("images/iPlantGlobalLogo.jpg");
		image_iPlantGlobalLogo.setStyleName((String) null);
		horizontalPanel.add(image_iPlantGlobalLogo);
		horizontalPanel.setCellHeight(image_iPlantGlobalLogo, "45");
		horizontalPanel.setCellWidth(image_iPlantGlobalLogo, "155");
		image_iPlantGlobalLogo.setSize("155", "45");
		horizontalPanel.setCellHorizontalAlignment(image_iPlantGlobalLogo, HasHorizontalAlignment.ALIGN_RIGHT);

		Label lblH2_filePath = new Label("Select analysis data to apply to tree:");
		lblH2_filePath.setStyleName("gwt-Label-MetadataDialoguePopup_styles_h3");
		verticalPanel_all.add(lblH2_filePath);

	    MultiUploader defaultUploader = new MultiUploader();
	    verticalPanel_all.add(defaultUploader);
	    defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

		Label lblH3_selectElement = new Label("Select the element to refine styling types:");
		lblH3_selectElement.setStyleName("gwt-Label-MetadataDialoguePopup_styles_h3");
		verticalPanel_all.add(lblH3_selectElement);

		HorizontalPanel horizontalPanel_dataDetected = new HorizontalPanel();
		verticalPanel_all.add(horizontalPanel_dataDetected);
		horizontalPanel_dataDetected.setSize("685px", "200px");

		VerticalPanel verticalPanel_dataDetected = new VerticalPanel();
		verticalPanel_dataDetected.setStyleName("gwt-verticalPanel-SelectAnalysisApply");
		horizontalPanel_dataDetected.add(verticalPanel_dataDetected);
		verticalPanel_dataDetected.setSize("320px", "200px");

		Label lbl_dataDetected = new Label("Data Detected");
		verticalPanel_dataDetected.add(lbl_dataDetected);
		lbl_dataDetected.setHeight("25px");

		verticalPanel_dataDetected.add(listBox_uploadData);
		listBox_uploadData.setSize("300px", "350px");
		listBox_uploadData.setVisibleItemCount(15);

		final TextBox textBox_id = new TextBox() ;

		VerticalPanel verticalPanel_right = new VerticalPanel();

		// id

		// id: horizontal panel
		HorizontalPanel horizontalPanel_id = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_id ) ;
		horizontalPanel_id.setWidth( "225px" ) ;

		// id: label
		Label label_id = new Label( "Element properties initially from:" ) ;
		horizontalPanel_id.add( label_id ) ;

		// id: item - i.e. textbox, listbox, etc.
		//TextBox textBox_id = new TextBox() ;
		horizontalPanel_id.add( textBox_id ) ;
		horizontalPanel_id.setCellHorizontalAlignment( textBox_id , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		horizontalPanel_id.setCellVerticalAlignment( textBox_id , HasVerticalAlignment.ALIGN_BOTTOM ) ;
		textBox_id.setWidth( "118px" ) ;

		// nodestyle

		Label label_nodestyle = new Label( "Node Style" ) ;
		label_nodestyle.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h1" ) ;
		verticalPanel_right.add( label_nodestyle ) ;


		// nodeColor

		// nodeColor: horizontal panel
		HorizontalPanel horizontalPanel_nodeColor = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_nodeColor ) ;
		horizontalPanel_nodeColor.setWidth( "225px" ) ;

		// nodeColor: label
		Label label_nodeColor = new Label( "Color:" ) ;
		label_nodeColor.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_nodeColor.add( label_nodeColor ) ;

		// nodeColor: item - i.e. textbox, listbox, etc.
		final ColorBox textBox_nodeColor = createColorBox();
		horizontalPanel_nodeColor.add( textBox_nodeColor ) ;
		horizontalPanel_nodeColor.setCellHorizontalAlignment( textBox_nodeColor , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_nodeColor.setWidth( "118px" ) ;


		// nodePointSize

		// nodePointSize: horizontal panel
		HorizontalPanel horizontalPanel_nodePointSize = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_nodePointSize ) ;
		horizontalPanel_nodePointSize.setWidth( "225px" ) ;

		// nodePointSize: label
		Label label_nodePointSize = new Label( "Point size:" ) ;
		label_nodePointSize.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_nodePointSize.add( label_nodePointSize ) ;

		// nodePointSize: item - i.e. textbox, listbox, etc.
		final TextBox textBox_nodePointSize = new TextBox() ;
		horizontalPanel_nodePointSize.add( textBox_nodePointSize ) ;
		horizontalPanel_nodePointSize.setCellHorizontalAlignment( textBox_nodePointSize , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_nodePointSize.setWidth( "118px" ) ;


		// nodeShape

		// nodeShape: horizontal panel
		HorizontalPanel horizontalPanel_nodeShape = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_nodeShape ) ;
		horizontalPanel_nodeShape.setWidth( "225px" ) ;

		// nodeShape: label
		Label label_nodeShape = new Label( "Node shape:" ) ;
		label_nodeShape.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_nodeShape.add( label_nodeShape ) ;

		// nodeShape: item - i.e. textbox, listbox, etc.
		final TextBox textBox_nodeShape = new TextBox() ;
		horizontalPanel_nodeShape.add( textBox_nodeShape ) ;
		horizontalPanel_nodeShape.setCellHorizontalAlignment( textBox_nodeShape , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_nodeShape.setWidth( "118px" ) ;


		// labelStyle

		Label label_labelStyle = new Label( "Label Style" ) ;
		label_labelStyle.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h1" ) ;
		verticalPanel_right.add( label_labelStyle ) ;


		// labelStyleColor: horizontal panel
		HorizontalPanel horizontalPanel_labelStyleColor = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_labelStyleColor ) ;
		horizontalPanel_labelStyleColor.setWidth( "225px" ) ;

		// labelStyleColor: label
		Label label_labelStyleColor = new Label( "Color:" ) ;
		label_labelStyleColor.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_labelStyleColor.add( label_labelStyleColor ) ;

		// labelStyleColor: item - i.e. textbox, listbox, etc.
		final ColorBox textBox_labelStyleColor = createColorBox();
		horizontalPanel_labelStyleColor.add( textBox_labelStyleColor ) ;
		horizontalPanel_labelStyleColor.setCellHorizontalAlignment( textBox_labelStyleColor , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_labelStyleColor.setWidth( "118px" ) ;


		// branchStyle

		Label label_branchStyle = new Label( "Branch style" ) ;
		label_branchStyle.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h1" ) ;
		verticalPanel_right.add( label_branchStyle ) ;


		// branchStyleStrokeColor: horizontal panel
		HorizontalPanel horizontalPanel_branchStyleStrokeColor = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_branchStyleStrokeColor ) ;
		horizontalPanel_branchStyleStrokeColor.setWidth( "225px" ) ;

		// branchStyleStrokeColor: label
		Label label_branchStyleStrokeColor = new Label( "Stroke color:" ) ;
		label_branchStyleStrokeColor.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_branchStyleStrokeColor.add( label_branchStyleStrokeColor ) ;

		// branchStyleStrokeColor: item - i.e. textbox, listbox, etc.
		final ColorBox textBox_branchStyleStrokeColor = createColorBox();		
		horizontalPanel_branchStyleStrokeColor.add( textBox_branchStyleStrokeColor ) ;
		horizontalPanel_branchStyleStrokeColor.setCellHorizontalAlignment( textBox_branchStyleStrokeColor , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_branchStyleStrokeColor.setWidth( "118px" ) ;


		// branchStyleLineWidth: horizontal panel
		HorizontalPanel horizontalPanel_branchStyleLineWidth = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_branchStyleLineWidth ) ;
		horizontalPanel_branchStyleLineWidth.setWidth( "225px" ) ;

		// branchStyleLineWidth: label
		Label label_branchStyleLineWidth = new Label( "Line width:" ) ;
		label_branchStyleLineWidth.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_branchStyleLineWidth.add( label_branchStyleLineWidth ) ;

		// branchStyleLineWidth: item - i.e. textbox, listbox, etc.
		final TextBox textBox_branchStyleLineWidth = new TextBox() ;
		horizontalPanel_branchStyleLineWidth.add( textBox_branchStyleLineWidth ) ;
		horizontalPanel_branchStyleLineWidth.setCellHorizontalAlignment( textBox_branchStyleLineWidth , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_branchStyleLineWidth.setWidth( "118px" ) ;


		// glyphStyle

		Label label_glyphStyle = new Label( "Glyph Style" ) ;
		label_glyphStyle.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h1" ) ;
		verticalPanel_right.add( label_glyphStyle ) ;


		// glyphStyleFillColor: horizontal panel
		HorizontalPanel horizontalPanel_glyphStyleFillColor = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_glyphStyleFillColor ) ;
		horizontalPanel_glyphStyleFillColor.setWidth( "225px" ) ;

		// glyphStyleFillColor: label
		Label label_glyphStyleFillColor = new Label( "Fill color:" ) ;
		label_glyphStyleFillColor.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_glyphStyleFillColor.add( label_glyphStyleFillColor ) ;

		// glyphStyleFillColor: item - i.e. textbox, listbox, etc.
		final ColorBox textBox_glyphStyleFillColor = createColorBox();		
		horizontalPanel_glyphStyleFillColor.add( textBox_glyphStyleFillColor ) ;
		horizontalPanel_glyphStyleFillColor.setCellHorizontalAlignment( textBox_glyphStyleFillColor , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_glyphStyleFillColor.setWidth( "118px" ) ;


		// glyphStyleStrokeColor: horizontal panel
		HorizontalPanel horizontalPanel_glyphStyleStrokeColor = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_glyphStyleStrokeColor ) ;
		horizontalPanel_glyphStyleStrokeColor.setWidth( "225px" ) ;

		// glyphStyleStrokeColor: label
		Label label_glyphStyleStrokeColor = new Label( "Stroke color:" ) ;
		label_glyphStyleStrokeColor.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_glyphStyleStrokeColor.add( label_glyphStyleStrokeColor ) ;

		// glyphStyleStrokeColor: item - i.e. textbox, listbox, etc.
		final ColorBox textBox_glyphStyleStrokeColor = createColorBox();		
		horizontalPanel_glyphStyleStrokeColor.add( textBox_glyphStyleStrokeColor ) ;
		horizontalPanel_glyphStyleStrokeColor.setCellHorizontalAlignment( textBox_glyphStyleStrokeColor , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_glyphStyleStrokeColor.setWidth( "118px" ) ;


		// glyphStyleLineWidth: horizontal panel
		HorizontalPanel horizontalPanel_glyphStyleLineWidth = new HorizontalPanel() ;
		verticalPanel_right.add( horizontalPanel_glyphStyleLineWidth ) ;
		horizontalPanel_glyphStyleLineWidth.setWidth( "225px" ) ;

		// glyphStyleLineWidth: label
		Label label_glyphStyleLineWidth = new Label( "Line width:" ) ;
		label_glyphStyleLineWidth.setStyleName( "gwt-Label-MetadataDialoguePopup_styles_h2" ) ;
		horizontalPanel_glyphStyleLineWidth.add( label_glyphStyleLineWidth ) ;

		// glyphStyleLineWidth: item - i.e. textbox, listbox, etc.
		final TextBox textBox_glyphStyleLineWidth = new TextBox() ;
		horizontalPanel_glyphStyleLineWidth.add( textBox_glyphStyleLineWidth ) ;
		horizontalPanel_glyphStyleLineWidth.setCellHorizontalAlignment( textBox_glyphStyleLineWidth , HasHorizontalAlignment.ALIGN_RIGHT ) ;
		textBox_glyphStyleLineWidth.setWidth( "118px" ) ;


		// button: attribute Apply
		HorizontalPanel horizontalPanel_btns = new HorizontalPanel();
		horizontalPanel_btns.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel_all.add(horizontalPanel_btns);
		verticalPanel_all.setCellHorizontalAlignment(horizontalPanel_btns, HasHorizontalAlignment.ALIGN_RIGHT);

		Button btn_editSelection = new Button("Edit Item", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{

				//ValueChangeEvent.fire( MetadataDialoguePopup.this , jsonObject_apply.toString() );

				String name = listBox_uploadData.getValue( selection ) ;
				JSONObject style = (JSONObject) jsonObject_applyStyles.get( name ) ;

				JSONObject jsonObject_nodeStyle = (JSONObject) style.get("nodeStyle") ;
				jsonObject_nodeStyle.put( "color" , new JSONString(textBox_nodeColor.getText())) ;
				jsonObject_nodeStyle.put( "pointSize" , new JSONNumber( Integer.parseInt(textBox_nodePointSize.getText()) )) ;
				jsonObject_nodeStyle.put( "nodeShape" , new JSONString( textBox_nodeShape.getText()) ) ;

				JSONObject jsonObject_labelStyle = (JSONObject) style.get("labelStyle") ;
				jsonObject_labelStyle.put( "color" , new JSONString(textBox_labelStyleColor.getText())) ;
				
				JSONObject jsonObject_branchStyle = (JSONObject) style.get("branchStyle") ;
				jsonObject_branchStyle.put( "strokeColor" , new JSONString(textBox_branchStyleStrokeColor.getText())) ;
				jsonObject_branchStyle.put( "lineWidth" , new JSONNumber( Integer.parseInt(textBox_branchStyleLineWidth.getText()) ) ) ;

				JSONObject jsonObject_glyphStyle = (JSONObject) style.get("glyphStyle") ;
				jsonObject_glyphStyle.put( "fillColor" , new JSONString(textBox_glyphStyleFillColor.getText())) ;
				jsonObject_glyphStyle.put( "strokeColor" , new JSONString(textBox_glyphStyleStrokeColor.getText())) ;
				jsonObject_glyphStyle.put( "lineWidth" , new JSONNumber( Integer.parseInt(textBox_glyphStyleLineWidth.getText()) ) ) ;

			}
		}); 
		btn_editSelection.setText("Edit Selection");
		horizontalPanel_btns.add(btn_editSelection);
		btn_editSelection.setStyleName(".gwt-Button-MetadataDialoguePopup_styles_01");
		horizontalPanel_btns.setCellHorizontalAlignment(btn_editSelection, HasHorizontalAlignment.ALIGN_RIGHT);

		horizontalPanel_dataDetected.add(verticalPanel_right);
		horizontalPanel_dataDetected.setCellHorizontalAlignment(verticalPanel_right, HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel_right.setSize("350px", "195px");

		Button btn_applyStyling = new Button("Apply Styling", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ValueChangeEvent.fire( MetadataDialoguePopup.this, jsonObject_apply.toString() );
				//MetadataDialoguePopup.this.hide() ;
			}
		});	
		btn_applyStyling.setText("Apply Styling");
		btn_applyStyling.setStyleName(".gwt-Button-MetadataDialoguePopup_styles_01");
		horizontalPanel_btns.add(btn_applyStyling);

		Button btn_close = new Button("Close", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				MetadataDialoguePopup.this.hide();
			}
		});	
		btn_close.setText("Close");
		btn_close.setStyleName(".gwt-Button-MetadataDialoguePopup_styles_01");
		horizontalPanel_btns.add(btn_close);

		this.add(verticalPanel_all);
		//initColorPicker();

		listBox_uploadData.addChangeHandler(new ChangeHandler()
		{

			@Override
			public void onChange(ChangeEvent event)
			{

				int sel = ( (ListBox)event.getSource() ).getSelectedIndex() ;
				String name = listBox_uploadData.getValue(sel);
				String id = ((JSONString) jsonObject_readNameStyleMappings.get(name)).stringValue() ;

				textBox_id.setText( id ) ;
				JSONObject style = (JSONObject) jsonObject_applyStyles.get(name);

				JSONObject jsonObject_nodeStyle = (JSONObject) style.get("nodeStyle") ;
				String nodeColor = ((JSONString) jsonObject_nodeStyle.get("color")).stringValue();
				textBox_nodeColor.setText( nodeColor ) ;
				ValueChangeEvent.fire( textBox_nodeColor , nodeColor ) ;
				textBox_nodePointSize.setText( ((JSONValue) jsonObject_nodeStyle.get("pointSize")).toString() ) ;
				textBox_nodeShape.setText( ((JSONString) jsonObject_nodeStyle.get("nodeShape")).stringValue() ) ;

				JSONObject jsonObject_labelStyle = (JSONObject) style.get("labelStyle") ;
				String labelStyleColor = ((JSONString) jsonObject_labelStyle.get("color")).stringValue() ;
				textBox_labelStyleColor.setText( labelStyleColor ) ;
				ValueChangeEvent.fire( textBox_labelStyleColor , labelStyleColor ) ;

				JSONObject jsonObject_branchStyle = (JSONObject) style.get("branchStyle") ;
				String branchStyleStrokeColor = ((JSONString) jsonObject_branchStyle.get("strokeColor")).stringValue() ;
				textBox_branchStyleStrokeColor.setText( branchStyleStrokeColor ) ;
				ValueChangeEvent.fire( textBox_branchStyleStrokeColor , branchStyleStrokeColor ) ;
				textBox_branchStyleLineWidth.setText( ((JSONValue) jsonObject_branchStyle.get("lineWidth")).toString() ) ;

				JSONObject jsonObject_glyphStyle = (JSONObject) style.get("glyphStyle") ;
				String glyphStyleFillColor = ((JSONString) jsonObject_glyphStyle.get("fillColor")).stringValue() ;
				textBox_glyphStyleFillColor.setText( glyphStyleFillColor ) ;
				ValueChangeEvent.fire( textBox_glyphStyleFillColor , glyphStyleFillColor ) ;						
				String glyphStyleStrokeColor = ((JSONString) jsonObject_glyphStyle.get("strokeColor")).stringValue() ;
				textBox_glyphStyleStrokeColor.setText( glyphStyleStrokeColor ) ;
				ValueChangeEvent.fire( textBox_glyphStyleStrokeColor , glyphStyleStrokeColor ) ;						
				textBox_glyphStyleLineWidth.setText( ((JSONValue) jsonObject_glyphStyle.get("lineWidth")).toString() ) ;

				selection = sel ;

			}

		});

	}

	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private void addListItem( String item , String value )
	{
		listBox_uploadData.addItem( item , value ) ;
	}

}
