package com.telstra.message;

/**
 * Telstra Final Interview Case Study Project
 * @author James Koungoulos
 */
 
import javax.ws.rs.GET;

import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.spi.resource.Singleton;
import com.telstra.message.Message;

/*
 * Message Cache for message documents in JSON format
 * Stored documents must have two keys: id and message
 * This implementation assumes id values should always be non-negative
 */

@Singleton
@Path("/messages")
public class MessageCache {
	
	// ConcurrentHashMap collection for concurrent access of cached messages in the form of {id:Message}
	Map<Integer, Message> messages = new ConcurrentHashMap<Integer, Message>();
	
	// Default Time-To-Live in seconds for messages in the cache
	int DEFAULT_TTL = 30;
	
	// returns the requested document given an id if it has been stored within the last 30 seconds
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveMessage(@PathParam("id") int id){
		// generate a JSONObject with the found, fresh message or return null if the message is old or non-existent 
		JSONObject message = findMessage(id);
			   
		ResponseBuilder builder;
	    // set status code (200 if message was found, 404 otherwise)
	    if (message != null) {
		    builder = Response.ok(message.toString());
		    builder.status(200);
	    } else {
		    builder = Response.ok("Resource not found");
	    	builder.status(404);
	    } 

	    return builder.build();
	}
	
	// Converts a properly formed JSON string into a JSONObject and stores its contents in the cache
	@POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response storeMessage(String document) {
		try {
			// convert provided string into a JSON document
			JSONObject newEntry = new JSONObject(document);
			
			// Check if the document is well formed before storing
			if (isValid(newEntry)) {
				// generate new TTL based on date
				Date expires = expiryDate(DEFAULT_TTL);
				// create Message object for caching online
				Message m = new Message(newEntry.getString("message"), expires);
				// Add this Message to the cache
				messages.put(newEntry.getInt("id"), m);
						
				return Response.noContent().status(200).build();
			} else { 
				return Response.noContent().status(422).build(); 
			}		
		} catch (JSONException e) {
			// text provided isn't in JSON format
			return Response.noContent().status(422).build();	
		}		
    }
	
    // Clear the cache of all documents by clearing the Map
	@DELETE
    public Response clearCache() {
		messages.clear();
		return Response.noContent().status(200).build();	
    }
	
	// Retrieves requested message in the form of a JSONObject
	private JSONObject findMessage(int id) {
		Message m = messages.get(id);
		JSONObject result = new JSONObject();
		long cTime = new Date().getTime();
		// If the message wasn't found or has expired, return null
		if (m == null || (cTime > m.getTTL().getTime())) {
			return null;
		}		
		result.put("id", id);
		result.put("message", m.getMessage());
		
		return result;
	}
	
	// Produce new Date object reflecting the document's Time-To-Live
	private Date expiryDate(int TTL) {
		long current = new Date().getTime();
	    return new Date(current + (TTL * 1000));
	}
	
	// Check if document is well formed
	private boolean isValid(JSONObject j) {
		return (j.length() == 2 && j.has("id") && j.getInt("id") > 0 && j.has("message"));
	}
	
}