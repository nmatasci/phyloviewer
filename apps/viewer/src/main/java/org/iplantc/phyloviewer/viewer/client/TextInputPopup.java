package org.iplantc.phyloviewer.viewer.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A PopupPanel with a text area and an OK button. Fires a ValueChangeEvent containing the text and hides itself when the OK button is pressed.
 */
public class TextInputPopup extends PopupPanel implements HasValueChangeHandlers<String>
{
	final TextArea textBox = new TextArea();
	
	public TextInputPopup()
	{
		VerticalPanel vPanel = new VerticalPanel();
		
		textBox.setVisibleLines(20);
		textBox.setCharacterWidth(80);
		Button okButton = new Button("OK", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ValueChangeEvent.fire(TextInputPopup.this, textBox.getValue());
				TextInputPopup.this.hide();
			}
		});

		Button cancelButton = new Button("Cancel", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TextInputPopup.this.hide();
			}
		});

		
		vPanel.add(textBox);
		vPanel.add(okButton);
		vPanel.add(cancelButton);
		this.add(vPanel);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	public void setText(String text)
	{
		textBox.setText(text);
	}
}
