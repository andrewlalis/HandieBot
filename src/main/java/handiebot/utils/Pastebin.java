package handiebot.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Lalis
 * Class to easily paste to pastebin.
 */
public class Pastebin {

    private static String PASTEBIN_KEY = "769adc01154922ece448cabd7a33b57c";

    public static String paste(String title, String content){
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://www.pastebin.com/api/api_post.php");

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("api_dev_key", PASTEBIN_KEY));
        params.add(new BasicNameValuePair("api_option", "paste"));
        params.add(new BasicNameValuePair("api_paste_code", content));
        params.add(new BasicNameValuePair("api_paste_private", "0"));
        params.add(new BasicNameValuePair("api_paste_name", title));
        params.add(new BasicNameValuePair("api_paste_expire_date", "10M"));
        params.add(new BasicNameValuePair("api_user_key", ""));

        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null){
                try (InputStream in = entity.getContent()){
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(in, writer, "UTF-8");
                    return writer.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
