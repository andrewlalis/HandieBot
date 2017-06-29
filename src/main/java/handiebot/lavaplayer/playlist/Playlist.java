package handiebot.lavaplayer.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import handiebot.view.BotLog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static handiebot.HandieBot.log;

/**
 * @author Andrew Lalis
 * A Playlist is a list of Tracks which a track scheduler can pull from to create a queue filled with songs. The
 * playlist is persistent, i.e. it is saved into a file.
 * Be careful, though, as the playlist is not saved in this class, but must be saved manually by whoever is operating
 * on the playlist.
 */
public class Playlist {
//TODO: externalize strings
    private String name;

    private List<UnloadedTrack> tracks;

    /**
     * Creates an empty playlist template.
     * Depending on the circumstances, you may need to call {@code load()} to fill the playlist from a file.
     * @param name The name of the playlist.
     */
    public Playlist(String name){
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getTrackCount(){
        return this.tracks.size();
    }

    public List<UnloadedTrack> getTracks(){
        return this.tracks;
    }

    public void addTrack(UnloadedTrack track){
        this.tracks.add(track);
    }

    public void removeTrack(UnloadedTrack track){
        this.tracks.remove(track);
    }

    /**
     * Copies all the tracks from another playlist onto this one.
     * @param playlist A playlist.
     */
    public void copy(Playlist playlist){
        this.getTracks().clear();
        for (UnloadedTrack track : playlist.getTracks()){
            this.tracks.add(track.clone());
        }
    }

    /**
     * Clears the list of tracks.
     */
    public void clear(){
        this.tracks.clear();
    }

    /**
     * Loads and returns the audio track that's first on the list.
     * This removes that track from the playlist.
     * @param shouldShuffle If this is true, the track returned will be chosen randomly.
     * @return The AudioTrack corresponding to the next UnloadedTrack in the list.
     */
    public AudioTrack loadNextTrack(boolean shouldShuffle){
        if (this.getTrackCount() == 0){
            return null;
        }
        if (shouldShuffle){
            return this.tracks.remove(getShuffledIndex(this.tracks.size())).loadAudioTrack();
        } else {
            return this.tracks.remove(0).loadAudioTrack();
        }
    }

    /**
     * Attempts to load a track or playlist from a URL, and add it to the tracks list.
     * @param url The URL to get the song/playlist from.
     */
    public void loadTrack(String url){
        try {
            UnloadedTrack track = new UnloadedTrack(url);
            this.tracks.add(track);
            log.log(BotLog.TYPE.MUSIC, "Added "+track.getTitle()+" to playlist ["+this.name+"].");
        } catch (Exception e) {
            log.log(BotLog.TYPE.ERROR, "Unable to add "+url+" to the playlist ["+this.name+"].");
            e.printStackTrace();
        }
    }

    /**
     * Gets a 'shuffled index' from a given list length. That means:
     * - A random number from 0 to (listLength-1) - threshold*(listLength), where threshold is some percentage of
     * recent songs that should be ignored; for example, the most recent 20% of the playlist can be ignored.
     * - A greater likelihood for numbers closer to 0 (those which have not been played in a while).
     * @param listLength The number of items in a potential list to choose from.
     * @return A pseudo-random choice as to which item to pick from the list.
     */
    public static int getShuffledIndex(int listLength){
        float threshold = 0.2f;
        int trueLength = listLength - (int)(threshold*(float)listLength);
        Random rand = new Random();
        //TODO Add in a small gradient in chance for a song to be picked.
        return rand.nextInt(trueLength);
    }

    /**
     * Saves the playlist to a file in its name. The playlists are saved into a file in the user's home directory.
     */
    public void save(){
        String homeDir = System.getProperty("user.home");
        File playlistDir = new File(homeDir+"/.handiebot/playlist");
        if (!playlistDir.exists()){
            if (!playlistDir.mkdirs()){
                log.log(BotLog.TYPE.ERROR, "Unable to make directory: "+playlistDir.getPath());
                return;
            }
        }
        File playlistFile = new File(playlistDir.getPath()+"/"+this.name.replace(" ", "_")+".txt");
        log.log(BotLog.TYPE.INFO, "Saving playlist to: "+playlistFile.getAbsolutePath());
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playlistFile)))){
            writer.write(Integer.toString(this.tracks.size())+'\n');
            for (UnloadedTrack track : this.tracks){
                writer.write(track.toString());
                writer.write('\n');
            }
        } catch (FileNotFoundException e) {
            log.log(BotLog.TYPE.ERROR, "Unable to find file to write playlist: "+this.name);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the playlist from a file with the playlist's name.
     */
    public void load(){
        String path = System.getProperty("user.home")+"/.handiebot/playlist/"+name.replace(" ", "_")+".txt";
        log.log(BotLog.TYPE.INFO, "Loading playlist from: "+path);
        File playlistFile = new File(path);
        if (playlistFile.exists()){
            try {
                List<String> lines = Files.readAllLines(Paths.get(playlistFile.toURI()));
                int trackCount = Integer.parseInt(lines.remove(0));
                this.name = name;
                this.tracks = new ArrayList<>(trackCount);
                for (int i = 0; i < trackCount; i++){
                    String[] words = lines.remove(0).split(" / ");
                    this.tracks.add(new UnloadedTrack(words[0], words[1], Long.parseLong(words[2])));
                }
            } catch (IOException e) {
                log.log(BotLog.TYPE.ERROR, "IOException while loading playlist ["+this.name+"]. "+e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.log(BotLog.TYPE.ERROR, "The playlist ["+this.name+"] does not exist.");
        }
    }

    /**
     * Returns a list of all playlists, or essentially all playlist files.
     * @return A list of all playlists.
     */
    public static List<String> getAvailablePlaylists(){
        File playlistFolder = new File(System.getProperty("user.home")+"/.handiebot/playlist");
        List<String> names = new ArrayList<String>(Arrays.asList(playlistFolder.list()));
        for (int i = 0; i < names.size(); i++){
            String name = names.get(i);
            name = name.replace(".txt", "");
            name = name.replace("_", " ");
            names.set(i, name);
        }
        return names;
    }

    /**
     * Returns true if a playlist exists.
     * @param name The name of the playlist.
     * @return True if the playlist exists.
     */
    public static boolean playlistExists(String name){
        List<String> names = getAvailablePlaylists();
        for (String n : names){
            if (n.equals(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("Playlist: "+this.getName()+'\n');
        if (this.getTrackCount() == 0){
            sb.append("There are no songs in this playlist.");
        } else {
            for (int i = 0; i < this.getTrackCount(); i++) {
                sb.append(i + 1).append(". ").append(this.tracks.get(i).getTitle()).append(" ").append(this.tracks.get(i).getFormattedDuration()).append("\n");
            }
        }
        return sb.toString();
    }

}
