package handiebot.command.commands.music;

import com.google.api.services.youtube.model.Video;
import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.ReactionHandler;
import handiebot.command.reactionListeners.YoutubePlaylistAddListener;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.MessageUtils;
import handiebot.utils.YoutubeSearch;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.*;
import static handiebot.utils.MessageUtils.sendMessage;
import static handiebot.utils.YoutubeSearch.WATCH_URL;

/**
 * @author Andrew Lalis
 * Command to manipulate playlists.
 */
public class PlaylistCommand extends ContextCommand {

    public PlaylistCommand(){
        super("playlist",
                "<create|delete|show|add|remove|rename|move|play> [PLAYLIST]",
                resourceBundle.getString("commands.command.playlist.description.main")+"\n" +
                        "\t`create <PLAYLIST>` - "+resourceBundle.getString("commands.command.playlist.description.create")+"\n" +
                        "\t`delete <PLAYLIST>` - "+resourceBundle.getString("commands.command.playlist.description.delete")+"\n" +
                        "\t`show [PLAYLIST]` - "+resourceBundle.getString("commands.command.playlist.description.show")+"\n" +
                        "\t`add <PLAYLIST> <URL URL...|SEARCHTEXT, SEARCHTEXT...>` - "+resourceBundle.getString("commands.command.playlist.description.add")+"\n" +
                        "\t`remove <PLAYLIST> <SONGINDEX>` - "+resourceBundle.getString("commands.command.playlist.description.remove")+"\n" +
                        "\t`rename <PLAYLIST> <NEWNAME>` - "+resourceBundle.getString("commands.command.playlist.description.rename")+"\n" +
                        "\t`move <PLAYLIST> <OLDINDEX> <NEWINDEX>` - "+resourceBundle.getString("commands.command.playlist.description.move")+"\n" +
                        "\t`play <PLAYLIST>` - "+resourceBundle.getString("commands.command.playlist.description.play"),
                8);
    }

    @Override
    public void execute(CommandContext context) {
        String[] args = context.getArgs();
        if (args.length > 0){
            switch (args[0]){
                case ("create"):
                    create(context);
                    break;
                case ("delete"):
                    delete(context);
                    break;
                case ("show"):
                    show(context);
                    break;
                case ("add"):
                    add(context);
                    break;
                case ("play"):
                    play(context);
                    break;
                case ("remove"):
                    remove(context);
                    break;
                case ("rename"):
                    rename(context);
                    break;
                case ("move"):
                    move(context);
                    break;
                default:
                    incorrectMainArg(context.getChannel());
                    break;
            }
        } else {
            incorrectMainArg(context.getChannel());
        }
    }

    /**
     * Error message to show if the main argument is incorrect.
     * @param channel The channel to show the error message in.
     */
    private void incorrectMainArg(IChannel channel){
        IMessage message = sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.incorrectMainArg"), this.getUsage(channel.getGuild())), channel);
        MessageUtils.deleteMessageAfter(5000, message);
    }

    /**
     * Creates a new playlist.
     * @param context The important data such as user and arguments to be passed.
     */
    private void create(CommandContext context){
        if (context.getArgs().length >= 2) {
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.save();
            if (context.getArgs().length > 2) {
                for (int i = 2; i < context.getArgs().length; i++) {
                    String url = context.getArgs()[i];
                    playlist.loadTrack(url);
                }
                playlist.save();
            }
            log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.playlist.createdPlaylist.log"), playlist.getName(), playlist.getTrackCount()));
            sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.createdPlaylist.message"), playlist.getName(), this.getPrefixedName(context.getGuild()), playlist.getName()), context.getChannel());
            window.updatePlaylistNames();//Refresh the list of names in the GUI.
        } else {
            sendMessage(resourceBundle.getString("commands.command.playlist.error.createPlaylistName"), context.getChannel());
        }
    }

    /**
     * Attempts to delete a playlist.
     * @param context The context of the command.
     */
    private void delete(CommandContext context){
        if (context.getArgs().length == 2){
            if (!checkForPlaylist(context))
                return;
            File f = new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[1].replace(" ", "_")+".txt");
            boolean success = f.delete();
            if (success){
                log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.playlist.delete.log"), context.getArgs()[1]));
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.delete.message"), context.getArgs()[1]), context.getChannel());
                window.updatePlaylistNames();//Refresh the list of names in the GUI.
            } else {
                log.log(BotLog.TYPE.ERROR, MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.delete.log"), context.getArgs()[1]));
                sendMessage(resourceBundle.getString("commands.command.playlist.error.delete.message"), context.getChannel());
            }
        } else {
            sendMessage(resourceBundle.getString("commands.command.playlist.error.deletePlaylistName"), context.getChannel());
        }
    }

    /**
     * Displays the list of playlists, or a specific playlist's songs.
     * @param context The data to be passed, containing channel and arguments.
     */
    private void show(CommandContext context){
        if (context.getArgs().length > 1){
            if (!checkForPlaylist(context))
                return;
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            sendMessage(playlist.toString(), context.getChannel());
        } else {
            List<String> playlists = Playlist.getAvailablePlaylists();
            StringBuilder sb = new StringBuilder("**Playlists:**\n");
            for (String playlist : playlists) {
                sb.append(playlist).append('\n');
            }
            sendMessage(sb.toString(), context.getChannel());
        }
    }

    /**
     * Attempts to add a song or multiple songs to a playlist.
     * @param context The command context.
     */
    private void add(CommandContext context){
        if (context.getArgs().length > 2){
            if (!checkForPlaylist(context))
                return;
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            if (context.getArgs()[2].startsWith("http")){
                //These are songs, so add them immediately.
                for (int i = 2; i < context.getArgs().length; i++){
                    playlist.loadTrack(context.getArgs()[i]);
                    sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.add.message"), playlist.getName()), context.getChannel());
                }
                playlist.save();
                sendMessage(playlist.toString(), context.getChannel());
                log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.playlist.add.log"), playlist.getName()));
            } else {
                //This is a youtube search query.
                List<Video> videos = YoutubeSearch.query(MessageUtils.getTextFromArgs(context.getArgs(), 2));
                if (videos != null) {
                    List<String> urls = new ArrayList<>(videos.size());
                    videos.forEach((video) -> urls.add(WATCH_URL+video.getId()));
                    IMessage message = YoutubeSearch.displayChoicesDialog(videos, context.getChannel());
                    ReactionHandler.addListener(new YoutubePlaylistAddListener(message, context.getUser(), urls, playlist));
                }
            }
        } else {
            if (context.getArgs().length == 1){
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.addNameNeeded"), getPlaylistShowString(context)), context.getChannel());
            } else {
                sendMessage(resourceBundle.getString("commands.command.playlist.error.addUrlNeeded"), context.getChannel());
            }
        }
    }

    /**
     * Shifts the named playlist to the active playlist and begins playback in accordance with the Music Player.
     * @param context The command context.
     */
    private void play(CommandContext context){
        if (context.getArgs().length == 2){
            if (!checkForPlaylist(context))
                return;
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.playlist.play.log"), playlist.getName()));
            sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.play.message"), playlist.getName()), context.getChannel());
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.setPlaylist(playlist);
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.nextTrack();
        } else {
            sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.playPlaylistNeeded"), getPlaylistShowString(context)), context.getChannel());
        }
    }

    /**
     * Attempts to rename a playlist.
     * @param context The command context.
     */
    private void rename(CommandContext context){
        if (context.getArgs().length == 3){
            if (!checkForPlaylist(context))
                return;
            File f = new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[1].replace(" ", "_")+".txt");
            boolean success = f.renameTo(new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[2].replace(" ", "_")+".txt"));
            if (success){
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.rename.message"), context.getArgs()[1], context.getArgs()[2]), context.getChannel());
                log.log(BotLog.TYPE.INFO, MessageFormat.format(resourceBundle.getString("commands.command.playlist.rename.log"), context.getArgs()[1], context.getArgs()[2]));
            } else {
                String response = MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.renameError"), context.getArgs()[1], context.getArgs()[2]);
                sendMessage(response, context.getChannel());
                log.log(BotLog.TYPE.ERROR, response);
            }
        } else {
            sendMessage(resourceBundle.getString("commands.command.playlist.error.renameBadArgs"), context.getChannel());
        }
    }

    /**
     * Attempst to remove the song at a specified index of the playlist.
     * @param context The command context.
     */
    private void remove(CommandContext context){
        if (context.getArgs().length == 3){
            if (!checkForPlaylist(context))
                return;
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            try{
                int index = Integer.parseInt(context.getArgs()[2]) - 1;
                UnloadedTrack track = playlist.getTracks().get(index);
                playlist.removeTrack(track);
                playlist.save();
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.remove.message"), track.getTitle(), playlist.getName()), context.getChannel());
                log.log(BotLog.TYPE.MUSIC, MessageFormat.format(resourceBundle.getString("commands.command.playlist.remove.log"), track.getTitle(), playlist.getName()));
            } catch (IndexOutOfBoundsException | NumberFormatException e){
                String response = MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.removeError"), playlist.getName());
                sendMessage(response, context.getChannel());
                log.log(BotLog.TYPE.ERROR, response);
                e.printStackTrace();
            }

        } else {
            sendMessage(resourceBundle.getString("commands.command.playlist.error.removeBadArgs"), context.getChannel());
        }
    }

    /**
     * Moves a song from one index to another.
     * @param context The command context.
     */
    private void move(CommandContext context){
        if (context.getArgs().length == 4){
            if (!checkForPlaylist(context))
                return;
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            int oldIndex = -1;
            int newIndex = -1;
            try {
                oldIndex = Integer.parseInt(context.getArgs()[2])-1;
                newIndex = Integer.parseInt(context.getArgs()[3])-1;
            } catch (NumberFormatException e){
                sendMessage(resourceBundle.getString("commands.command.playlist.error.moveIndexError"), context.getChannel());
            }
            UnloadedTrack track;
            if ((oldIndex > -1 && oldIndex < playlist.getTrackCount()) &&
                    (newIndex > -1 && newIndex <= playlist.getTrackCount())){
                track = playlist.getTracks().remove(oldIndex);
                playlist.getTracks().add(newIndex, track);
                playlist.save();
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.move.message"), track.getTitle(), oldIndex + 1, newIndex + 1), context.getChannel());
                log.log(BotLog.TYPE.MUSIC, MessageFormat.format(resourceBundle.getString("commands.command.playlist.move.log"), track.getTitle(), oldIndex + 1, newIndex + 1));
            } else {
                sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.moveInvalidIndex"), oldIndex, newIndex), context.getChannel());
            }
        } else {
            sendMessage(resourceBundle.getString("commands.command.playlist.error.moveBadArgs"), context.getChannel());
        }
    }

    /**
     * Checks if a playlist exists, and if not, outputs a message to let people know.
     * Used before most of the commands here to make sure the playlist actually exists, and exit if it doesn't.
     * @param context The command context.
     * @return True if the playlist exists, false otherwise.
     */
    private boolean checkForPlaylist(CommandContext context){
        if (Playlist.playlistExists(context.getArgs()[1])){
            return true;
        } else {
            IMessage message = sendMessage(MessageFormat.format(resourceBundle.getString("commands.command.playlist.error.playlistDoesNotExist"), getPlaylistShowString(context)), context.getChannel());
            MessageUtils.deleteMessageAfter(3000, message);
            return false;
        }
    }

    /**
     * Simply returns a string that uses the correct prefix.
     * @param context The command context.
     * @return A correct suggestion on how to view all playlists.
     */
    private String getPlaylistShowString(CommandContext context){
        return MessageFormat.format(resourceBundle.getString("commands.command.playlist.showHelpString"), CommandHandler.PREFIXES.get(context.getGuild()));
    }

}
