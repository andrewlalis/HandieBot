package handiebot.view;

import handiebot.command.Commands;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
            for (int i = 1; i < words.length; i++) {
                args[i-1] = words[i];
            }
            executeCommand(command, args);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Executes a given command on the command line.
     * @param command The first word typed, or the command itself.
     * @param args The list of arguments for the command.
     */
    private void executeCommand(String command, String[] args){
        if (command.equals("quit")){
            Commands.executeCommand("quit", null);
        }
    }
}
