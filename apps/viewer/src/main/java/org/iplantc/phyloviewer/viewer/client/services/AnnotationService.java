package org.iplantc.phyloviewer.viewer.client.services;

import org.iplantc.phyloviewer.viewer.server.AnnotationData;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Just a marker interface for a GWT RemoteService based on AnnotationData.
 */
public interface AnnotationService extends RemoteService, AnnotationData
{
}

