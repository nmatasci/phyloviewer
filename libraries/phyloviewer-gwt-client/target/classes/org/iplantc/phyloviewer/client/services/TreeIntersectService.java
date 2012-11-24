package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A RemoteService that gets the node at a given Cartesian location in a tree layout.
 */
@RemoteServiceRelativePath("treeIntersect")
public interface TreeIntersectService extends RemoteService {

	/**
	 * @param rootNodeId the id of the root node of the tree
	 * @param layoutID the tree layout id
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return if there's a hit, a json string containing the node and layout information in the following format:
	 * <pre>
	 *{"hit":{
	 *  "nodeId": 1,
	 *  "position": {"x": 42, "y": 42},
	 *  "boundingBox": {
	 *    "min" : {"x": 0, "y": 0},
	 *	  "max" : {"x": 142, "y": 142}
	 *  }
	 *}}
	 * </pre>
	 * 
	 * otherwise a null hit: <pre>{"hit" : null}</pre>
	 */
	String intersectTree(int rootNodeId, String layoutID, double x, double y);
}
