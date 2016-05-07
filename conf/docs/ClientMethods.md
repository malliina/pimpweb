## Client-side methods

HTTP requests are made using URLs like

    http://my_server_ip:8456/playback
    
### Library <a name="library"></a>

Library folders contain audio tracks and subfolders. Use the following methods 
to browse and retrieve items from the library.
   
#### Get root folder <small>GET /folders</small>

Returns the contents of the root folder of the music library.

Example response:

    {
        "folders":[
            {
                "id":"100+Hits+-+Dance+Mix",
                "title":"100 Hits - Dance Mix",
                "path":"100 Hits - Dance Mix"
            },
            {
                "id":"22+Pistepirkko",
                "title":"22 Pistepirkko",
                "path":"22 Pistepirkko"
            },
            {
                "id":"2pac",
                "title":"2pac",
                "path":"2pac"
            },
            {
                "id":"3+Doors+Down",
                "title":"3 Doors Down",
                "path":"3 Doors Down"
            }
        ],
        "tracks":[
            {
                "id":"00+-+Preludio+Obsesivo.mp3",
                "title":"Preludio Obsesivo",
                "artist":"Rata Blanca",
                "album":"Rata Blanca",
                "duration":222,
                "size":3108553
            },
            {
                    "id":"00+-+Turn+Me+On.mp3",
                    "title":"Turn Me On",
                    "artist":"Kevin Lyttle",
                    "album":"Summer In The City - Latin Party",
                    "duration":175,
                    "size":4225234
            },
            {
                    "id":"A+Ha+-+Take+On+Me.mp3",
                    "title":"Take On Me [October 1985]",
                    "artist":"A-Ha",
                    "album":"The Definitive Singles Collection 1984-2004",
                    "duration":227,
                    "size":5470208
            }
        ]
    }

#### Get subfolder <small>GET /folders/my_folder_id_here </small>

Returns the contents of the specified music library folder.

<span class="label label-info">Note</span> First request the contents of the root folder 
in order to get a list of subfolders for further browsing.

#### Get track <small>GET /tracks/my_track_id</small>

Retrieves the file with the specified ID. Supply this to a media player that accepts a 
URI as media source, for example.

#### Get track, with Accept-Ranges<small> GET /downloads/my_track_id</small>

The same as above except that the HTTP response contains the string `bytes` 
in the **Accept-Ranges** header.

#### Search <small>GET /search?term=tillbaka+till+samtiden&limit=100</small>

Search for a track, album or artist. Specify your search query in the **term** query
parameter, and optionally set the maximum number of returned results in the **limit** query
parameter.

Example response:

    [
        {
            "id":"Kent%5C2007+-+Tillbaka+Till+Samtiden%5C01_kent-elefanter.mp3",
            "title":"Elefanter",
            "artist":"Kent",
            "album":"Tillbaka Till Samtiden",
            "duration":321,
            "size":7639850
        },
        {
            "id":"Kent%5C2007+-+Tillbaka+Till+Samtiden%5C02_kent-berlin.mp3",
            "title":"Berlin",
            "artist":"Kent",
            "album":"Tillbaka Till Samtiden",
            "duration":276,
            "size":7143257
        }
    ]

### Remote playback control

Control a remote playback device using WebSockets or HTTP POST calls. 
Two types of remote playback devices are supported:

- the MusicPimp server
- clients connected to the MusicPimp server, such as a web browser

To use the MusicPimp server as the playback device, open a WebSocket connection to
`/ws/playback` or HTTP POST to `/playback`.

To control the playback devices of clients connected to the MusicPimp server, open a WebSocket 
connection to `/ws/webplay` or HTTP POST to `/webplay`.

The available commands are described below.

#### Play track <small>HTTP POST or WebSocket</small>

Start playback of the track with the given ID using the following HTTP request body or WebSocket message:

    {"cmd": "play", "track": "my_track_id"}
        
<span class="label label-info">Note</span> Browse the music [library](#library) 
to obtain IDs of available tracks. If the track you wish to play is not
located on the MusicPimp server, see the next item instead.
            
#### Play uploaded file <small>POST to /playback/uploads</small>

Starts playback of the file uploaded as *multipart/form-data*.

<span class="label label-warning">Note</span> Uploaded playback is at the moment 
only supported when the playback device is the MusicPimp server.

#### Stop playback <small>HTTP POST or WebSocket</small>

Stop playback:

    {"cmd": "stop"}
            
#### Resume playback <small>HTTP POST or WebSocket</small>
            
Resume playback: 

    {"cmd": "resume"}
            
#### Seek <small>HTTP POST or WebSocket</small>
            
Seek playback to the specified position, in seconds, of the current track:

    {"cmd": "seek", "value": 42}
                
#### Adjust volume <small>HTTP POST or WebSocket</small>
            
Adjust the volume, which is an integer in the range [0, 100]: 

    {"cmd": "volume", "value": 42}
                
#### Mute and unmute <small>HTTP POST or WebSocket</small>
            
Turn mute on or off (true/false): 

    {"cmd": "mute", "value": true}
            
#### Status <small>GET /playback</small>
            
Returns a server player status message.
            
Example response:
            
    {
        "track":{
            "id":"Paola+-+Interstellar+Love.mp3",
            "title":"Interstellar Love",
            "artist":"Paola",
            "album":"Stockcity Girl",
            "duration":201,
            "size":4840094
        },
        "state":"Started",
        "position":14,
        "volume":40,
        "mute":false,
        "playlist":[
            {
                "id":"Paola+-+Interstellar+Love.mp3",
                "title":"Interstellar Love",
                "artist":"Paola",
                "album":"Stockcity Girl",
                "duration":201,
                "size":4840094
            },
            {
                "id":"cheap+trick+-+hello+there.mp3",
                "title":"Hello There",
                "artist":"Cheap Trick",
                "album":"The Essential Cheap Trick",
                "duration":100,
                "size":2573904
            }
        ],
        "playlist_index":0
    }

#### Upload library track <small>POST to /playback/stream</small>

Instruct the MusicPimp server to perform a **multipart/form-data** upload
of a track in its library to a remote destination.
                
    {
        "track": "my_track_id",
        "uri": "destination_uri",
        "username": "destination_username",
        "password": "destination_password"
    }

The credentials will be put into the **Authorization** header of the upload request
as if the destination required HTTP Basic authentication.

### Playlist

#### Add <small>HTTP POST or WebSocket</small>

Add the track with the specified ID to the playlist:

    {"cmd": "add", "track": "my_track_id"}
    
#### Add uploaded file <small>POST to /playlist/uploads</small>

Add the file uploaded as multipart/form-data to the playlist.

#### Remove <small>HTTP POST or WebSocket</small>

Remove the track at the specified playlist index from the playlist:

    {"cmd": "remove", "value": 3}
    
#### Skip <small>HTTP POST or WebSocket</small>

Start playback of the track at the specified playlist index:

    {"cmd": "skip", "value": 3}
    
### Miscellaneous

#### Ping <small>GET /ping</small>

Pings the server. Returns a 200 OK HTTP response on success.

<span class="label label-info">Note</span> Ping is the only API call that 
does not require authentication.
    
#### Authenticated ping <small>GET /pingauth</small>

Pings the server and validates the supplied credentials. Responds with 200 OK 
and the server version as JSON content if the credentials are valid, and with
401 Unauthorized if authentication fails.
    
Example response:

    {"version":"1.8.0"}
