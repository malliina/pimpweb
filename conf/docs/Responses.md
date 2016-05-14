## Responses <div id="responses" class="area"></div>

The MusicPimp server responds to requests in either **JSON** (application/json)
or **HTML** (text/html), depending on what the client has requested.

### Compression
            
The responses are gzip-compressed provided that the **Accept-Encoding** HTTP header 
of the request contains `gzip`.

### Success and Failure

A successful HTTP status code indicates that the request was processed successfully. The 
response of a failed request will have an HTTP status code indicating failure.

In addition to the information you get from the status code, failed API requests may contain 
a JSON response content indicating the reason your request went titsup.

Example: 

    {"reason":"Invalid parameter."}
