package handiebot.view;

import handiebot.HandieBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Andrew Lalis
 * This class inherits JFrame and simplifies the creation of a window.
 */
public class BotWindow extends JFrame {

    public BotWindow(View view){
        super(HandieBot.APPLICATION_NAME);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog((JFrame) e.getSource(), "Are you sure you want to exit and shutdown the bot?",
                        "Confirm shutdown",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    HandieBot.quit();
                }
            }
        });
        setContentPane(view.mainPanel);
        setJMenuBar(new MenuBar());
        setPreferredSize(new Dimension(800, 600));
        pack();
        setVisible(true);
    }

}
