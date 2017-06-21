# HandieBot

HandieBot is a bot for Discord, written using Java, the [Discord4J](https://github.com/austinv11/Discord4J) library, and the [Lavaplayer](https://github.com/sedmelluq/lavaplayer) library for sound processing. It is a fully-fledged bot with the ability to manage playlists, get music from URLs, and perform other necessary functions for a clean and enjoyable user experience.

## Commands

### `play <URL>`

Issuing the `play` command attempts to load a song from a given URL, and append it to the active queue. The bot will tell the user quite obviously if their link does not work, or if there was an internal error. If there are already some songs in the queue, then this will also, if successful, tell the user approximately how long it will be until their song is played.


