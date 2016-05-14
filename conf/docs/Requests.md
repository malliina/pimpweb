## Requests <div id="requests" class="area"></div>

Clients may interact with the MusicPimp server using HTTP requests and/or 
[WebSocket](http://en.wikipedia.org/wiki/WebSockets) connections.
                    
Obtain information using HTTP GET requests, and send JSON-formatted messages either 
in the body of HTTP POST requests or as WebSocket messages.
                
To upload files, HTTP POST them as **multipart/form-data**.

### Media Types

When making requests, indicate the desired response format in the **Accept** HTTP header.
The JSON response formats are versioned, and in order to ensure API compatibility,
clients should be explicit about which version of the format they accept. The following formats are
currently valid:
                
- application/vnd.musicpimp.v18+json
- application/vnd.musicpimp.v17+json
- application/vnd.musicpimp+json
- application/json

Remember this also when opening WebSocket connections; the value in the Accept header of the 
HTTP request that initiates a WebSocket connection will determine the format of the messages.
            
Whenever backwards-incompatible changes are made to the JSON response format in future MusicPimp server 
updates, they will be made under a new version. However, new content (name/value pairs) may be added to an existing 
format without notice. The examples in this documentation are based on the latest format, 
*application/vnd.musicpimp.v18+json*.
            
<span class="label label-info">Note</span> To force the server to respond with the latest JSON format
regardless of the Accept header, append query parameter `f=json` to the request URL.

### Authentication

HTTP Basic authentication is supported and required most of the time. Set your 
credentials in the **Authorization** HTTP header of the request.
            
For example, if your username is `i_love` and password `michael`,
the value of the Authorization header must be `Basic aV9sb3ZlOm1pY2hhZWw=`,
where `aV9sb3ZlOm1pY2hhZWw=` is `i_love:michael` base64-encoded.
