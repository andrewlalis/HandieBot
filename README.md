# HandieBot

![AvatarIcon](/src/main/resources/avatarIcon.png)

HandieBot is a bot for Discord, written using Java, the [Discord4J](https://github.com/austinv11/Discord4J) library, and the [Lavaplayer](https://github.com/sedmelluq/lavaplayer) library for sound processing. It is a fully-fledged bot with the ability to manage playlists, get music from URLs, and perform other necessary functions for a clean and enjoyable user experience.

## Description

This Bot is designed to run as one executable Jar file, to represent one Discord bot. The bot itself keeps track of which servers (`guilds`) that it's connected to, and can independently handle requests from each one, provided it has enough bandwidth.

In each guild, the bot will use, or create both a voice and a text channel for it to use. These values are set in the source code as `HandieBotMusic` and `handiebotmusic`, respectively. From these channels, the bot will send messages about what song it's currently playing, responses to player requests, and any possible errors that occur. The voice channel is specifically only for playing music, and the bot will try to only connect when it is doing so.

## Commands

HandieBot contains some commands, most of which should be quite intuitive to the user. However, for completions' sake, the format for commands is as follows:

All commands begin with a prefix, which will not be shown with all the following commands, as it can be configured by users. This prefix is by default `!`.

`command [optional arguments] <required arguments>`

In particular, if the optional argument is shown as capital letters, this means that you must give a value, but if the optional argument is given in lowercase letters, simply write this argument. For example, the following commands are valid:

```text
play
play https://www.youtube.com/watch?v=9bZkp7q19f0
queue
queue all
```

Because the play command is defined as `play [URL]`, and the queue command is defined as `queue [all]`.


### Music

* `play [URL]` - Starts playback from the queue, or if a URL is defined, then it will attempt to play that song, or add it to the queue, depending on if a song is already playing. If a song is already playing, you should receive an estimate of when your song should begin playing.

* `skip` - If a song is playing, the bot will skip it and play the next song in the queue.

* `queue [all]` - Lists up to the first 10 items on the queue, if no argument is given. If you add `all`, the bot will upload a list to [PasteBin](http://pastebin.com) of the entire queue, and give you 

* `repeat [true|false]` - Sets the bot to repeat the playlist, as in once a song is removed from the queue to be played, it is added back to the end of the playlist.

* `shuffle [true|false]` - Sets the bot to shuffle the playlist, as in pull a random song from the playlist, with some filters to prevent repeating songs.

* `playlist <create|show|play|delete|add|remove|rename>` - Various commands to manipulate playlists. The specific sub-commands are explained below. 
    * `create <NAME> [URL]...` - Creates a new playlist, optionally with some starting URLs.
    
    * `delete <NAME>` - Deletes a playlist with the given name.
       
    * `show [NAME]` - If a name is given, shows the songs in a given playlist; otherwise it lists the names of the playlists.
    
    * `play <NAME>` - Loads and begins playing the specified playlist.
    
    * `add <NAME> <URL> [URL]...` - Adds the specified URL, or multiple URLs to the playlist given by `NAME`.
    
    * `remove <NAME> <SONGNAME>` - Removes the specified song name, or the one that most closely matches the song name given, from the playlist given by `NAME`.
    
    * `rename <NAME> <NEWNAME>` - Renames the playlist to the new name.
    
