package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface JobStatusChangeEventHandler extends EventHandler 
{
	void onStatusChange(JobStatusChangeEvent event); 
}
