package handiebot.view;

import handiebot.command.Commands;
import handiebot.view.actions.ActionItem;
import handiebot.view.actions.CommandAction;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis & Zino Holwerda
 * Custom menu bar to be added to the console control panel.
 */
public class MenuBar extends JMenuBar {
//TODO: Implement a way to restart the program in nederlands.
    private BotWindow window;
    private int language;

    public MenuBar(BotWindow window){
        this.window = window;
        JMenu fileMenu = new JMenu(resourceBundle.getString("menu.fileMenu.title"));
            fileMenu.add(new ActionItem(resourceBundle.getString("menu.fileMenu.quit"), new CommandAction(Commands.get("quit"))));
            this.add(fileMenu);
        JMenu viewMenu = new JMenu(resourceBundle.getString("menu.viewMenu.view"));
            JMenu language =  new JMenu(resourceBundle.getString("menu.viewMenu.language"));
                language.add(new ActionItem(resourceBundle.getString("menu.viewMenu.language.english"), e -> resourceBundle = ResourceBundle.getBundle("Strings", Locale.US)));
                language.add(new ActionItem(resourceBundle.getString("menu.viewMenu.language.dutch"), e -> resourceBundle = ResourceBundle.getBundle("Strings", Locale.forLanguageTag("nl"))));
            viewMenu.add(language);
            viewMenu.add(new ActionItem(resourceBundle.getString("menu.viewMenu.playlistsVisible"), e -> window.togglePlaylistsVisibility()));
            this.add(viewMenu);
    }

}
