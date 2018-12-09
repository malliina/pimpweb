HTTP requests are made using URLs like

    http://my_server_ip:8456/playback
    
### Library <div id="library" class="sub-area"></a>

Library folders contain audio tracks and subfolders. Use the following methods 
to browse and retrieve items from the library.
   
#### Get root folder

    GET /folders

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

#### Get subfolder

    GET /folders/my_folder_id_here

Returns the contents of the specified music library folder.

<span class="label label-info">Note</span> First request the contents of the root folder 
in order to get a list of subfolders for further browsing.

#### Get track

    GET /tracks/my_track_id

Retrieves the file with the specified ID. Supply this to a media player that accepts a 
URI as media source, for example.

#### Get track, with Accept-Ranges

    GET /downloads/my_track_id

The same as above except that the HTTP response contains the string `bytes` 
in the **Accept-Ranges** header.

#### Search

    GET /search?term=tillbaka+till+samtiden&limit=100

Search for a track, album or artist. Specify your search query in the **term** query
parameter, and optionally set the maximum number of returned results in the **limit** query
parameter.

Example response:

    [
        {
            "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C01_kent-elefanter.mp3",
            "title": "Elefanter",
            "artist": "Kent",
            "album": "Tillbaka Till Samtiden",
            "duration": 321,
            "size": 7639850
        },
        {
            "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C02_kent-berlin.mp3",
            "title": "Berlin",
            "artist": "Kent",
            "album": "Tillbaka Till Samtiden",
            "duration": 276,
            "size": 7143257
        }
    ]
    
#### Get most popular tracks

    GET /player/popular
    
 Returns an array (in key *populars*) of the most played tracks along with the playback count
 (in key *playbackCount*) of each track:
 
    {
        "populars": [
            {
                "track": {
                    "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C01_kent-elefanter.mp3",
                    "title": "Elefanter",
                    "artist": "Kent",
                    "album": "Tillbaka Till Samtiden",
                    "duration": 321,
                    "size": 7639850
                },
                "playbackCount": 42
            },
            {
                "track": {
                    "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C02_kent-berlin.mp3",
                    "title": "Berlin",
                    "artist": "Kent",
                    "album": "Tillbaka Till Samtiden",
                    "duration": 276,
                    "size": 7143257
                },
                "playbackCount": 41
            }
        ]
    }

#### Get most recently played tracks

    GET /player/recent
    
Returns an array (in key *recents*) of the most recently played tracks, 
starting from the most recently played track, along with a timestamp 
(in key *when*, given in unix time in milliseconds) when playback started:

    {
        "recents": [
            {
                "track": {
                    "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C01_kent-elefanter.mp3",
                    "title": "Elefanter",
                    "artist": "Kent",
                    "album": "Tillbaka Till Samtiden",
                    "duration": 321,
                    "size": 7639850
                },
                "when": 1462812430364
            },
            {
                "track": {
                    "id": "Kent%5C2007+-+Tillbaka+Till+Samtiden%5C02_kent-berlin.mp3",
                    "title": "Berlin",
                    "artist": "Kent",
                    "album": "Tillbaka Till Samtiden",
                    "duration": 276,
                    "size": 7143257
                },
                "when": 1462812422540
            }
        ]
    }

### Player <div id="player" class="sub-area"></a>

Control playback on the MusicPimp server using WebSockets or HTTP POST calls. 

To use the MusicPimp server as the playback device, open a WebSocket connection to
`/ws/playback` or HTTP POST to `/playback`.

The available commands are described below.

#### Play track <small>HTTP POST or WebSocket</small>

Start playback of the track with the given ID using the following HTTP request body or WebSocket message:

    {"cmd": "play", "track": "my_track_id"}
        
<span class="label label-info">Note</span> Browse the music [library](#library) 
to obtain IDs of available tracks. If the track you wish to play is not
located on the MusicPimp server, see the next item instead.

#### Play all

Reset the playlist with the given tracks and start playback from the first track:

    {
        "cmd": "play_items", 
        "folders": [ "folder_id1", "folder_id2" ], 
        "tracks": [ "track_id1", "track_id2" ]
    }
            
#### Play uploaded file <small>POST to /playback/uploads</small>

Starts playback of the file uploaded as *multipart/form-data*.

#### Stop playback <small>HTTP POST or WebSocket</small>

Stop playback:

    {"cmd": "stop"}
            
#### Resume playback <small>HTTP POST or WebSocket</small>
            
Resume playback: 

    {"cmd": "resume"}
    
#### Next

Start playback of the next track in the playlist:

    {"cmd": "next"}

#### Previous

Start playback of the previous track in the playlist:

    {"cmd": "prev"}

#### Skip <small>HTTP POST or WebSocket</small>

Start playback of the track at the specified playlist index:

    {"cmd": "skip", "value": 3}
            
#### Seek <small>HTTP POST or WebSocket</small>
            
Seek playback to the specified position, in seconds, of the current track:

    {"cmd": "seek", "value": 42}
                
#### Adjust volume <small>HTTP POST or WebSocket</small>
            
Adjust the volume, which is an integer in the range [0, 100]: 

    {"cmd": "volume", "value": 42}
                
#### Mute and unmute <small>HTTP POST or WebSocket</small>
            
Turn mute on or off (true/false): 

    {"cmd": "mute", "value": true}
            
#### Status

    GET /playback
            
Returns a server player status message.
            
Example response:
            
    {
        "track": {
            "id": "Paola+-+Interstellar+Love.mp3",
            "title": "Interstellar Love",
            "artist": "Paola",
            "album": "Stockcity Girl",
            "duration": 201,
            "size": 4840094
        },
        "state": "Started",
        "position": 14,
        "volume": 40,
        "mute": false,
        "playlist": [
            {
                "id": "Paola+-+Interstellar+Love.mp3",
                "title": "Interstellar Love",
                "artist": "Paola",
                "album": "Stockcity Girl",
                "duration": 201,
                "size": 4840094
            },
            {
                "id": "cheap+trick+-+hello+there.mp3",
                "title": "Hello There",
                "artist": "Cheap Trick",
                "album": "The Essential Cheap Trick",
                "duration": 100,
                "size": 2573904
            }
        ],
        "playlist_index": 0
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

### Playlist <div id="playlist" class="sub-area"></a>

#### Add <small>HTTP POST or WebSocket</small>

Add the track with the specified ID to the playlist:

    {"cmd": "add", "track": "my_track_id"}
    
#### Add all

Add multiple items to the playlist:

    {
        "cmd": "add_items", 
        "folders": [ "folder_id1", "folder_id2" ], 
        "tracks": [ "track_id1", "track_id2" ]
    }
    
#### Reset playlist

Reset the playlist to the given *tracks* and set the index to *index*:
    
    {
        "cmd": "reset_playlist",
        "tracks": [ "track_id1", "track_id2" ],
        "index": 0
    }
    
Resetting the playlist does not start or stop playback.    

#### Add uploaded file <small>POST to /playlist/uploads</small>

Add the file uploaded as multipart/form-data to the playlist.

#### Remove <small>HTTP POST or WebSocket</small>

Remove the track at the specified playlist index from the playlist:

    {"cmd": "remove", "value": 3}
    
#### Insert track

Insert the given track at the specified index in the playlist:

    {"cmd": "insert", "track": "my_track_id", "index": 42}
    
#### Move track in playlist

Move the track at index *from* in the playlist to index *to*:

    {"cmd": "move", "from": 42, "to": 43}
    
#### Get saved playlists

Saved playlists are user-specific.
    
    GET /playlists
    
Returns an array of saved playlists for the logged in user:    

    {
        "playlists": [
            {
                "id": 42,
                "name": "The best playlist ever",
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
        ]
    }
    
#### Get one saved playlist

Get a playlist by ID:

    GET /playlists/42
    
Example response:

    {
        "playlist": {
            "id": 42,
            "name": "The best playlist ever",
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
    }
    
#### Create or update a saved playlist
    
    POST /playlists
    
Saves the playlist supplied in the JSON body of the request:    

    {
        "playlist": {
            "id": 42,
            "name": "My updated playlist",
            "tracks": [ "track_id1", "track_id2" ]
        }
    }
       
To create a new playlist, set the ID to *null* in the above JSON body:

    "id": null
    
Returns the ID of the saved playlist in a JSON object:

    {
        "id": 42
    }
    
#### Delete a saved playlist

Delete a playlist with a given ID:

    POST /playlists/delete/42
    
Returns HTTP 202 Accepted if deletion succeeds.

### Alarms <div id="alarms" class="sub-area"></a>

Schedule tracks for playback on your MusicPimp server. Use this as an
alarm clock, for example.

#### Get alarms

    GET /alarms

Returns an array of alarms configured on the MusicPimp server:

    [
        {
            "id": "d7e506f0-9c05-4253-8fff-13ef37b8b38c",
            "job": {
                "track": "Chicane%5C17-chicane_ft_moya_brennan-saltwater.mp3"
            },
            "when": {
                "hour": 6,
                "minute": 40,
                "days": [
                    "fri",
                    "wed",
                    "mon",
                    "thu",
                    "tue"
                ]
            },
            "enabled": true
        }
    ]

#### Control alarms

    POST /alarms

To control alarms, HTTP POST a request body with JSON to the */alarms* endpoint. Each JSON body
must at least contain a *cmd* key and may contain other key-value pairs, as documented below.

Save changes to an existing alarm:

    {
        "cmd": "save",
        "ap": {
            "id": "d7e506f0-9c05-4253-8fff-13ef37b8b38c",
            "job": {
                "track": "Chicane%5C17-chicane_ft_moya_brennan-saltwater.mp3"
            },
            "when": {
                "hour": 6,
                "minute": 40,
                "days": [
                    "fri",
                    "wed",
                    "mon",
                    "thu",
                    "tue"
                ]
            },
            "enabled": true
        }
    }

To create a new alarm, POST a payload like above, but set the alarm *id* to *null*:

    "id": null

MusicPimp will generate a suitable ID for the alarm.

Delete an alarm:

    { "cmd": "delete", "id": "alarm_id_here" }

Start alarm playback:

    { "cmd": "start", "id": "alarm_id_here" }

Stop alarm playback:

    { "cmd": "stop" }

You may opt-in to receive push notifications to your mobile device(s) when
scheduled playback starts. You may use such a notification to stop playback.

To opt-in, you must register your device for push notifications with MusicPimp:

Register a Microsoft Push Notification Service (MPNS) device:

    { "cmd": "push_add", "url": "mpns_device_url_here", "silent": true, "tag": "tag_identifier" }

Unregister an MPNS device:

    { "cmd": "push_remove", "url": "mpns_device_url_here" }

or

    { "cmd": "push_remove", "tag": "tag_identifier" }

Register an Android device using Google Cloud Messaging (GCM):

    { "cmd": "gcm_add", "id": "device_token", "tag": "tag_identifier" }

Unregister a GCM device:

    { "cmd": "gcm_remove", "id": "tag_identifier" }

Register an Android device using Amazon Device Messaging (ADM):

    { "cmd": "adm_add", "id": "device_token", "tag": "tag_identifier" }

Unregister an ADM device:

    { "cmd": "adm_remove", "id": "tag_identifier" }

Register an Apple Push Notification service (APNs) device:

    { "cmd": "apns_add", "id": "device_token", "tag": "tag_identifier" }

Unregister an APNs device:

    { "cmd": "apns_remove", "id": "tag_identifier" }

Supply a unique and static *tag* ID with your registrations. This is used to
identify your device (as device tokens may change) and is included in the payload
of every push notification so that devices can identify the source MusicPimp server.

### Miscellaneous <div id="misc" class="sub-area"></a>

#### Ping

    GET /ping

Pings the server. Returns a 200 OK HTTP response on success.

<span class="label label-info">Note</span> Ping is the only API call that 
does not require authentication.
    
#### Authenticated ping

    GET /pingauth

Pings the server and validates the supplied credentials. Responds with 200 OK 
and the server version as JSON content if the credentials are valid, and with
401 Unauthorized if authentication fails.
    
Example response:

    {"version":"1.8.0"}
