package handiebot.command.commands.support;

import handiebot.command.CommandContext;
import handiebot.command.types.ContextCommand;
import handiebot.utils.MessageUtils;
import handiebot.view.BotLog;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Command to allow anyone to report a user, and have all administrators be notified of this report, and then
 * choose a proper punishment.
 */
public class ReportCommand extends ContextCommand {


    public ReportCommand() {
        super("report",
                "<USER> [REASON]",
                resourceBundle.getString("commands.command.report.description"),
                0);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length < 1){
            sendMessage(resourceBundle.getString("commands.command.report.error"), context.getChannel());
            return;
        }
        List<IRole> roles = context.getGuild().getRoles();
        List<IRole> adminRoles = new ArrayList<>();
        for (IRole role : roles){
            if (role.getPermissions().contains(Permissions.VOICE_MUTE_MEMBERS) && role.getPermissions().contains(Permissions.VOICE_DEAFEN_MEMBERS)){
                //The role has sufficient reason to be notified.
                adminRoles.add(role);
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle("Report");
        builder.withColor(Color.red);
        StringBuilder sb = new StringBuilder();
        sb.append(context.getUser().mention()).append(" has reported ").append(context.getArgs()[0]).append(".");
        if (context.getArgs().length > 1){
            sb.append('\n').append("Reason: ").append(MessageUtils.getTextFromArgs(context.getArgs(), 1));
        }
        builder.withDescription(sb.toString());
        EmbedObject eo = builder.build();
        for (IUser user : context.getGuild().getUsers()){
            if (!user.isBot()) {
                for (IRole role : adminRoles) {
                    if (user.getRolesForGuild(context.getGuild()).contains(role)) {
                        //The user has sufficient reason to be notified.
                        user.getOrCreatePMChannel().sendMessage(eo);
                        break;
                    }
                }
            }
        }
        log.log(BotLog.TYPE.COMMAND, context.getGuild(), eo.description);
    }
}
