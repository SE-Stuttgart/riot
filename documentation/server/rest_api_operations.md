# RIOT REST API Operations


A complete REST operation is formed by combining an HTTP method with the full URI to the resource to be addressed. For example, here is an operation to retrieve all calendar entries:

	GET http://localhost:8080/riot.server/api/v1/calendar

To create a complete request, combine the operation with the appropriate HTTP headers and any required JSON payload.

Here are the available REST API operations:

## Calendar

### Create Calendar Entry

	POST /calendar

#### Request Body

JSON payload corresponding to the Calendar Entry to be created.

#### Response

HTTP Created (201) response if operation was succesful; or

HTTP Bad Request (400) response if request body in invalid.

### Retrieve Calendar Entries
Retrieving at maximum 20 calendar entries.
	
	GET /calendar
Retrieving at maximum {limit} calendar entries.

	GET /calendar?limit={limit}

Retrieving at maximum {limit} calendar entries, starting at position {offset}.

	GET /calendar?offset={offset}&limit={limit}

#### Parameters

limit - maximum number of calendar entries to return.

offset - the beggining position.

#### Response

HTTP OK (200) response if operation was succesful; or

HTTP Bad Request (400) response if parameters have invalid values.

The Response Header contains the information: offset, limit and total (total number of calendar entries).

### Retrieve Calendar Entries using Filter

	POST /calendar/filter

#### Request Body

JSON payload (FilteredRequest) representing the filter to be applied. Offset and limit can be also specified on the request.

#### Response

HTTP OK (200) response if operation was succesful; or

HTTP Bad Request (400) response if parameters at FilteredRequest have invalid values.

The Response Header contains the information: offset, limit and total (total number of calendar entries that applied to the filter).

### Retrieve Calendar Entry

	GET /calendar/{entryId}

#### Parameters

entryId - the id of calendar entry to be retrieved.

#### Response

HTTP OK (200) response if operation was succesful; or

HTTP Not Found (404) response if calendar entry does not exist.

### Update Calendar Entry

	PUT /calendar/{entryId}

#### Parameters

entryId - the id of calendar entry to be updated.

#### Request Body

JSON payload corresponding to the Calendar Entry to be updated.

#### Response

HTTP No Content (204) response if operation was succesful;

HTTP Bad Request (400) response if request body in invalid; or

HTTP Not Found (404) response if calendar entry does not exist.

### Delete Calendar Entry

	DELETE /calendar/{entryId}

#### Parameters

entryId - the id of calendar entry to be deleted.

#### Response

HTTP No Content (204) response if operation was succesful; or

HTTP Not Found (404) response if calendar entry does not exist.