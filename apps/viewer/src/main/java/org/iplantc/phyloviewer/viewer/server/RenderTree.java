package org.iplantc.phyloviewer.viewer.server;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.iplantc.phyloviewer.viewer.server.persistence.Constants;

public class RenderTree extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2634082637401140976L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		String sTreeID = request.getParameter("treeID");
		String layoutID = request.getParameter("layoutID");
		int width = Integer.parseInt(request.getParameter("width"));
		int height = Integer.parseInt(request.getParameter("height"));
		
		Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "RenderTree Received request for " + "treeID=" + sTreeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height);
		
		ServletOutputStream stream;
		try
		{
			stream = response.getOutputStream();
		}
		catch(IOException e)
		{
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.SEVERE, "Unable to get output stream");
			return;
		}

		try {
			byte[] treeID = Hex.decodeHex(sTreeID.toCharArray());
			BufferedImage image = renderTreeImage(treeID, layoutID, width, height);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);

			// Set the content parameters and write the bytes for the png.
			response.setContentType("image/png");
			response.setContentLength(out.size());

			out.writeTo(stream);
			stream.close();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		catch(DecoderException e)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public BufferedImage renderTreeImage(byte[] treeID, String layoutID,
			int width, int height) {

		BufferedImage overview =  this.getOverviewData().getOverviewImage(treeID, layoutID);
		
		// Resize the image to the requested size.
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		if(overview!=null) {
			Graphics2D bg = image.createGraphics();
		    bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		    bg.scale((double)width/overview.getWidth(), (double)height/overview.getHeight());
		    bg.drawImage(overview, 0, 0, null);
		    bg.dispose(); 
		    image.flush();
		} else {
			Logger.getLogger("org.iplantc.phyloviewer").log(Level.FINE, "Image file not found for " + "treeID=" + treeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height);
			//TODO go ahead and generate the image now?
		}
	    
		return image;
	}

	private IOverviewImageData getOverviewData() {
		return (IOverviewImageData) this.getServletContext().getAttribute(Constants.OVERVIEW_DATA_KEY);
	}
}
