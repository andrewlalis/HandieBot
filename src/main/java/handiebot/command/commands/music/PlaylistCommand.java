package handiebot.command.commands.music;

import handiebot.command.CommandContext;
import handiebot.command.CommandHandler;
import handiebot.command.types.ContextCommand;
import handiebot.lavaplayer.Playlist;
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

    public PlaylistCommand(){
        super("playlist");
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

                    break;
                case ("remove"):

                    break;
                case ("rename"):

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
        new DisappearingMessage(channel, "Please use one of the following actions: \n`<create|delete|show|play|add|remove|rename>`", 5000);
    }

    /**
     * Creates a new playlist.
     * @param context The important data such as user and arguments to be passed.
     */
    private void create(CommandContext context){
        if (context.getArgs().length >= 2) {
            Playlist playlist = new Playlist(context.getArgs()[1], context.getUser().getLongID());
            playlist.save();
            for (int i = 2; i < context.getArgs().length; i++){
                String url = context.getArgs()[i];
                playlist.loadTrack(url);
                playlist.save();
            }
            log.log(BotLog.TYPE.INFO, "Created playlist: "+playlist.getName()+" with "+playlist.getTracks().size()+" new tracks.");
            new DisappearingMessage(context.getChannel(), "Your playlist *"+playlist.getName()+"* has been created.\nType `"+ CommandHandler.PREFIX+"playlist play "+playlist.getName()+"` to play it.", 5000);
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
                    new DisappearingMessage(context.getChannel(), "The playlist *"+context.getArgs()[1]+"* has been deleted.", 5000);
                } else {
                    log.log(BotLog.TYPE.ERROR, "Unable to delete playlist: "+context.getArgs()[1]);
                    new DisappearingMessage(context.getChannel(), "The playlist was not able to be deleted.", 3000);
                }
            } else {
                new DisappearingMessage(context.getChannel(), "The name you entered is not a playlist.\nType `"+CommandHandler.PREFIX+"playlist show` to list the playlists available.", 5000);
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

        } else {
            List<String> playlists = Playlist.getAvailablePlaylists();
            StringBuilder sb = new StringBuilder("**Playlists:**\n");
            for (String playlist : playlists) {
                sb.append(playlist).append('\n');
            }
            context.getChannel().sendMessage(sb.toString());
        }
    }

}
