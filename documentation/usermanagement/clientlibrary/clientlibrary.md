# Clientlibrary
The clientlibrary project contains all REST clients for the RIOT Server.

## LoginClient
The LoginClient is used by the other clients to handle Authentication. The LoginClient provides operations for the following http requests:

- get
- post
- put
- delete

Every request is augmented with the Access-Token, that is initially retrieved by calling the login() operation. The LoginClient also handles the refresh mechanism that is used if an Access-Token is expired. To logout simply call the logout() operation.  

## UsermanagementClient
The UsermanagementClient offers all operations that are available on the Usermanagement. 
How to use the UsermanagementClient:

	LoginClient loginClient = new LoginClient(SERVER_URL, THING_NAME, new DefaultTokenManager()); // The DefaultTokenManager saves tokens only in memory!
	UsermanagementClient client = new UsermanagementClient(loginClient);
	loginClient.login(USERNAME, PASSWORD);
	Collection<UserResponse> users = client.getUsers();
	// do stuff
	loginClient.logout();

## How to implement a new client
In order to implement a new REST-Client create a class as follows:

	public class NewClient {

       	private final LoginClient loginClient;

		// For every REST-Operation 
		public ResponseClass restServiceOperationName(RequestClass request) throwsRequestException {
        		HttpResponse response = this.loginClient.put(REST_SERVICE_URL, request);
        		try {
				return this.loginClient.jsonMapper.readValue(response.getEntity().getContent(),
							ResponseClass.class);
        		} catch (Exception e) {
				    throw new RequestException(e);
        		}
    		}
       } 

In this example a put request is used, as mentioned before you can use get, post, put and delete requests.

The Request and Response classes have to satisfy some rules. First of all they have to be placed in the commons project, so that every component can use the client by using the client and commons libraries. Every field has to have a setter as well as a getter. The classes have to provide a default constructor without parameters. A Response/Request class maybe look like the following:

	public class Response {

		protected String fieldX;
		protected String fieldY;
  		
		public Response() {
		}

		public String getFieldX() {
			return fieldX;
		}

		public void setFieldX(String fieldX) {
			this.fieldX = fieldX;
		}
		
		public String getFieldY() {
			return fieldY;
		}

		public void setFieldY(String fieldY) {
			this.fieldY = fieldY;
		}

	}