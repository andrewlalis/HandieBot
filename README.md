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

Commands shown in **`bold`** can only be executed by an administrator, for security reasons.

### General

* `info` - Displays the most common commands, and some basic information about the bot.

* `help` - Sends a private message to whoever issues this command. The message contains an in-depth list of all commands and their proper usage.

* **`setprefix <PREFIX>`** - Sets the prefix for all commands. Be careful, as some values will cause irreversible damage, if for example, a prefix conflicts with another bot's prefix.

### Music

* `play [URL]` - Starts playback from the queue, or if a URL is defined, then it will attempt to play that song, or add it to the queue, depending on if a song is already playing. If a song is already playing, you should receive an estimate of when your song should begin playing.

* **`stop`** - If music is playing, this will stop it.

* **`skip`** - If a song is playing, the bot will skip it and play the next song in the queue.

* `queue [all|clear|save]` - Lists up to the first 10 items on the queue, if no argument is given. 

    * `all` - The bot will upload a list to [PasteBin](http://pastebin.com) of the entire queue, provided it is greater than 10 elements, and give you a link which expires in 10 minutes. 
    
    * **`clear`** - The queue will be cleared and the current song will be stopped.
    
    * **`save <PLAYLIST>`** - The queue will be saved as a playlist with the given name.
    
* `repeat [true|false]` - Sets the bot to repeat the playlist, as in once a song is removed from the queue to be played, it is added back to the end of the playlist. If no argument is given, then this shows if the queue is currently repeating.

* `shuffle [true|false]` - Sets the bot to shuffle the playlist, as in pull a random song from the playlist, with some filters to prevent repeating songs. If no argument is given, then this shows if the queue is currently shuffling.

    >Note that for `repeat` and `shuffle`, anyone may view the status of these values, but only administrators may set them.

* **`playlist <create|show|play|delete|add|remove|rename|move>`** - Various commands to manipulate playlists. The specific sub-commands are explained below. 
    * `create <PLAYLIST> [URL]...` - Creates a new playlist, optionally with some starting URLs.
    
    * `delete <PLAYLIST>` - Deletes a playlist with the given name.
       
    * `show [PLAYLIST]` - If a name is given, shows the songs in a given playlist; otherwise it lists the names of the playlists.
    
    * `play <PLAYLIST>` - Loads and begins playing the specified playlist.
    
    * `add <PLAYLIST> <URL> [URL]...` - Adds the specified URL, or multiple URLs to the playlist given by `PLAYLIST`.
    
    * `remove <PLAYLIST> <SONGNUMBER>` - Removes the specified song name, or the one that most closely matches the song name given, from the playlist given by `PLAYLIST`.
    
    * `rename <PLAYLIST> <NEWNAME>` - Renames the playlist to the new name.
    
    * `move <PLAYLIST> <SONGNUMBER> <NEWNUMBER>` - Moves a song from one index to another index, shifting other elements as necessary.
    
### Miscellaneous

* `tengwar <to|from> <TEXT>` - Uses the [TengwarTranslatorLibrary](https://github.com/andrewlalis/TengwarTranslatorLibrary) to translate text into a Tengwar script equivalent, or translate from Tengwar to normal text. Be aware that due to the nature of this font, capitalization is not saved in Tengwar. For more information on how this works, check out my [TengwarTranslator](https://github.com/andrewlalis/TengwarTranslator).
    
    * `to <TEXT>` - Translates some text to tengwar, and responds with both the raw, UTF-8 string, and an image generated using a Tengwar font.
    
    * `from <TEXT>` - Translates some tengwar text to normal, human readable text.