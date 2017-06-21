package handiebot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andrew Lalis
 * A Playlist is a list of AudioTracks which a track scheduler can pull from to create a queue filled with songs. The
 * playlist is persistent, i.e. it is saved into a file.
 */
public class Playlist {

    private String name;
    private long creatorUID;

    List<AudioTrack> tracks;

    /**
     * Creates an empty playlist template.
     * @param name The name of the playlist.
     * @param creatorUID The ID of the user who created it.
     */
    public Playlist(String name, long creatorUID){
        this.name = name;
        this.creatorUID = creatorUID;
        this.tracks = new ArrayList<>();
    }

    /**
     * Creates a playlist from a file with the given name.
     * @param name The name of the file.
     */
    public Playlist(String name){
        this.name = name;
        this.load();
    }

    public String getName(){
        return this.name;
    }

    public long getCreatorUID(){
        return this.creatorUID;
    }

    public List<AudioTrack> getTracks(){
        return this.tracks;
    }

    /**
     * Adds a track to the end of the playlist.
     * @param track The track to add.
     */
    public void addTrack(AudioTrack track){
        this.tracks.add(track);
    }

    /**
     * Removes a track from the playlist.
     * @param track The track to remove.
     */
    public void removeTrack(AudioTrack track){
        this.tracks.remove(track);
    }

    /**
     * Returns the next track, i.e. the first one in the list, and removes it from the internal list.
     * @return The AudioTrack that should be played next.
     */
    public AudioTrack getNextTrackAndRemove(boolean shouldShuffle){
        if (this.tracks.isEmpty()){
            return null;
        }
        return this.tracks.remove((shouldShuffle ? getShuffledIndex(this.tracks.size()) : 0));
    }

    /**
     * Returns the next track to be played, and re-adds it to the end of the playlist, as it would do in a loop.
     * @return
     */
    public AudioTrack getNextTrackAndRequeue(boolean shouldShuffle){
        if (this.tracks.isEmpty()){
            return null;
        }
        AudioTrack track = this.tracks.remove((shouldShuffle ? getShuffledIndex(this.tracks.size()) : 0));
        this.tracks.add(track);
        return track;
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
        int trueLength = listLength - (int)threshold*listLength;
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
                System.out.println("Unable to make directory: "+playlistDir.getPath());
                return;
            }
        }
        File playlistFile = new File(playlistDir.getPath()+"/"+this.name.replace(" ", "_")+".txt");
        System.out.println("Saving playlist to: "+playlistFile.getAbsolutePath());
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playlistFile)))){
            writer.write(this.name+'\n');
            writer.write(Long.toString(this.creatorUID)+'\n');
            writer.write(Integer.toString(this.tracks.size())+'\n');
            for (AudioTrack track : this.tracks){
                writer.write(track.getInfo().uri);
                writer.write('\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the playlist from a file with the playlist's name.
     */
    public void load(){//TODO Make load work!!!
        String path = System.getProperty("user.home")+"/.handiebot/playlist/"+this.name.replace(" ", "_")+".txt";
        File playlistFile = new File(path);
        if (playlistFile.exists()){
            try {
                List<String> lines = Files.readAllLines(Paths.get(playlistFile.toURI()));
                this.name = lines.remove(0);
                this.creatorUID = Long.parseLong(lines.remove(0));
                int trackCount = Integer.parseInt(lines.remove(0));
                this.tracks = new ArrayList<>(trackCount);
                AudioPlayerManager pm = new DefaultAudioPlayerManager();
                for (int i = 0; i < trackCount; i++){
                    System.out.println("Loading item "+i);
                    String url = lines.remove(0);
                    pm.loadItem(url, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack audioTrack) {
                            System.out.println("Added track");
                            tracks.add(audioTrack);
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist audioPlaylist) {
                            System.out.println("Playlist loaded.");
                            //Do nothing. This should not happen.
                        }

                        @Override
                        public void noMatches() {
                            System.out.println("No matches for: "+url);
                            //Do nothing. This should not happen.
                        }

                        @Override
                        public void loadFailed(FriendlyException e) {
                            System.out.println("Load failed: "+e.getMessage());
                            //Do nothing. This should not happen.
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
