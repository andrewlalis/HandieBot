package handiebot.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.base.Joiner;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static handiebot.HandieBot.APPLICATION_NAME;
import static handiebot.utils.MessageUtils.addReaction;
import static handiebot.utils.MessageUtils.sendMessage;

/**
 * @author Andrew Lalis
 * Class to query Youtube Data API for results to searches, and return these in a nice list.
 */
public class YoutubeSearch {
//TODO: Externalize Strings
    private static final String KEY =  "AIzaSyAjYuxCYBCuZCNvW4w573LQ-jw5UKL64G8";
    private static final int NUMBER_OF_VIDEOS_RETURNED = 5;

    public static final String WATCH_URL = "https://www.youtube.com/watch?v=";

    private static final File DATA_STORE_DIR = new File(FileUtil.getDataDirectory()+"googleData/");
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY);


    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            HTTP_TRANSPORT = null;
        }
    }

    /**
     * Create an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = YoutubeSearch.class.getClassLoader().getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * Build and return an authorized API client service, such as a YouTube
     * Data API client service.
     * @return an authorized API client service
     * @throws IOException
     */
    public static YouTube getYouTubeService() throws IOException {
        Credential credential = authorize();
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Query Youtube Data API for a list of videos matching a given string.
     * @param searchString The string to use for the search.
     * @return A List of SearchResult objects which contain data about each video.
     */
    public static List<Video> query(String searchString){
        try {
            YouTube youtube = getYouTubeService();
            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(KEY);
            search.setQ(searchString);
            search.setType("video");
            search.setFields("items(id/videoId)");
            search.setMaxResults((long)NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> results = searchResponse.getItems();
            List<String> videoIds = new ArrayList<>();

            if (results != null){
                for (SearchResult searchResult : results){
                    videoIds.add(searchResult.getId().getVideoId());
                }
                Joiner stringJoiner = Joiner.on(',');
                String videosId = stringJoiner.join(videoIds);
                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet,statistics,contentDetails").setId(videosId);
                VideoListResponse listResponse = listVideosRequest.execute();

                return listResponse.getItems();

            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Creates an embed object to display.
     * @param results The list of videos to use to generate an embed object.
     * @return A fully assembled embedded object.
     */
    public static EmbedObject createEmbed(List<Video> results){
        EmbedBuilder builder = new EmbedBuilder();
        if (results != null) {
            builder.withTitle("Showing the first " + NUMBER_OF_VIDEOS_RETURNED + " results from YouTube.com");
            builder.withColor(Color.red);
            for (int i = 0; i < results.size(); i++) {
                Video video = results.get(i);
                String views = NumberFormat.getNumberInstance(Locale.US).format(video.getStatistics().getViewCount());
                String duration = video.getContentDetails().getDuration()
                        .replace("PT", "")
                        .replace("H", ":")
                        .replace("M", ":")
                        .replace("S", "");
                String[] components = duration.split(":");
                int hours, minutes, seconds;
                String formattedTime = "Unknown";
                if (components.length == 2){
                    minutes = Integer.parseInt(components[0]);
                    seconds = Integer.parseInt(components[1]);
                    formattedTime = String.format("%d:%02d", minutes, seconds);
                } else if (components.length == 3){
                    hours = Integer.parseInt(components[0]);
                    minutes = Integer.parseInt(components[1]);
                    seconds = Integer.parseInt(components[2]);
                    formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
                }
                String channelName = video.getSnippet().getChannelTitle();
                double likeDislikeRatio = (double)video.getStatistics().getLikeCount().longValue() / (double)video.getStatistics().getDislikeCount().longValue();
                builder.appendField(MessageUtils.getNumberEmoji(i+1) + video.getSnippet().getTitle(),
                        ":signal_strength: " + views +
                                "\t:watch: " + formattedTime +
                                "\t:copyright: "+ channelName +
                                "\t:arrow_up_down: "+ String.format("%.2f", likeDislikeRatio) +
                                "\n"+ WATCH_URL + video.getId(),
                        false);
            }
            builder.withFooterText("Please add a reaction to select a song, or cancel. Choice times out in 30 seconds.");
        }
        return builder.build();
    }

    /**
     * Displays the first five results from a youtube search, and adds reactions so that a user may choose an option.
     * @param videos The list of videos, returned from calling {@code query}.
     * @param channel The channel to display the dialog in.
     */
    public static IMessage displayChoicesDialog(List<Video> videos, IChannel channel){
        EmbedObject e = YoutubeSearch.createEmbed(videos);
        IMessage message = sendMessage(e, channel);
        List<String> urls = new ArrayList<>(videos.size());
        videos.forEach((video) -> urls.add(WATCH_URL + video.getId()));
        addReaction(message, ":one:");
        addReaction(message, ":two:");
        addReaction(message, ":three:");
        addReaction(message, ":four:");
        addReaction(message, ":five:");
        addReaction(message, ":x:");
        return message;
    }

}
