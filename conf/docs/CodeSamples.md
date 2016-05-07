## Code samples

As an example, let's use the API to ping a locally installed MusicPimp server 
and validate our credentials. We make a request and expect to get an HTTP response 
with status code 200 [OK](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html).

Here's how it can be done in Scala, using either [Apache HttpClient](http://hc.apache.org/httpcomponents-client-ga/index.html) or 
the [Web Services library](http://www.playframework.com/documentation/2.1.x/ScalaWS) in 
[Play Framework](http://www.playframework.com/):

<script src="https://gist.github.com/malliina/5509130.js"></script>

Here's the same in C#, using either [Microsoft HTTP Client Libraries (HttpClient)](https://www.nuget.org/packages/Microsoft.Net.Http) 
or [RestSharp](http://restsharp.org/):

<script src="https://gist.github.com/malliina/5513464.js"></script>
