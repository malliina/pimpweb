## Server-side events <div id="server" class="area"></div>

You can subscribe to server-side playback updates. This is useful when you want to display 
a player in your app even though playback happens on another device.
    
To subscribe, open a WebSocket connection to `/ws/playback` on the MusicPimp server.
    
### Events

The following events are sent by the server.

#### Time updated

Sent when playback has progressed in time. The playback position is 
given as an integer in seconds. Sent at least once per second during playback:

    {"event":"time_updated","position":42}
    
#### Track changed

The player track has changed.

    {
        "event":"track_changed",
        "track":{
            "id":"Pearl+Jam+-+Man+Of+The+Hour.mp3",
            "title":"Man Of The Hour",
            "artist":"Pearl Jam",
            "album":"My Lost Dogs",
            "duration":225,
            "size":5408768
        }
    }
    
The track size is reported in bytes.

#### Playstate changed

Sent when the playstate has changed.

    {"event":"playstate_changed","state":"Stopped"}
    
The playstate is one of the following: **Playing**, **Paused**, **Stopped**, **NoMedia**. NoMedia 
implies that the player has no track set.

#### Playlist modified

The tracks in the playlist have been modified. A separate event exists for when the 
playlist index has changed; see the next item.

    {
        "event":"playlist_modified",
        "playlist":[
            {
                "id":"Pearl+Jam+-+Man+Of+The+Hour.mp3",
                "title":"Man Of The Hour",
                "artist":"Pearl Jam",
                "album":"My Lost Dogs",
                "duration":225,
                "size":5408768
            },
            {
                "id":"ryan+adams+-+desire.mp3",
                "title":"Desire",
                "artist":"Ryan Adams",
                "album":"48 Hours",
                "duration":231,
                "size":5546654
            }
        ]
    }

#### Playlist index changed

The player has skipped to a new song in the playlist.

    {"event":"playlist_index_changed","playlist_index":42}
    
#### Volume changed

The volume has changed. The volume is an integer value within [0,100].

    {"event":"volume_changed","volume":42}
    
#### Mute toggled

Mute has been turned on or off.

    {"event":"mute_toggled","mute":true}
