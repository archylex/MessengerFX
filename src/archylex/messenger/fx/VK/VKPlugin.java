package archylex.messenger.fx.VK;

import archylex.messenger.fx.Messenger.Person;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.friends.responses.GetResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VKPlugin {
    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    private static final VkApiClient vk = new VkApiClient(new HttpTransportClient());
    private static boolean authorized = false;
    private static UserActor actor;
    private static Integer userID;
    private static Integer ts;
    private static int maxMsgId = -1;

    public VKPlugin(String login, String password) {
        try {
            Map<String, String> idt = parseParamsFromURL(doLogin(login, password));

            if (idt.get("access_token") != null && idt.get("user_id") != null) {
                userID = Integer.valueOf(idt.get("user_id"));
                actor = new UserActor(userID, idt.get("access_token"));
                authorized = true;
                ts = vk.messages().getLongPollServer(actor).execute().getTs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Stage vstage = new Stage();
        final WebView view = new WebView();
        final WebEngine engine = view.getEngine();

        engine.load(getVkAuthUrl(readProperties()));

        engine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.startsWith(REDIRECT_URL)) {
                    Map<String, String> idt = parseParamsFromURL(newValue);

                    if (idt.get("access_token") != null && idt.get("user_id") != null) {
                        userID = Integer.valueOf(idt.get("user_id"));
                        actor = new UserActor(userID, idt.get("access_token"));
                        authorized = true;

                        try {
                            ts = vk.messages().getLongPollServer(actor).execute().getTs();
                        } catch (ApiException e) {
                            e.printStackTrace();
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    }

                    vstage.close();
                }
            }
        });

        vstage.setTitle("VK Autorization");
        vstage.setScene(new Scene(view, 655, 349));
        vstage.showAndWait();*/
    }

    private static String getVkAuthUrl(Map<String, String> props) {
        String res = null;

        if (props.get("app_id") != null && props.get("v") != null)
            res = "https://oauth.vk.com/authorize?client_id=" + props.get("app_id") +
                    "&display=page&redirect_uri=" + REDIRECT_URL +
                    "&scope=messages,friends,offline&response_type=token&v=" + props.get("v");

        return res;
    }

    private static Map<String, String> readProperties() {
        Properties prop = new Properties();
        Map<String, String> res = new HashMap();

        try {
            String path = new File(".").getCanonicalPath() + "/src/resources/vk.properties";
            prop.load(new FileInputStream(path));
            res.put("app_id", prop.getProperty("app_id"));
            res.put("v", prop.getProperty("v"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to read file 'vk.properties'");
        }

        return res;
    }

    private static Map<String, String> parseParamsFromURL(String url) {
        Map<String, String> result = new LinkedHashMap<String, String>();

        url = url.substring(url.indexOf("#")+1);
        url.trim();

        String[] params = url.split("&");

        for (String param : params) {
            int i = param.indexOf("=");
            result.put(param.substring(0, i), param.substring(i + 1));
        }

        return result;
    }

    public static Person getUser() {
        Person person = null;

        try {
            List<UserXtrCounters> getResponse = vk.users().get(actor).userIds(userID.toString()).fields(Fields.PHOTO_50).execute();
            UserXtrCounters user = getResponse.get(0);
            person = new Person(user.getFirstName(), user.getLastName(), userID.toString(), "VK", user.getPhoto50().toString());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return person;
    }

    public static Person getPersonByID(Integer id) {
        List<UserXtrCounters> getFriendResponse = null;

        try {
            getFriendResponse = vk.users().get(actor).userIds(id.toString()).fields(Fields.PHOTO_50).execute();
            UserXtrCounters friend = getFriendResponse.get(0);
            return new Person(friend.getFirstName(), friend.getLastName(),id.toString(), "VK", friend.getPhoto50().toString());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Person>  getFriends() {
        List<Person> result = new LinkedList<>();

        try {
            GetResponse getResponse = vk.friends().get(actor).execute();
            List<Integer> friends = getResponse.getItems();

            for (Integer element : friends) {
                result.add(getPersonByID(element));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean isAuthorized() {
        return authorized;
    }

    public Message getMessage() throws ClientException, ApiException {
        MessagesGetLongPollHistoryQuery query = vk.messages().getLongPollHistory(actor).ts(ts);

        if (maxMsgId > 0) {
            query.maxMsgId(maxMsgId);
        }

        List<Message> messages = query.execute().getMessages().getItems();

        if (!messages.isEmpty()) {
            try {
                ts = vk.messages().getLongPollServer(actor).execute().getTs();

                if (!messages.get(0).isOut()) {
                    int messageId = messages.get(0).getId();

                    if (messageId > maxMsgId){
                        maxMsgId = messageId;
                    }

                    return messages.get(0);
                }
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void sendMessage(Integer idc, String msg) {
        try {
            vk.messages().send(actor).userId(idc).message(msg).randomId(generate64bits().nextInt()).execute();
            getMessage();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private SecureRandom generate64bits() {
        SecureRandom sr = new SecureRandom();
        byte[] randbytes = new byte[8];
        sr.nextBytes(randbytes);
        return sr;
    }

    private void getConversations() {
        vk.messages().getConversations(actor);
    }







    private static CookieStore cookieStore = new BasicCookieStore();

    private static String doLogin(String login, String password) throws IOException {
        //String url = "https://oauth.vk.com/authorize";
        //String query = "?client_id=" + "7034196" + "&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=messages,friends,offline&response_type=token&v=5.100";

        //url += query;

        String url = getVkAuthUrl(readProperties());

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();


        CloseableHttpClient client = HttpClients
                .custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();


        HttpPost post = new HttpPost(url);

        HttpResponse response = client.execute(post);

        String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());

        Map<String, String> result = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"((?:_origin|ip_h|lg_h|to))\" value=\"(.*?)\"");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            result.put(matcher.group(1), matcher.group(2));
        }

        result.put("email", login);
        result.put("pass", password);
        result.put("expire", "0");

        return doLoginSoft(result);
    }

    private static String doLoginSoft(Map<String, String> params) throws IOException {
        String url = "https://login.vk.com/?act=login&soft=1";

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        CloseableHttpClient client = HttpClients
                .custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();

        HttpPost post = new HttpPost(url);

        List<NameValuePair> urlParams = new ArrayList<>();

        for (Map.Entry<String, String> p : params.entrySet()) {
            urlParams.add(new BasicNameValuePair(p.getKey(), p.getValue()));
        }

        post.setEntity(new UrlEncodedFormEntity(urlParams));

        HttpClientContext context = HttpClientContext.create();

        HttpResponse response = client.execute(post, context);

        List<URI> redirectURIs = context.getRedirectLocations();
        if (redirectURIs != null && !redirectURIs.isEmpty()) {
            URI finalURI = redirectURIs.get(redirectURIs.size() - 1);
            return finalURI.toString();
        }

        return null;
    }
}
