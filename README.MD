# RESTful Online Document Cache by James Koungoulos

**Online Document Cache for JSON documents using the Jersey RESTful framework, Maven and Tomcat.**

**Written in Java using Mars Eclipse.**

## Operations
### POST
Converts a properly formed JSON string into a JSONObject and stores its contents in the cache

### GET
Returns the requested document given an id if it has been stored within the last 30 seconds

### DELETE
Clears the cache of all documents by clearing the Map

## Document format
`{
	“id”: int,
	“message”: string
}`
Stored documents must have two keys: id and message, with integer and string values respectively.
This implementation assumes id values should always be non-negative.

## Extensions:
1. Cache entries have a 30 second TTL; this can be tweaked by changing the value of DEFAULT_TTL or altering the POST method storeMessage() in MessageCache.java.
2. Malformed documents are rejected with a 422 (Unprocessable Entity) status.
