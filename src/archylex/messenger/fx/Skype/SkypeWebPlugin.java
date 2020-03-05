package archylex.messenger.fx.Skype;

import archylex.messenger.fx.Messenger.Person;
import archylex.messenger.fx.Messenger.clMessage;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkypeWebPlugin {
    private static final String client_id = "578134";
    private static final String redirect_uri = "https://web.skype.com";
    private static final String site_name = "lw.skype.com";

    private static String login;
    private static String password;
    private static String skypetoken;
    private static Person myProfile;
    private static Map<String, String> registrationToken;

    public SkypeWebPlugin(String login, String password) {
        this.login = login;
        this.password = password;

        try {
            registrationToken = getRegToken(getSkypeToken(ppSecure(oAuth())));

            pingActive(registrationToken);

            Subscriptions(registrationToken);

            subscriptionsPoll(registrationToken);

            myProfile = getMyProfile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> oAuth() throws Exception {
        String url = "https://login.skype.com/login/oauth/microsoft";
        String query = "client_id=" + client_id + "&redirect_uri=" + redirect_uri;

        Map<String, String> result = new LinkedHashMap<>();
        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url + "?" + query);

        HttpResponse response = client.execute(get);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Header[] headers = response.getAllHeaders();

            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                    Pattern pattern = Pattern.compile("((?:MSPOK|MSPRequ))=(.*?);");
                    Matcher matcher = pattern.matcher(header.getValue());
                    while (matcher.find()) {
                        result.put(matcher.group(1), matcher.group(2));
                    }
                }
            }

            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());

            Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"PPFT\".*value=\"(.*?)\"/>");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                result.put("PPFT", matcher.group(1));
            }

            return result;
        } else {
            System.out.println("oAuth has troubles");
        }

        return null;
    }

    private static String ppSecure(Map<String, String> params) throws IOException {
        String url = "https://login.live.com/ppsecure/post.srf";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DefaultHttpClient client = new DefaultHttpClient();

        String query = "wa=wsignin1.0&wp=MBI_SSL&wreply=https://lw.skype.com/login/oauth/proxy?client_id=" +
                client_id + "&site_name=" + site_name + "&redirect_uri=" + redirect_uri;

        HttpPost post = new HttpPost(url + "?" + query);

        String cookie = "MSPOK=" + params.get("MSPOK") + "; MSPRequ=" + params.get("MSPRequ") + "; CkTst=" + timestamp.getTime();
        post.setHeader("Cookie", cookie);

        List<NameValuePair> form = new ArrayList<NameValuePair>();
        form.add(new BasicNameValuePair("login", login));
        form.add(new BasicNameValuePair("passwd", password));
        form.add(new BasicNameValuePair("PPFT", params.get("PPFT")));
        post.setEntity(new UrlEncodedFormEntity(form));

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());

            Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"t\" id=\"t\" value=\"(.*?)\">");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                return matcher.group(1);
            }
        } else {
            System.out.println("ppSecure has troubles");
        }

        return null;
    }

    private static Map<String, String> getSkypeToken(String t) throws IOException {
        String url = "https://login.skype.com/login/microsoft";
        Map<String, String> result = new LinkedHashMap<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DefaultHttpClient client = new DefaultHttpClient();

        String query = "client_id=" + client_id + "&redirect_uri=" + redirect_uri;

        HttpPost post = new HttpPost(url + "?" + query);

        List<NameValuePair> form = new ArrayList<NameValuePair>();
        form.add(new BasicNameValuePair("client_id", client_id));
        form.add(new BasicNameValuePair("redirect_uri", redirect_uri));
        form.add(new BasicNameValuePair("oauthPartner", "999"));
        form.add(new BasicNameValuePair("site_name", site_name));
        form.add(new BasicNameValuePair("t", t));
        post.setEntity(new UrlEncodedFormEntity(form));

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());

            //Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"((?:skypetoken|expires_in))\" value=\"(.*?)\"/>");
            Pattern pattern = Pattern.compile("<input type=\"hidden\" name=\"(skypetoken)\" value=\"(.*?)\"/>");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                result.put(matcher.group(1), matcher.group(2));
                skypetoken = matcher.group(2);
            }

            return result;
        } else {
            System.out.println("getSkypeToken has troubles");
        }

        return null;
    }

    private static Map<String, String> getRegToken(Map<String, String> params) throws IOException {
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints";
        Map<String, String> result = new LinkedHashMap<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String stringTimeStamp = String.valueOf(timestamp.getTime());
        String hash = SkypeCrypt.getMac256Hash(stringTimeStamp);

        DefaultHttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url);

        post.setHeader("Accept", "application/json");
        post.setHeader("LockAndKey", "appId=msmsgs@msnmsgr.com; time=" + stringTimeStamp + "; lockAndKeyResponse=" + hash);
        post.setHeader("Authentication", "skypetoken=" + params.get("skypetoken"));

        Map< String, Object >jsonValues = new HashMap< String, Object >();
        jsonValues.put("endpointFeatures", "Agent");
        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            Header[] headers = response.getAllHeaders();

            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Set-RegistrationToken")) {
                    Pattern pattern = Pattern.compile("registrationToken=(.*?);.*endpointId=\\{(.*?)}$");
                    Matcher matcher = pattern.matcher(header.getValue());

                    while (matcher.find()) {
                        result.put("registrationToken", matcher.group(1));
                        result.put("endpointId", "%7B" + matcher.group(2) + "%7D");
                        return result;
                    }
                }
            }
        } else {
            System.out.println("getRegToken has troubles");
        }

        return null;
    }

    public static void pingActive(Map<String, String> params) throws Exception {
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints/" +
                params.get("endpointId") + "/active";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Accept", "application/json");
        post.setHeader("RegistrationToken", "registrationToken=" + params.get("registrationToken"));

        Map< String, Object >jsonValues = new HashMap< String, Object >();
        jsonValues.put("timeout", "12");
        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            System.out.println("ping active ... OK");
        } else {
            System.out.println("pingActive has troubles");
        }
    }

    public static void Subscriptions(Map<String, String> params) throws Exception {
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints/" +
                params.get("endpointId") + "/subscriptions";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Accept", "application/json");
        post.setHeader("RegistrationToken", "registrationToken=" + params.get("registrationToken"));

        String[] interestedResources = { "/v1/users/ME/conversations/ALL/properties",
                "/v1/users/ME/conversations/ALL/messages",
                "/v1/threads/ALL",
                "/v1/users/ME/contacts/ALL"};

        Map< String, Object >jsonValues = new HashMap< String, Object >();
        jsonValues.put("channelType", "HttpLongPoll");
        jsonValues.put("template", "raw");
        jsonValues.put("interestedResources", interestedResources);
        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            System.out.println("Subscriptions ... OK");
        } else {
            System.out.println("Subscriptions has troubles");
        }
    }

    // JSON Response
    public static String subscriptionsPoll(Map<String, String> params) throws Exception {
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints/" +
                params.get("endpointId") + "/subscriptions/0/poll";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Accept", "application/json");
        post.setHeader("RegistrationToken", "registrationToken=" + params.get("registrationToken"));

        Map< String, Object >jsonValues = new HashMap< String, Object >();
        jsonValues.put("endpointFeatures", "Agent");
        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
        } else {
            System.out.println("subscriptionsPoll has troubles");
        }

        return null;
    }

    public static List<Person> getContactList() throws Exception {
        String url = "https://contacts.skype.com/contacts/v2/users/" + myProfile.getSNID() + "/contacts";
        List<Person> result = new LinkedList<>();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        get.setHeader("Accept", "application/json");
        get.setHeader("X-SkypeToken", skypetoken);

        HttpResponse response = client.execute(get);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
            JSONObject json = new JSONObject(content);

            JSONArray arr = json.getJSONArray("contacts");
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject profile = arr.getJSONObject(i).getJSONObject("profile");
                String urla = profile.has("avatar_url") ? profile.getString("avatar_url") : null;

                Person person = new Person(arr.getJSONObject(i).getString("display_name"), "",
                        arr.getJSONObject(i).getString("mri"), "Skype", urla);

                result.add(person);
            }

            return result;
        } else {
            System.out.println("getContactList has troubles");
        }

        return null;
    }

    public static Person getMyProfile() throws Exception {
        String url = "https://api.skype.com/users/self/profile";
        Person result = null;

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        get.setHeader("Accept", "application/json");
        get.setHeader("X-SkypeToken", skypetoken);

        HttpResponse response = client.execute(get);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());

            JSONObject json = new JSONObject(content);

            String urla = json.has("avatarUrl") ? json.getString("avatarUrl") : null;

            return new Person(json.getString("firstname"), json.getString("lastname"),
                    json.getString("username"), "Skype", urla);
        } else {
            System.out.println("getProfile has troubles");
        }

        return null;
    }

    public static void sendMessage(String content, String recipientID) throws Exception {
        String xECSetag = "swx-skype.com";
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/" + recipientID + "/messages?x-ecs-etag=" + xECSetag;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String stringTimeStamp = String.valueOf(timestamp.getTime());

        DefaultHttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url);

        post.setHeader("Accept", "application/json");
        post.setHeader("RegistrationToken", "registrationToken=" + registrationToken.get("registrationToken"));


        Map< String, Object >jsonValues = new HashMap< String, Object >();
        jsonValues.put("OriginalArrivalTime", stringTimeStamp); //1451606400000
        jsonValues.put("content", content);
        jsonValues.put("contenttype", "text");
        jsonValues.put("messagetype", "RichText");
        JSONObject json = new JSONObject(jsonValues);
        StringEntity entity = new StringEntity(json.toString(), "UTF8");
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            System.out.println("sendMessage ... OK");
        } else {
            System.out.println("sendMessage has troubles");
        }
    }

    public static List<clMessage> getMessagesFromUserID(String userID) throws Exception {
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/" + userID + "/messages";
        List<clMessage> result = new LinkedList<>();

        String params = "startTime=0&view=msnp24Equivalent&targetType=Passport%7CSkype%7CLync%7CThread";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url + "?" + params);

        get.setHeader("Accept", "application/json");
        get.setHeader("RegistrationToken", "registrationToken=" + registrationToken.get("registrationToken"));

        HttpResponse response = client.execute(get);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
            JSONObject json = new JSONObject(content);

            JSONArray arr = json.getJSONArray("messages");
            for (int i = 0; i < arr.length(); i++)
            {
                Pattern pattern = Pattern.compile("^https:.*/(.*?)$");
                Matcher matcher = pattern.matcher(arr.getJSONObject(i).getString("from"));
                String from = null;

                if (matcher.find()) {
                    from = matcher.group(1);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                Date date = dateFormat.parse(arr.getJSONObject(i).getString("originalarrivaltime"));
                Timestamp timestamp = new Timestamp(date.getTime());

                clMessage msg = new clMessage("Skype", arr.getJSONObject(i).getString("conversationid"),
                        from, arr.getJSONObject(i).getString("content"), timestamp);

                result.add(msg);
            }

            Collections.reverse(result);
            return result;
        } else {
            System.out.println("getMessageFromUserID has troubles");
        }

        return null;
    }
}
