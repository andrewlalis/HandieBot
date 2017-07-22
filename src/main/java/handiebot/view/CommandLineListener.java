package handiebot.view;

import handiebot.HandieBot;
import handiebot.command.CommandContext;
import handiebot.command.types.Command;
import handiebot.command.types.CommandLineCommand;
import handiebot.command.types.ContextCommand;
import handiebot.command.types.StaticCommand;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static handiebot.HandieBot.log;
import static handiebot.command.Commands.commands;

/**
 * @author Andrew Lalis
 * Class to listen for commands from the console command line.
 */
public class CommandLineListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            //user wishes to submit command.
            JTextField commandLine = (JTextField) e.getSource();
            String[] words = commandLine.getText().trim().split(" ");
            commandLine.setText(null);
            String command = words[0];
            String[] args = new String[words.length-1];
            System.arraycopy(words, 1, args, 0, words.length - 1);
            executeCommand(command, new CommandContext(HandieBot.client.getOurUser(), null, null, args));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Executes a given command on the command line. This must be written separate from the {@code executeCommand}
     * method in {@code Commands}, because here, no permissions may be checked.
     * @param command The first word typed, or the command itself.
     * @param context The list of arguments for the command.
     */
    private void executeCommand(String command, CommandContext context) {
        for (Command cmd : commands){
            if (cmd.getName().equals(command) && (cmd instanceof CommandLineCommand)){
                log.log(BotLog.TYPE.COMMAND, "Command issued: "+command);
                if (cmd instanceof StaticCommand){
                    ((StaticCommand) cmd).execute();
                } else if (cmd instanceof ContextCommand){
                    ((ContextCommand) cmd).execute(context);
                }
            }
        }
    }
}
