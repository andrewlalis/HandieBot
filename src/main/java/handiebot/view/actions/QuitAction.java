package handiebot.view.actions;

import handiebot.HandieBot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Andrew Lalis
 */
public class QuitAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        HandieBot.quit();
    }
}
