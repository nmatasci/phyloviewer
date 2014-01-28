package org.iplantc.phyloviewer.viewer.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import org.apache.commons.fileupload.FileItemIterator; 
import org.apache.commons.fileupload.FileItemStream; 
import org.apache.commons.fileupload.servlet.ServletFileUpload; 

public class FileUpload extends HttpServlet
{

	private static final long serialVersionUID = -3297546467715259466L ;

	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    	System.out.println(" FileUpload called ");
		super.doGet(req, resp);
	}



	public void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
	{
		response.setContentType("text/html");
        ServletFileUpload upload = new ServletFileUpload() ;
    	
        try
        {
        	Enumeration<String> names = request.getAttributeNames();
        	if (! names.hasMoreElements()) {
        		System.out.println("No request elements");
        	}
        	while (names.hasMoreElements()) {
        		System.out.println(names.nextElement());
        	}
        	FileItemIterator iter = upload.getItemIterator(request) ;
        	System.out.println(" FileUpload initialized ");

            while (iter.hasNext())
            {
            	
                FileItemStream item = iter.next() ;
                InputStream stream = item.openStream() ;
                // Process the input stream
                File f = File.createTempFile("style-upload-", ".tmp");
                FileOutputStream out = new FileOutputStream(f);
                int len ;
                byte[] buffer = new byte[8192] ;
                while ((len = stream.read(buffer, 0, buffer.length)) != -1)
                {
                  out.write(buffer, 0, len) ;
                }
                out.close();
                System.out.println(f.getAbsolutePath());
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.getOutputStream().write(f.getAbsolutePath().getBytes());
            }
        }
        
        catch(Exception e)
        {
          throw new RuntimeException(e) ;
        }

    }
}