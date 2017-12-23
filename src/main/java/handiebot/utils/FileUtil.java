package handiebot.utils;

import handiebot.HandieBot;
import handiebot.view.BotLog;

import java.io.*;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static handiebot.HandieBot.log;
import static handiebot.HandieBot.resourceBundle;

/**
 * @author Andrew Lalis
 * Class to simplify file operations.
 */
public class FileUtil {

    /**
     * Gets the directory where handiebot's data is stored.
     * @return The directory leading to 'user.home'.
     */
    public static String getDataDirectory(){
        return System.getProperty("user.home")+"/.handiebot/";
    }

    /**
     * Reads a file, and returns a list of lines in the file.
     * @param file The file to read.
     * @return A list of lines from the file.
     */
    public static List<String> getLinesFromFile(File file){
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Writes a list of strings to a file, separated by newlines.
     * @param lines The list of lines.
     * @param file The file to write to.
     */
    public static void writeLinesToFile(List<String> lines, File file){
        if (lines.size() == 0){
            return;
        }
        if (!file.exists()){
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    log.log(BotLog.TYPE.ERROR, MessageFormat.format(resourceBundle.getString("fileutil.fileCreateError"), file.getAbsolutePath()));
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.log(BotLog.TYPE.ERROR, MessageFormat.format(resourceBundle.getString("fileutil.fileCreateError"), file.getAbsolutePath()));
                return;
            }
        }
        try (PrintWriter writer = new PrintWriter(file)){
            while (lines.size() > 0) {
                writer.println(lines.remove(0));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.log(BotLog.TYPE.ERROR, MessageFormat.format(resourceBundle.getString("fileutil.writeError"), file.getAbsolutePath()));
        }
    }

    /**
     * Saves the global set of settings.
     * @param settings The settings to save.
     */
    public static void saveSettings(Properties settings){
        try {
            settings.store(new FileWriter(FileUtil.getDataDirectory()+"settings"), "Settings for HandieBot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a properties file.
     * @return The settings saved locally for HandieBot.
     */
    public static Properties loadSettings(){
        Properties settings = new Properties();
        try {
            settings.load(HandieBot.class.getClassLoader().getResourceAsStream("default_settings"));
            settings.load(new FileInputStream(FileUtil.getDataDirectory()+"settings"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * Reads the private discord token necessary to start the bot. If this fails, the bot will shut down.
     * @return The string token needed to log in.
     */
    public static String readToken(){
        String path = FileUtil.getDataDirectory()+"token.txt";
        String result = "";
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            result = reader.readLine();
        } catch (IOException e) {
            System.err.println("Unable to find the token file. You are unable to start the bot without this.");
        }
        return result;
    }

}
