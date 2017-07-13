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
            System.arraycopy(words, 1, args, 0, words.length - 1);
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
    private void executeCommand(String command, String[] args) {
        switch (command) {
            case "quit":
                Commands.executeCommand("quit", null);
                break;
        }
    }
}
