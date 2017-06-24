package handiebot.utils;

import handiebot.view.BotLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * Class to simplify file operations.
 */
public class FileUtil {

    public static String getDataDirectory(){
        return System.getProperty("user.home")+"/.handiebot/";
    }

    public static List<String> getLinesFromFile(File file){
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void writeLinesToFile(List<String> lines, File file){
        if (lines.size() == 0){
            return;
        }
        if (!file.exists()){
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    log.log(BotLog.TYPE.ERROR, "Unable to create file. "+file.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.log(BotLog.TYPE.ERROR, "Unable to create file. "+file.getAbsolutePath());
                return;
            }
        }
        try (PrintWriter writer = new PrintWriter(file)){
            while (lines.size() > 0) {
                writer.println(lines.remove(0));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.log(BotLog.TYPE.ERROR, "Unable to write to file. "+file.getAbsolutePath());
        }
    }

}
