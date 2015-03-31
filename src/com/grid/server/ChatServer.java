package com.grid.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.grid.structs.geo.Point2D;
import com.grid.structs.models.Environment;
import com.grid.structs.models.Message;
import com.grid.structs.models.Publisher;
import com.grid.structs.models.World;

public class ChatServer {
	private static World mWorld;

    public static void main(String[] args) throws Exception {
    	mWorld = new World();
        final Server server = new Server(80);
        
        final ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        
        servletHandler.addServlet(MessageServlet.class, "/messages");
        servletHandler.addServlet(FeedServlet.class, "/feeds");
        servletHandler.addServlet(NameServlet.class, "/names");
        
        final ServletHolder fileUploadServletHolder = new ServletHolder(new UploadServlet());
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("image/png"));
        servletHandler.addServlet(fileUploadServletHolder, "/upload");
        
        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setResourceBase("web");
        
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });
        
        server.setHandler(handlers);
        server.start();
        server.join();
    }
    
    @SuppressWarnings("serial")
    public static class BaseServlet extends HttpServlet {
    	@Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("application/json");
            setCorsHeaders(response);
        }

		private void setCorsHeaders(HttpServletResponse response) {
			response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
            response.setHeader("Access-Control-Allow-Headers", "Overwrite, Destination, Content-Type, Depth, User-Agent, Translate, "
            		+ "Range, Content-Range, Timeout, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control, "
            		+ "Location, Lock-Token, If, Origin");
            response.setHeader("Access-Control-Expose-Headers", "content-length, Allow");
		}
		
		protected JSONObject toJson(final String body) {
			try {
				return new JSONObject(body);
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
    }
    
    @SuppressWarnings("serial")
    public static class NameServlet extends BaseServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	super.doPost(request, response);
        	try {
	            response.setStatus(HttpServletResponse.SC_CREATED);
	            final JSONObject jsonResponse = new JSONObject();
	            jsonResponse.put("name", Publisher.generateName());
				response.getWriter().println(jsonResponse.toString());
        	}
        	catch (Exception e) {
	            final JSONObject jsonResponse = new JSONObject();
	            try {
	            	jsonResponse.put("name", "?");
	            }
	            catch (JSONException je) {
	            	throw new IllegalStateException(e); 
	            }
        		response.getWriter().println(jsonResponse);
        	}

        }
    }
    
    @SuppressWarnings("serial")
    public static class FeedServlet extends BaseServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	super.doPost(request, response);

        	try {
            	String body = "";
                String line;
                while ((line = request.getReader().readLine()) != null) {
            		body += line;
    			}
            	final JSONObject jsonBody = toJson(body);
	        	final double longitude = Double.valueOf(String.valueOf(jsonBody.get("lon")));
	        	final double latitude = Double.valueOf(String.valueOf(jsonBody.get("lat")));
	    		final Environment myEnv = new Environment(new Point2D(longitude, latitude));
	            response.setStatus(HttpServletResponse.SC_OK);
	            final JSONObject jsonResponse = new JSONObject();
	            final List<Message> feedResponse = mWorld.feed(myEnv);
	            jsonResponse.put("feed", feedResponse);
				response.getWriter().println(jsonResponse.toString());
        	}
        	catch (Exception e) {
	            final JSONObject jsonResponse = new JSONObject();
	            try {
	            	jsonResponse.put("feed", Lists.newArrayList());
	            }
	            catch (JSONException je) {
	            	throw new IllegalStateException(e); 
	            }
        		response.getWriter().println(jsonResponse);
        	}
        }
    }
 
    @SuppressWarnings("serial")
    public static class MessageServlet extends BaseServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        	super.doPost(request, response);

        	String body = "";
            String line;
            while ((line = request.getReader().readLine()) != null) {
        		body += line;
			}
        	final JSONObject jsonBody = toJson(body);
        	final Message message = toMessage(jsonBody);
        	mWorld.publish(message.getLocation(), message.getPublisher(), message.getMessage());
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println(message.getMessage());
        }

		private Message toMessage(final JSONObject jsonBody) {
			try {
	        	final double locationX = jsonBody.getDouble("lon");
	        	final double locationY = jsonBody.getDouble("lat");
	        	final String publisher = jsonBody.getString("publisher");
				return new Message(publisher, jsonBody.get("message") + "", new Point2D(locationX, locationY));
			}
			catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
    }
    
    @SuppressWarnings("serial")
    public static class UploadServlet extends BaseServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	super.doPost(request, response);
        	
        	final String localFilename = String.valueOf("web/" + UUID.randomUUID() + ".png");
        	final Path localFilePath = Files.createFile(Paths.get(localFilename));
        	final OutputStream localFileStream = Files.newOutputStream(localFilePath, StandardOpenOption.APPEND);
        	
            for (Part requestPart : request.getParts()) {
        		IOUtils.copy(requestPart.getInputStream(), localFileStream);
			}
            
            localFileStream.close();

            final JSONObject jsonResponse = new JSONObject();
            try {
            	jsonResponse.put("img", localFilename);
            }
            catch (JSONException je) {
            	throw new IllegalStateException(je); 
            }
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println(jsonResponse);
        }
    }
}
