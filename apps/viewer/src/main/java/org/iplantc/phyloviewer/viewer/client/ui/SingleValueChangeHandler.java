package org.iplantc.phyloviewer.viewer.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * A ValueChangeHandler with methods to attach to and detach from a single widget at a time.
 * @param <T> the value about to be changed
 */
public abstract class SingleValueChangeHandler<T> implements ValueChangeHandler<T>
{
	HandlerRegistration registration;
	
	/**
	 * Listen for change events from the given widget. Stop listening to any previously attached widget.
	 */
	public void attachTo(HasValue<T> widget)
	{
		detach();
		registration = widget.addValueChangeHandler(this);
	}
	
	/**
	 * Stop listening for changes from the current widget.
	 */
	public void detach()
	{
		if (registration != null)
		{
			registration.removeHandler();
		}
		
		registration = null;
	}
}
