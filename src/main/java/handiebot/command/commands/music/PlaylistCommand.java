package handiebot.command.commands.music;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.playlist.Playlist;
import handiebot.lavaplayer.playlist.UnloadedTrack;
import handiebot.utils.DisappearingMessage;
import handiebot.view.BotLog;
import sx.blah.discord.handle.obj.IChannel;

import java.io.File;
import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Command to manipulate playlists.
 */
public class PlaylistCommand extends ContextCommand {
//TODO: Add specific permissions per argument.
    public PlaylistCommand(){
        super("playlist",
                "<create|delete|show|add|remove|rename|move|play> [PLAYLIST]",
        "Do something with a playlist.\n" +
                "\t`create <PLAYLIST>` - Creates a playlist.\n" +
                "\t`delete <PLAYLIST>` - Deletes a playlist.\n" +
                "\t`show [PLAYLIST]` - If a playlist given, show that, otherwise show a list of playlists.\n" +
                "\t`add <PLAYLIST> <URL> [URL]...` - Adds one or more songs to a playlist.\n" +
                "\t`remove <PLAYLIST> <SONGINDEX>` - Removes a song from a playlist.\n" +
                "\t`rename <PLAYLIST> <NEWNAME>` - Renames a playlist.\n" +
                "\t`move <PLAYLIST> <OLDINDEX> <NEWINDEX>` - Moves a song from one index to another.\n" +
                "\t`play <PLAYLIST>` - Queues all songs from a playlist.",
                0);
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
        new DisappearingMessage(channel, "To use the playlist command: \n"+this.getUsage(channel.getGuild()), 5000);
    }

    /**
     * Creates a new playlist.
     * @param context The important data such as user and arguments to be passed.
     */
    private void create(CommandContext context){
        if (context.getArgs().length >= 2) {
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.save();
            for (int i = 2; i < context.getArgs().length; i++){
                String url = context.getArgs()[i];
                playlist.loadTrack(url);
            }
            playlist.save();
            log.log(BotLog.TYPE.INFO, "Created playlist: "+playlist.getName()+" with "+playlist.getTrackCount()+" new tracks.");
            context.getChannel().sendMessage("Your playlist *"+playlist.getName()+"* has been created.\nType `"+this.getPrefixedName(context.getGuild())+" play "+playlist.getName()+"` to play it.");
        } else {
            context.getChannel().sendMessage("You must specify a name for the new playlist.");
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
                log.log(BotLog.TYPE.INFO, "The playlist ["+context.getArgs()[1]+"] has been deleted.");
                context.getChannel().sendMessage("The playlist *"+context.getArgs()[1]+"* has been deleted.");
            } else {
                log.log(BotLog.TYPE.ERROR, "Unable to delete playlist: "+context.getArgs()[1]);
                context.getChannel().sendMessage("The playlist could not be deleted.");
            }
        } else {
            context.getChannel().sendMessage("You must specify the name of a playlist to delete.");
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
            context.getChannel().sendMessage(playlist.toString());
        } else {
            List<String> playlists = Playlist.getAvailablePlaylists();
            StringBuilder sb = new StringBuilder("**Playlists:**\n");
            for (String playlist : playlists) {
                sb.append(playlist).append('\n');
            }
            context.getChannel().sendMessage(sb.toString());
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
            for (int i = 2; i < context.getArgs().length; i++){
                playlist.loadTrack(context.getArgs()[i]);
                context.getChannel().sendMessage("Added track to *"+playlist.getName()+"*.");
            }
            playlist.save();
            context.getChannel().sendMessage(playlist.toString());
            log.log(BotLog.TYPE.INFO, "Added song(s) to playlist ["+playlist.getName()+"].");
        } else {
            if (context.getArgs().length == 1){
                context.getChannel().sendMessage("You must provide the name of a playlist to add a URL to."+getPlaylistShowString(context));
            } else {
                context.getChannel().sendMessage("You must provide at least one URL to add.");
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
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.setPlaylist(playlist);
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.nextTrack();
            log.log(BotLog.TYPE.INFO, "Loaded playlist ["+playlist.getName()+"].");
            context.getChannel().sendMessage("Loaded songs from playlist: *"+playlist.getName()+"*.");
        } else {
            context.getChannel().sendMessage("You must provide a playlist to play."+getPlaylistShowString(context));
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
                context.getChannel().sendMessage("The playlist *"+context.getArgs()[1]+"* has been renamed to *"+context.getArgs()[2]+"*.");
                log.log(BotLog.TYPE.INFO, "Playlist "+context.getArgs()[1]+" renamed to "+context.getArgs()[2]+".");
            } else {
                context.getChannel().sendMessage("Unable to rename playlist.");
                log.log(BotLog.TYPE.ERROR, "Unable to rename playlist "+context.getArgs()[1]+" to "+context.getArgs()[2]+".");
            }
        } else {
            context.getChannel().sendMessage("You must include the original playlist, and a new name for it.");
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
                context.getChannel().sendMessage("Removed song: *"+track.getTitle()+"* from playlist **"+playlist.getName()+"**.");
                log.log(BotLog.TYPE.MUSIC, "Removed song: "+track.getTitle()+" from playlist ["+playlist.getName()+"].");
            } catch (IndexOutOfBoundsException | NumberFormatException e){
                context.getChannel().sendMessage("Unable to remove the specified song.");
                log.log(BotLog.TYPE.ERROR, "Unable to remove song from playlist: ["+playlist.getName()+"].");
                e.printStackTrace();
            }

        } else {
            context.getChannel().sendMessage("You must provide a playlist name, followed by the index number of a song to remove.");
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
                context.getChannel().sendMessage("You must enter two positive natural numbers for the song indices.");
            }
            UnloadedTrack track;
            if ((oldIndex > -1 && oldIndex < playlist.getTrackCount()) &&
                    (newIndex > -1 && newIndex <= playlist.getTrackCount())){
                track = playlist.getTracks().remove(oldIndex);
                playlist.getTracks().add(newIndex, track);
                playlist.save();
                context.getChannel().sendMessage("Moved song *"+track.getTitle()+"* from position "+(oldIndex+1)+" to position "+(newIndex+1));
                log.log(BotLog.TYPE.MUSIC, "Moved song "+track.getTitle()+" from position "+(oldIndex+1)+" to position "+(newIndex+1));
            } else {
                context.getChannel().sendMessage("The song indices are invalid. You specified moving song "+oldIndex+" to position "+newIndex+".");
            }
        } else {
            context.getChannel().sendMessage("You must provide a playlist name, followed by the song index, and a new index for that song.");
        }
    }

    /**
     * Checks if a playlist exists, and if not, outputs a message to let people know.
     * Used before most of the commands here to make sure the playlist actually exists, and exit if it doesn't.
     * @param context The command context.
     * @return True if the playlist exists, false otherwise.
     */
    private boolean checkForPlaylist(CommandContext context){
        if (!Playlist.playlistExists(context.getArgs()[1])){
            new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist."+getPlaylistShowString(context), 3000);
            return false;
        }
        return true;
    }

    /**
     * Simply returns a string that uses the correct prefix.
     * @param context The command context.
     * @return A correct suggestion on how to view all playlists.
     */
    private String getPlaylistShowString(CommandContext context){
        return "\nUse `"+CommandHandler.PREFIXES.get(context.getGuild())+"playlist show` to view available playlists.";
    }

}
