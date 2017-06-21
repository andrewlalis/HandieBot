package handiebot.view.actions;

import handiebot.HandieBot;
import sx.blah.discord.handle.obj.IGuild;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Andrew Lalis
 */
public class QuitAction implements ActionListener {

    private IGuild guild;

    public QuitAction(){
    }

    public QuitAction(IGuild guild){
        this.guild = guild;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (guild != null){
            HandieBot.musicPlayer.getChatChannel(this.guild).sendMessage("Quiting HandieBot");
            HandieBot.musicPlayer.quit(this.guild);
        } else {
            HandieBot.quit();
        }
    }
}
