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
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Command to manipulate playlists.
 */
public class PlaylistCommand extends ContextCommand {

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
                "\t`play <PLAYLIST>` - Queues all songs from a playlist.");
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
            new DisappearingMessage(context.getChannel(), "Your playlist *"+playlist.getName()+"* has been created.\nType `"+ CommandHandler.PREFIXES.get(context.getGuild())+"playlist play "+playlist.getName()+"` to play it.", 5000);
        } else {
            new DisappearingMessage(context.getChannel(), "You must specify a name for the new playlist.", 3000);
        }
    }

    /**
     * Attempts to delete a playlist.
     * @param context The context of the command.
     */
    private void delete(CommandContext context){
        if (context.getArgs().length == 2){
            if (Playlist.playlistExists(context.getArgs()[1])){
                File f = new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[1].replace(" ", "_")+".txt");
                boolean success = f.delete();
                if (success){
                    log.log(BotLog.TYPE.INFO, "The playlist ["+context.getArgs()[1]+"] has been deleted.");
                    new DisappearingMessage(context.getChannel(), "The playlist *"+context.getArgs()[1]+"* has been deleted.", 5000);
                } else {
                    log.log(BotLog.TYPE.ERROR, "Unable to delete playlist: "+context.getArgs()[1]);
                    new DisappearingMessage(context.getChannel(), "The playlist was not able to be deleted.", 3000);
                }
            } else {
                new DisappearingMessage(context.getChannel(), "The name you entered is not a playlist.\nType `"+CommandHandler.PREFIXES.get(context.getGuild())+"playlist show` to list the playlists available.", 5000);
            }
        } else {
            new DisappearingMessage(context.getChannel(), "You must specify the name of a playlist to delete.", 3000);
        }
    }

    /**
     * Displays the list of playlists, or a specific playlist's songs.
     * @param context The data to be passed, containing channel and arguments.
     */
    private void show(CommandContext context){
        if (context.getArgs().length > 1){
            if (Playlist.playlistExists(context.getArgs()[1])){
                Playlist playlist = new Playlist(context.getArgs()[1]);
                playlist.load();
                IMessage message = context.getChannel().sendMessage(playlist.toString());
                DisappearingMessage.deleteMessageAfter(12000, message);
            } else {
                new DisappearingMessage(context.getChannel(), "The playlist you specified does not exist.\nUse `"+CommandHandler.PREFIXES.get(context.getGuild())+"playlist show` to view available playlists.", 5000);
            }
        } else {
            List<String> playlists = Playlist.getAvailablePlaylists();
            StringBuilder sb = new StringBuilder("**Playlists:**\n");
            for (String playlist : playlists) {
                sb.append(playlist).append('\n');
            }
            IMessage message = context.getChannel().sendMessage(sb.toString());
            DisappearingMessage.deleteMessageAfter(12000, message);
        }
    }

    /**
     * Attempts to add a song or multiple songs to a playlist.
     * @param context The command context.
     */
    private void add(CommandContext context){
        if (context.getArgs().length > 2){
            if (!Playlist.playlistExists(context.getArgs()[1])){
                new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist.", 3000);
                return;
            }
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            for (int i = 2; i < context.getArgs().length; i++){
                playlist.loadTrack(context.getArgs()[i]);
                new DisappearingMessage(context.getChannel(), "Added track to *"+playlist.getName()+"*.", 3000);
            }
            playlist.save();
            IMessage message = context.getChannel().sendMessage(playlist.toString());
            log.log(BotLog.TYPE.INFO, "Added song(s) to playlist ["+playlist.getName()+"].");
            DisappearingMessage.deleteMessageAfter(6000, message);
        } else {
            if (context.getArgs().length == 1){
                new DisappearingMessage(context.getChannel(), "You must provide the name of a playlist to add a URL to.\nUse '"+CommandHandler.PREFIXES.get(context.getGuild())+"playlist show` to view available playlists.", 5000);
            } else {
                new DisappearingMessage(context.getChannel(), "You must provide at least one URL to add.", 3000);
            }
        }
    }

    /**
     * Shifts the named playlist to the active playlist and begins playback in accordance with the Music Player.
     * @param context The command context.
     */
    private void play(CommandContext context){
        if (context.getArgs().length == 2){
            if (!Playlist.playlistExists(context.getArgs()[1])){
                new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist.", 3000);
                return;
            }
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.setPlaylist(playlist);
            HandieBot.musicPlayer.getMusicManager(context.getGuild()).scheduler.nextTrack();
            log.log(BotLog.TYPE.INFO, "Loaded playlist ["+playlist.getName()+"].");
            new DisappearingMessage(context.getChannel(), "Now playing from playlist: *"+playlist.getName()+"*.", 6000);
        } else {
            new DisappearingMessage(context.getChannel(), "You must provide a playlist to play.\nUse '"+CommandHandler.PREFIXES.get(context.getGuild())+"playlist show` to view available playlists.", 3000);
        }
    }

    /**
     * Attempts to rename a playlist.
     * @param context The command context.
     */
    private void rename(CommandContext context){
        if (context.getArgs().length == 3){
            if (!Playlist.playlistExists(context.getArgs()[1])){
                new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist.", 3000);
                return;
            }
            File f = new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[1].replace(" ", "_")+".txt");
            boolean success = f.renameTo(new File(System.getProperty("user.home")+"/.handiebot/playlist/"+context.getArgs()[2].replace(" ", "_")+".txt"));
            if (success){
                new DisappearingMessage(context.getChannel(), "The playlist *"+context.getArgs()[1]+"* has been renamed to *"+context.getArgs()[2]+"*.", 6000);
                log.log(BotLog.TYPE.INFO, "Playlist "+context.getArgs()[1]+" renamed to "+context.getArgs()[2]+".");
            } else {
                new DisappearingMessage(context.getChannel(), "Unable to rename playlist.", 3000);
                log.log(BotLog.TYPE.ERROR, "Unable to rename playlist "+context.getArgs()[1]+" to "+context.getArgs()[2]+".");
            }
        } else {
            new DisappearingMessage(context.getChannel(), "You must include the original playlist, and a new name for it.", 3000);
        }
    }

    /**
     * Attempst to remove the song at a specified index of the playlist.
     * @param context The command context.
     */
    private void remove(CommandContext context){
        if (context.getArgs().length == 3){
            if (!Playlist.playlistExists(context.getArgs()[1])){
                new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist.", 3000);
                return;
            }
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            try{
                int index = Integer.parseInt(context.getArgs()[2]) - 1;
                UnloadedTrack track = playlist.getTracks().get(index);
                playlist.removeTrack(track);
                playlist.save();
                new DisappearingMessage(context.getChannel(), "Removed song: *"+track.getTitle()+"* from playlist **"+playlist.getName()+"**.", 6000);
                log.log(BotLog.TYPE.MUSIC, "Removed song: "+track.getTitle()+" from playlist ["+playlist.getName()+"].");
                DisappearingMessage.deleteMessageAfter(6000, context.getChannel().sendMessage(playlist.toString()));
            } catch (IndexOutOfBoundsException | NumberFormatException e){
                new DisappearingMessage(context.getChannel(), "Unable to remove the specified song.", 3000);
                log.log(BotLog.TYPE.ERROR, "Unable to remove song from playlist: ["+playlist.getName()+"].");
                e.printStackTrace();
            }

        } else {
            new DisappearingMessage(context.getChannel(), "You must provide a playlist name, followed by the index number of a song to remove.", 5000);
        }
    }

    /**
     * Moves a song from one index to another.
     * @param context The command context.
     */
    private void move(CommandContext context){
        if (context.getArgs().length == 4){
            if (!Playlist.playlistExists(context.getArgs()[1])){
                new DisappearingMessage(context.getChannel(), "The playlist you entered does not exist.", 3000);
                return;
            }
            Playlist playlist = new Playlist(context.getArgs()[1]);
            playlist.load();
            int oldIndex = -1;
            int newIndex = -1;
            try {
                oldIndex = Integer.parseInt(context.getArgs()[2])-1;
                newIndex = Integer.parseInt(context.getArgs()[3])-1;
            } catch (NumberFormatException e){
                new DisappearingMessage(context.getChannel(), "You must enter two integer values for the song indices.", 5000);
            }
            UnloadedTrack track;
            if ((oldIndex > -1 && oldIndex < playlist.getTrackCount()) &&
                    (newIndex > -1 && newIndex <= playlist.getTrackCount())){
                track = playlist.getTracks().remove(oldIndex);
                playlist.getTracks().add(newIndex, track);
                playlist.save();
                new DisappearingMessage(context.getChannel(), "Moved song *"+track.getTitle()+"* from position "+(oldIndex+1)+" to position "+(newIndex+1), 6000);
                log.log(BotLog.TYPE.MUSIC, "Moved song "+track.getTitle()+" from position "+(oldIndex+1)+" to position "+(newIndex+1));
            } else {
                new DisappearingMessage(context.getChannel(), "The song indices are invalid. You specified moving song "+oldIndex+" to position "+newIndex+". ", 5000);
            }
        } else {
            new DisappearingMessage(context.getChannel(), "You must provide a playlist name, followed by the song index, and a new index for that song.", 5000);
        }
    }

}
