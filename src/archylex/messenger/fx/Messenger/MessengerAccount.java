package archylex.messenger.fx.Messenger;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessengerAccount {
    private static MessengerAccount p_mAccount = null;

    private static Map<String, String> id;
    private static Map<String, String> name;
    private static Map<String, String> photo_url;

    public static MessengerAccount getInstance() {
        if (p_mAccount == null)
            p_mAccount = new MessengerAccount();

        return p_mAccount;
    }

    private MessengerAccount() {
        id = new LinkedHashMap<>();
        name = new LinkedHashMap<>();
        photo_url = new LinkedHashMap<>();
    }

    public void setID(String snname, String userid) {
        this.id.put(snname, userid);
    }

    public static String getID(String snname) {
        return id.get(snname);
    }

    public void setName(String snname, String username) {
        this.name.put(snname, username);
    }

    public static String getName(String snname) {
        return name.get(snname);
    }

    public void setPhotoURL(String snname, String userurl) {
        this.photo_url.put(snname, userurl);
    }

    public static String getPhotoURL(String snname) {
        return photo_url.get(snname);
    }
}
